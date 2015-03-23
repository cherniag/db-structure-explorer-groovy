package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.DtdLoader;
import mobi.nowtechnologies.server.trackrepo.ingest.IParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Locale.ENGLISH;

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

public class UniversalParser extends IParser {

    private final static String OLD_XML_NAMESPACE_PATTERN = "xmlns=\"http://www.digiplug.com/dsc/%s\"";
    private final static String NEW_XML_NAMESPACE_PATTERN = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xsi:noNamespaceSchemaLocation=\"http://www.digiplug.com/dsc/%s\"";
    private static final Logger LOGGER = LoggerFactory.getLogger(UniversalParser.class);
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", ENGLISH);
    protected SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private Pattern OLD_XML_NAMESPACE_REGEXP_PATTERN = Pattern.compile("xmlns=\"http://www.digiplug.com/dsc/(.*?)\"");
    private PeriodFormatter durationFormatter;

    public UniversalParser(String root) throws FileNotFoundException {
        super(root);

        durationFormatter = new PeriodFormatterBuilder().appendHours().appendSuffix(":").appendMinutes().appendSuffix(":").appendSeconds().toFormatter();

        LOGGER.info("Universal parser loading from {}", root);
    }

    public Map<String, DropTrack> loadXml(String drop, String code, Map<String, List<DropAssetFile>> fulfillmentFiles) {

        SAXBuilder builder = new SAXBuilder();
        builder.setEntityResolver(new DtdLoader());

        LOGGER.info("Scanning {}/{}_{}", root, code, drop);
        File productDir = new File(root + "/" + code + "_" + drop);
        File[] files = productDir.listFiles();
        Map<String, DropTrack> resultDropTracksWithMetadata = new HashMap<>();
        for (File file : files) {
            String name = file.getName();
            if ((!name.contains("DDEX")) && name.endsWith(".xml")) {
                try {
                    preprocess(file);

                    LOGGER.debug("Loading [{}]", file.getPath());

                    Document document = builder.build(file);
                    Element product = document.getRootElement();

                    addProductMetadata(code, fulfillmentFiles, product, resultDropTracksWithMetadata);
                }
                catch (IOException | JDOMException io) {
                    LOGGER.error(io.getMessage());
                }
            }
        }

        return resultDropTracksWithMetadata;
    }

    /*
     * The method is intended to replace input xml namespace
     * from: xmlns="http://www.digiplug.com/dsc/umgistd-1_4_5"
     * to: xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://www.digiplug.com/dsc/umgistd-1_4_5".
     *
     * Method takes into account that namespace version other than umgistd-1_4_5 could be be sent and properly replaces it.
     *
     * Xml namespace schema is needed to avoid parser failure.
     */
    private void preprocess(File file) throws IOException {
        LOGGER.debug("Replacing xml namespaces.");

        byte[] bytes = Files.readAllBytes(file.toPath());
        String inputData = new String(bytes);

        Matcher matcher = OLD_XML_NAMESPACE_REGEXP_PATTERN.matcher(inputData);

        while (matcher.find()) {
            String version = matcher.group(1);

            String outputData = inputData.replaceFirst(String.format(OLD_XML_NAMESPACE_PATTERN, version), String.format(NEW_XML_NAMESPACE_PATTERN, version));

            Files.write(file.toPath(), outputData.getBytes());
        }
    }

    private void addProductMetadata(String code, Map<String, List<DropAssetFile>> fulfillmentFiles, Element product, Map<String, DropTrack> resultDropTracks) {
        String country = product.getChildText("territory");
        String provider = product.getChildText("prd_label_name");
        String releaseDate = product.getChildText("release_date");
        String prdExplicit = product.getChildText("prd_explicit");
        String copyright = product.getChildText("c_line");
        String genre = product.getChildText("genre");
        DropTrack.Type type = parseType(product);
        Date startDate = parseStartDate(releaseDate);
        String year = parseYear(startDate);

        @SuppressWarnings("unchecked") List<Element> tracks = product.getChild("tracks").getChildren("track");
        for (Element track : tracks) {
            String isrc = track.getAttributeValue("isrc");

            DropTrack dropTrack = resultDropTracks.get(isrc);
            if (dropTrack == null) {
                dropTrack = new DropTrack();
                resultDropTracks.put(isrc, dropTrack);
            }

            dropTrack.type = type;
            dropTrack.productCode = code;
            dropTrack.productId = code;
            dropTrack.physicalProductId = code;
            dropTrack.copyright = copyright;
            dropTrack.genre = genre;
            dropTrack.year = year;
            dropTrack.isrc = isrc;
            dropTrack.explicit = parseExplicit(prdExplicit, track);
            dropTrack.artist = parseArtist(track);
            dropTrack.title = track.getChildText("track_title");
            dropTrack.subTitle = track.getChildText("track_version_title");
            dropTrack.xml = new XMLOutputter().outputString(track);
            dropTrack.album = product.getChildText("prd_title");

            parseTerritories(country, provider, startDate, track, dropTrack);

            insertIntoDropTrackDropAssetFiles(fulfillmentFiles, dropTrack, isrc);
            insertIntoDropTrackDropAssetFiles(fulfillmentFiles, dropTrack, null);
        }
    }

    private void insertIntoDropTrackDropAssetFiles(Map<String, List<DropAssetFile>> fulfillmentFiles, DropTrack data, String key) {
        if (fulfillmentFiles.containsKey(key)) {
            for (DropAssetFile dropAssetFile : fulfillmentFiles.get(key)) {
                if (!data.files.contains(dropAssetFile)) {
                    data.files.add(dropAssetFile);
                }
            }
        }
    }

    private void parseTerritories(String country, String provider, Date startDate, Element track, DropTrack data) {
        Element trackPricing = track.getChild("track_pricing");
        String isrc = track.getAttributeValue("isrc");

        DropTerritory territoryData = DropTerritory.getTerritory(country, data.territories);
        territoryData.country = country;
        territoryData.label = track.getChildText("track_label");
        territoryData.reportingId = isrc;
        territoryData.distributor = provider;
        territoryData.startdate = startDate;
        territoryData.priceCode = trackPricing.getChildText("current_price_code");
    }

    private boolean parseExplicit(String prdExplicit, Element track) {
        String trackExplicit = track.getChildText("track_explicit");
        boolean explicit = "Y".equals(prdExplicit);

        return !"".equals(trackExplicit) ?
               "Y".equals(trackExplicit) :
               explicit;
    }

    private DropTrack.Type parseType(Element product) {
        String type = product.getChildText("type");
        return "new".equalsIgnoreCase(type) ?
               DropTrack.Type.INSERT :
               DropTrack.Type.UPDATE;
    }

    private Date parseStartDate(String releaseDate) {
        Date startDate = null;

        try {
            if (releaseDate != null) {
                startDate = dateFormat.parse(releaseDate);
            }
        } catch (ParseException e) {
            LOGGER.warn(e.getMessage(), e);
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
        @SuppressWarnings("unchecked") List<Element> artists = track.getChild("track_contributors").getChildren("artist_name");
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

    @Override
    public Map<String, DropTrack> ingest(DropData drop) {

        Map<String, DropTrack> result = new HashMap<>();
        try {
            File fulfillment = new File(root + "/Delivery_Messages/fulfillment_" + drop.name + ".xml");
            SAXBuilder builder = new SAXBuilder();
            builder.setEntityResolver(new DtdLoader());
            LOGGER.info("Loading [{}]", fulfillment.getPath());

            try {
                Document document = builder.build(fulfillment);
                Element rootNode = document.getRootElement();

                @SuppressWarnings("unchecked") List<Element> products = rootNode.getChild("products").getChildren("product");
                for (Element product : products) {
                    result.putAll(parseProduct(drop.name, product));
                }
            }
            catch (IOException | JDOMException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        catch (Exception e) {
            LOGGER.error("Can't ingest [{}] drop", drop, e);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, DropTrack> parseProduct(String dropId, Element product) {
        String code = product.getChildText("upc");
        String base = getResourceBase(code, dropId);

        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<>();

        List<Element> assets = new ArrayList<>(product.getChild("assets").getChildren("asset"));
        assets.addAll(product.getChild("assets").getChildren("track"));

        for (Element asset : assets) {
            DropAssetFile assetFile = parseAssetFile(asset);

            if (assetFile.type != null) {
                assetFile.file = base + assetFile.file;

                List<DropAssetFile> assetFiles = fulfillmentFiles.get(assetFile.isrc);
                if (assetFiles == null) {
                    assetFiles = new ArrayList<>();
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
        assetFile.isrc = assetFile.type == AssetFile.FileType.IMAGE ?
                         null :
                         isrc;
        assetFile.duration = parseDuration(assetFileEl);

        return assetFile;
    }

    private Integer parseDuration(Element assetFileEl) {
        String length = assetFileEl.getChildText("track_length");

        return length != null ?
               (int) durationFormatter.parsePeriod(length).toDurationFrom(new DateTime(0)).getMillis() :
               null;
    }

    private AssetFile.FileType getAssetType(boolean isExcerpt, String fileType, String assetType, String assetSubType) {
        if ("mp3".equalsIgnoreCase(fileType)) {
            if (!isExcerpt) {
                return AssetFile.FileType.DOWNLOAD;
            }
        } else if ("mp4".equalsIgnoreCase(fileType)) {
            if ("Video".equals(assetType) && "Video".equals(assetSubType)) {
                if (!isExcerpt) {
                    return AssetFile.FileType.VIDEO;
                }
            } else if (!isExcerpt) {
                return AssetFile.FileType.MOBILE;
            } else {
                return AssetFile.FileType.PREVIEW;
            }
        } else if ("jpg".equalsIgnoreCase(fileType) && "Images".equals(assetType) && "Cover Art".equals(assetSubType)) {
            return AssetFile.FileType.IMAGE;
        }

        return null;
    }

    public String getResourceBase(String code, String drop) {
        return root + File.separator + code + "_" + drop + File.separator;
    }

    @Override
    public void commit(DropData drop, boolean auto) {
        if (!auto) {
            File commitFile = new File(root + "/Delivery_Messages/" + drop.name + ".ack");
            try {
                commitFile.createNewFile();
            }
            catch (IOException e) {
                LOGGER.error("Can't create file [{}]", commitFile, e);
            }
        }
        File commitFile = new File(root + "/Delivery_Messages/auto_" + drop.name + ".ack");
        try {
            commitFile.createNewFile();
        }
        catch (IOException e) {
            LOGGER.error("Can't create file [{}]", commitFile, e);
        }
    }

    @Override
    public List<DropData> getDrops(boolean auto) {
        List<DropData> result = new ArrayList<>();
        File deliveries = new File(root + "/Delivery_Messages");
        if (!deliveries.exists()) {
            LOGGER.warn("Skipping drops scanning: folder [{}] does not exists!", deliveries.getAbsolutePath());
            return result;
        }
        LOGGER.info("Checking manifests in {}/Delivery_Messages: found {} files", root, deliveries.listFiles().length);

        File[] fulfillmentFiles = deliveries.listFiles();
        for (File file : fulfillmentFiles) {
            LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
            if (file.getName().startsWith("delivery") && file.getName().endsWith(".xml")) {
                String order = file.getName().substring(file.getName().indexOf('_') + 1, file.getName().lastIndexOf('.'));
                File ackManual = new File(root + "/Delivery_Messages/" + order + ".ack");
                if (!auto) {
                    if (!ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());

                        LOGGER.info("The drop was found: [{}]", drop.name);
                        result.add(drop);
                    }
                } else {
                    File ack = new File(root + "/Delivery_Messages/auto_" + order + ".ack");
                    if (!ack.exists() && !ackManual.exists()) {
                        DropData drop = new DropData();
                        drop.name = order;
                        drop.date = new Date(file.lastModified());

                        LOGGER.info("The drop was found: [{}]", drop.name);
                        result.add(drop);
                    }
                }
            }
        }

        return result;
    }

}