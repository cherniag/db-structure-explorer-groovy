package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class UniversalParser extends IParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversalParser.class);
    private PeriodFormatter durationFormatter;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    protected SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    public UniversalParser(String root) throws FileNotFoundException {
        super(root);

        durationFormatter = new PeriodFormatterBuilder()
                .appendHours().appendSuffix(":")
                .appendMinutes().appendSuffix(":")
                .appendSeconds().toFormatter();

        LOGGER.info("Universal parser loading from " + root);
    }

    protected Map<String, DropTrack> loadXml(String drop, String code, Map<String, List<DropAssetFile>> fulfillmentFiles) {

        SAXBuilder builder = new SAXBuilder();
        builder.setEntityResolver(new DtdLoader());

        LOGGER.info("Scanning " + root + "/" + code + "_" + drop + " ");
        File productDir = new File(root + "/" + code + "_" + drop);
        File[] files = productDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".xml")) {
                try {
                    LOGGER.debug("Loading " + file.getPath());

                    Document document = builder.build(file);
                    Element product = document.getRootElement();

                    return parseProductMetadata(code, fulfillmentFiles, product);
                } catch (IOException io) {
                    LOGGER.error(io.getMessage());
                } catch (JDOMException jdomex) {
                    LOGGER.error(jdomex.getMessage());
                }
            }
        }

        return null;
    }

    private Map<String, DropTrack> parseProductMetadata(String code, Map<String, List<DropAssetFile>> fulfillmentFiles, Element product) {
        Map<String, DropTrack> result = new HashMap<String, DropTrack>();

        String country = product.getChildText("territory");
        String provider = product.getChildText("prd_label_name");
        String releaseDate = product.getChildText("release_date");
        String prdExplicit = product.getChildText("prd_explicit");
        String copyright = product.getChildText("c_line");
        String genre = product.getChildText("genre");
        DropTrack.Type type = parseType(product);
        Date startDate = parseStartDate(releaseDate);
        String year = parseYear(startDate);

        List<Element> tracks = product.getChild("tracks").getChildren("track");
        for (Element track : tracks) {
            String isrc = track.getAttributeValue("isrc");

            DropTrack data = result.get(isrc);
            if (data == null) {
                data = new DropTrack();
                result.put(isrc, data);
            }

            data.type = type;
            data.productCode = code;
            data.productId = code;
            data.physicalProductId = code;
            data.copyright = copyright;
            data.genre = genre;
            data.year = year;
            data.isrc = isrc;
            data.explicit = parseExplicit(prdExplicit, track);
            data.artist = parseArtist(track);
            data.title = track.getChildText("track_title");
            data.subTitle = track.getChildText("track_version_title");
            data.xml = new XMLOutputter().outputString(track);

            parseTerritories(country, provider, startDate, track, data);

            if (fulfillmentFiles.containsKey(isrc))
                data.files.addAll(fulfillmentFiles.get(isrc));

            if (fulfillmentFiles.containsKey(null))
                data.files.addAll(fulfillmentFiles.get(null));
        }

        return result;
    }

    private void parseTerritories(String country, String provider, Date startDate, Element track, DropTrack data) {
        Element trackPricing = track.getChild("track_pricing");
        String isrc = track.getAttributeValue("isrc");

        DropTerritory territoryData = DropTerritory.getTerritory(country, data.territories);
        territoryData.country = country;
        String territoryLabel = track.getChildText("track_label");
        territoryData.label = territoryLabel;
        territoryData.reportingId = isrc;
        territoryData.distributor = provider;
        territoryData.startdate = startDate;
        territoryData.priceCode = trackPricing.getChildText("current_price_code");
    }

    private boolean parseExplicit(String prdExplicit, Element track) {
        String trackExplicit = track.getChildText("track_explicit");
        boolean explicit = "Y".equals(prdExplicit);

        return !"".equals(trackExplicit) ? "Y".equals(trackExplicit) : explicit;
    }

    private DropTrack.Type parseType(Element product) {
        String type = product.getChildText("type");
        return "new".equalsIgnoreCase(type) ? DropTrack.Type.INSERT : DropTrack.Type.UPDATE;
    }

    private Date parseStartDate(String releaseDate) {
        Date startDate = null;

        try {
            if (releaseDate != null) {
                startDate = dateFormat.parse(releaseDate);
            }
        } catch (ParseException e) {
        }

        return startDate;
    }

    private String parseYear(Date startDate) {
        String year = null;

        if (startDate != null) {
            year = yearFormat.format(startDate);
        }

        return year;
    }

    private String parseArtist(Element track) {
        List<Element> artists = track.getChild("track_contributors").getChildren("artist_name");
        boolean firstArtist = true;
        String artist = "";
        for (Element artistElement : artists) {
            if (firstArtist) {
                firstArtist = false;
            } else {
                artist += ", ";
            }
            artist += artistElement.getText();
        }

        return artist;
    }

    public Map<String, DropTrack> ingest(DropData drop) {

        Map<String, DropTrack> result = new HashMap<String, DropTrack>();
        try {
            File fulfillment = new File(root + "/Delivery_Messages/fulfillment_" + drop.name + ".xml");
            SAXBuilder builder = new SAXBuilder();
            builder.setEntityResolver(new DtdLoader());
            LOGGER.info("Loading " + fulfillment.getPath());

            try {
                Document document = (Document) builder.build(fulfillment);
                Element rootNode = document.getRootElement();

                List<Element> products = rootNode.getChild("products").getChildren("product");
                for (Element product : products) {
                    result.putAll(parseProduct(drop.name, product));
                }
            } catch (IOException io) {
                LOGGER.error(io.getMessage());
            } catch (JDOMException jdomex) {
                LOGGER.error(jdomex.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return result;
    }

    private Map<String, DropTrack> parseProduct(String dropId, Element product) {
        String code = product.getChildText("upc");
        String base = getResourceBase(code, dropId);

        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();

        List<Element> assets = new ArrayList<Element>(product.getChild("assets").getChildren("asset"));
        assets.addAll(product.getChild("assets").getChildren("track"));

        for (Element asset : assets) {
            DropAssetFile assetFile = parseAssetFile(asset);

            if (assetFile.type != null) {
                assetFile.file = base + assetFile.file;

                List<DropAssetFile> assetFiles = fulfillmentFiles.get(assetFile.isrc);
                if (assetFiles == null) {
                    assetFiles = new ArrayList<DropAssetFile>();
                    fulfillmentFiles.put(assetFile.isrc, assetFiles);
                }

                assetFiles.add(assetFile);
            }
        }

        return loadXml(dropId, code, fulfillmentFiles);
    }

    private DropAssetFile parseAssetFile(Element assetFileEl) {
        DropAssetFile assetFile = new DropAssetFile();

        String isrc = assetFileEl.getChildText("isrc");
        String type = assetFileEl.getAttributeValue("type");
        String subType = assetFileEl.getChildText("subtype");

        Element file = assetFileEl.getChild("files").getChild("file");
        String fileType = file.getChildText("file_type");
        boolean isExcerpt = file.getChild("excerpt") != null;

        assetFile.md5 = file.getChildText("checksum");
        assetFile.file = file.getChildText("file_name");
        assetFile.type = getAssetType(isExcerpt, fileType, type, subType);
        assetFile.isrc = assetFile.type == AssetFile.FileType.IMAGE ? null : isrc;
        assetFile.duration = parseDuration(assetFileEl);

        return assetFile;
    }

    private Integer parseDuration(Element assetFileEl) {
        String length = assetFileEl.getChildText("track_length");

        return length != null ? (int)durationFormatter.parsePeriod(length).toDurationFrom(new DateTime(0)).getMillis()
                              : null;
    }

    private AssetFile.FileType getAssetType(boolean isExcerpt, String fileType, String assetType, String assetSubType) {
        if ("mp3".equalsIgnoreCase(fileType)) {
            if (!isExcerpt)
                return AssetFile.FileType.DOWNLOAD;
        } else if ("mp4".equalsIgnoreCase(fileType)) {
            if ("Video".equals(assetType) && "Video".equals(assetSubType)) {
                if (!isExcerpt)
                    return AssetFile.FileType.VIDEO;
            } else if (!isExcerpt)
                return AssetFile.FileType.MOBILE;
            else
                return AssetFile.FileType.PREVIEW;
        } else if ("jpg".equalsIgnoreCase(fileType) && "Images".equals(assetType) && "Cover Art".equals(assetSubType)) {
            return AssetFile.FileType.IMAGE;
        }

        return null;
    }

    public String getResourceBase(String code, String drop) {
        return root + File.separator + code + "_" + drop + File.separator;
    }

    public void commit(DropData drop, boolean auto) {

        if (!auto) {
            File commitFile = new File(root + "/Delivery_Messages/" + drop.name + ".ack");
            try {
                commitFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File commitFile = new File(root + "/Delivery_Messages/auto_" + drop.name + ".ack");
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DropData> getDrops(boolean auto) {
        List<DropData> result = new ArrayList<DropData>();
        File deliveries = new File(root + "/Delivery_Messages");
        LOGGER.info("Checking manifests in " + root + "/Delivery_Messages");
        File[] fulfillmentFiles = deliveries.listFiles();
        for (File file : fulfillmentFiles) {
            if (file.getName().startsWith("delivery") && file.getName().endsWith(".xml")) {
                String order = file.getName().substring(file.getName().indexOf('_') + 1, file.getName().lastIndexOf('.'));
                File ackManual = new File(root + "/Delivery_Messages/" + order + ".ack");
                if (!auto) {
                    if (!ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());
                        result.add(drop);
                    }
                } else {
                    File ack = new File(root + "/Delivery_Messages/auto_" + order + ".ack");
                    if (!ack.exists() && !ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());
                        result.add(drop);
                    }
                }
            }
        }

        return result;
    }

}