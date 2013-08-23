package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.UPDATE;

public abstract class DDEXParser extends IParser {

    protected static final Log LOG = LogFactory.getLog(DDEXParser.class);

    public DDEXParser(String root) throws FileNotFoundException {
        super(root);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, DropTrack> loadXml(String file) {

        SAXBuilder builder = new SAXBuilder();
        LOG.info("Loading " + file);
        File xmlFile = new File(file);

        try {
            String fileRoot = xmlFile.getParent();
            Map<String, DropTrack> resourceDetails = new HashMap<String, DropTrack>();

            Document document = builder.build(xmlFile);
            Element rootNode = document.getRootElement();

            String distributor = getDistributor(rootNode);

            Type action = getActionType(rootNode);

            Map<String, List<DropAssetFile>> files = parseMediaFiles(fileRoot, resourceDetails, rootNode);

            DropAssetFile imageFile = parseImageFile(fileRoot, rootNode);

            Map<String, Map<String, DropTerritory>> deals = parseDeals(rootNode);

            return parseReleases(imageFile, files, resourceDetails, deals, distributor, action, rootNode);

        } catch (IOException io) {
            LOG.error("Exception " + io.getMessage());
        } catch (JDOMException jdomex) {
            LOG.error("Exception " + jdomex.getMessage());
        }
        return null;
    }

    private Map<String, DropTrack> parseReleases(DropAssetFile imageFile, Map<String, List<DropAssetFile>> files, Map<String, DropTrack> resourceDetails, Map<String, Map<String, DropTerritory>> deals, String distributor, Type action, Element rootNode) {
        Element albumElement = null;
        Map<String, DropTrack> result = new HashMap<String, DropTrack>();
        List<Element> releaseList = rootNode.getChild("ReleaseList").getChildren("Release");

        for (int i = 0; i < releaseList.size(); i++) {
            Element release = releaseList.get(i);
            String type = release.getChildText("ReleaseType");
            LOG.info("release type " + type);

            boolean isAlbum = checkAlbum(type);

            if (!isAlbum) {
                DropTrack track = parseTrack(distributor, action, deals, files, resourceDetails, imageFile, release);

                result.put(track.isrc + track.productCode + getClass(), track);
            } else {
                albumElement = release;
            }
        }

        parseAlbum(result, albumElement);

        return result;
    }

    private void parseAlbum(Map<String, DropTrack> result, Element albumElement) {
        String albumTitle = null;
        String upc = null;
        String grid = null;

        albumTitle = albumElement.getChild("ReferenceTitle").getChildText("TitleText");
        Element releaseId = albumElement.getChild("ReleaseId");
        if (releaseId != null) {
            upc = releaseId.getChildText("ICPN");
            grid = releaseId.getChildText("GRid");
        }
        LOG.info("album " + albumTitle);
        // Add album title to all tracks
        if (albumTitle != null)
            for (DropTrack track : result.values()) {
                track.album = albumTitle;
                setUpc(track, upc);
                setGRid(track, grid);
            }
    }

    private DropTrack parseTrack(String distributor, Type action, Map<String, Map<String, DropTerritory>> deals, Map<String, List<DropAssetFile>> files, Map<String, DropTrack> resourceDetails, DropAssetFile imageFile, Element release) {
        DropTrack track = new DropTrack();
        track.type = action;

        String resourceRef = release.getChild("ReleaseResourceReferenceList").getChildText("ReleaseResourceReference");
        LOG.info("Resource reference " + resourceRef);

        if (files.get(resourceRef) != null)
            track.files.addAll(files.get(resourceRef));

        if (imageFile != null)
            track.files.add(imageFile);

        getIds(release, track, track.files);

        DropTrack resourceDetail = resourceDetails.get(resourceRef);

        if (track.isrc == null || "".equals(track.isrc)) {
            if (release.getChild("ReleaseResourceReferenceList").getChildren("ReleaseResourceReference").size() == 1
                    && resourceDetail != null) {
                LOG.info("Getting ISRC from resource " + resourceRef);
                track.isrc = resourceDetail.isrc;
            }
        }

        Element pLine = release.getChild("PLine");
        if (pLine != null) {
            track.year = pLine.getChildText("Year");
            track.copyright = pLine.getChildText("PLineText");
        } else if (resourceDetail != null) {
            track.year = resourceDetail.year;
            track.copyright = resourceDetail.copyright;
        }

        if (resourceDetail != null) {
            track.explicit = resourceDetail.explicit;
        }

        Element details = release.getChild("ReleaseDetailsByTerritory");

        track.title = release.getChild("ReferenceTitle").getChildText("TitleText");
        track.subTitle = getSubTitle(release, details);
        track.artist = getArtist(details);
        track.label = details.getChildText("LabelName");

        XMLOutputter outPutter = new XMLOutputter();
        track.xml = outPutter.outputString(release);

        parseTerritories(distributor, deals, release, track);

        return track;
    }

    private String getArtist(Element details) {
        String artist = details.getChildText("DisplayArtistName");
        if (artist == null || "".equals(artist)) {
            // Try another format (used by CI)
            List<Element> displayArtists = details.getChildren("DisplayArtist");
            if (displayArtists != null && displayArtists.size() > 0) {
                for (Element displayArtist : displayArtists) {
                    if (displayArtist.getChildText("ArtistRole").equals("MainArtist")) {
                        artist = displayArtist.getChild("PartyName").getChildText("FullName");
                    }
                }
                if (artist == null || "".equals(artist)) {
                    // Default to first one.....
                    artist = displayArtists.get(0).getChild("PartyName").getChildText("FullName");
                }
            }
        }
        return artist;
    }

    private String getSubTitle(Element release, Element details) {
        String subTitle = release.getChild("ReferenceTitle").getChildText("SubTitle");

        //Get all sub titles from ReleaseDetailsByTerritory
        Element detailsTitle = details.getChild("Title");
        List subTitles = detailsTitle.getChildren("SubTitle");
        if (subTitles != null) {
            String fullSubTitle = new String();
            for (int si = 0; si < subTitles.size(); si++) {
                Element subTitleElement = (Element) subTitles.get(si);
                fullSubTitle += subTitleElement.getText();
                if (si < subTitles.size() - 1)
                    fullSubTitle += " / ";
            }
            if (!"".equals(fullSubTitle))
                subTitle = fullSubTitle;
        }

        return subTitle;
    }

    private void parseTerritories(String distributor, Map<String, Map<String, DropTerritory>> deals, Element release, DropTrack track) {
        List<Element> territoriesNodes = release.getChildren("ReleaseDetailsByTerritory");
        for (Element territory : territoriesNodes) {
            String code = territory.getChildText("TerritoryCode");
            String releaseReference = release.getChildText("ReleaseReference");
            Element genre = territory.getChild("Genre");
            if (genre != null)
                track.genre = genre.getChildText("GenreText");

            Map<String, DropTerritory> deal = deals.get(releaseReference);
            LOG.info("Deal for release ref  " + releaseReference + " " + deal);

            if (deal == null) {
                continue;
            }
            if ("Worldwide".equals(code)) {
                Set<String> countries = deal.keySet();
                Iterator<String> it = countries.iterator();
                while (it.hasNext()) {
                    String country = it.next();
                    LOG.info("Adding country " + country);
                    DropTerritory territoryData = DropTerritory.getTerritory(country, track.territories);
                    DropTerritory dealTerritory = deal.get(country);
                    territoryData.country = dealTerritory.country;
                    territoryData.takeDown = dealTerritory.takeDown;
                    territoryData.distributor = distributor;
                    String territoryLabel = territory.getChildText("LabelName");
                    territoryData.label = territoryLabel;
                    territoryData.reportingId = track.isrc;
                    territoryData.startdate = dealTerritory.startdate;
                    territoryData.price = dealTerritory.price;
                    territoryData.priceCode = dealTerritory.priceCode;
                    territoryData.currency = dealTerritory.currency;
                    territoryData.dealReference = dealTerritory.dealReference;
                }
            } else {
                LOG.info("Adding country " + code);

                DropTerritory dealTerritory = deal.get(code);
                DropTerritory territoryData = DropTerritory.getTerritory(code, track.territories);
                territoryData.country = dealTerritory.country;
                territoryData.takeDown = dealTerritory.takeDown;
                territoryData.distributor = distributor;
                String territoryLabel = territory.getChildText("LabelName");
                territoryData.label = territoryLabel;
                territoryData.reportingId = track.isrc;
                territoryData.startdate = dealTerritory.startdate;
                territoryData.price = dealTerritory.price;
                territoryData.priceCode = dealTerritory.priceCode;
                territoryData.currency = dealTerritory.currency;
                territoryData.dealReference = dealTerritory.dealReference;
            }
        }
    }

    private Type getActionType(Element rootNode) {
        String updateIndicator = rootNode.getChildText("UpdateIndicator");
        return "UpdateMessage".equals(updateIndicator) ? UPDATE : INSERT;
    }

    private Map<String, Map<String, DropTerritory>> parseDeals(Element rootNode) {
        Map<String, Map<String, DropTerritory>> deals = new HashMap<String, Map<String, DropTerritory>>();
        List<Element> mediaFiles = rootNode.getChild("DealList").getChildren("ReleaseDeal");
        for (int i = 0; i < mediaFiles.size(); i++) {
            Element releaseDeal = mediaFiles.get(i);
            List<Element> references = releaseDeal.getChildren("DealReleaseReference");

            Map<String, DropTerritory> dealsMap = new HashMap<String, DropTerritory>();

            List<Element> dealNodes = releaseDeal.getChildren("Deal");

            for (Element dealNode : dealNodes) {
                Element dealTerms = dealNode.getChild("DealTerms");

                boolean validUseType = validDealUseType(dealTerms);

                String reference = dealNode.getChildText("DealReference");

                parseTerritoryCodes(dealsMap, reference, dealTerms, validUseType);
            }

            for (Element reference : references) {
                LOG.info("Loading deal reference " + reference.getText());
                Map<String, DropTerritory> ExistingDealsMap = deals.get(reference.getText());
                if (ExistingDealsMap == null)
                    deals.put(reference.getText(), dealsMap);
                else
                    ExistingDealsMap.putAll(dealsMap);
            }
        }
        return deals;
    }

    private void parseTerritoryCodes(Map<String, DropTerritory> dealsMap, String reference, Element dealTerms, boolean validUseType) {
        String takeDown = dealTerms.getChildText("TakeDown");
        if (takeDown != null || validUseType) {
            List<Element> countriesNodes = dealTerms.getChildren("TerritoryCode");
            String startDate = dealTerms.getChild("ValidityPeriod").getChildText("StartDate");
            Date dealStartDate = null;
            SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dealStartDate = dateParse.parse(startDate);
            } catch (ParseException e) {
            }

            Element priceInfo = dealTerms.getChild("PriceInformation");
            String price = null;
            String currency = null;
            String priceType = null;
            if (priceInfo != null) {
                Element wholeSalePrice = priceInfo.getChild("WholesalePricePerUnit");
                if (wholeSalePrice != null) {
                    currency = wholeSalePrice.getAttributeValue("CurrencyCode");
                    price = wholeSalePrice.getText();
                }
                priceType = priceInfo.getChildText("PriceType");
                if (priceType == null) {
                    priceType = priceInfo.getChildText("PriceRangeType");
                }
            }

            for (Element country : countriesNodes) {
                LOG.info("Deal for country " + country.getText());
                DropTerritory territory = dealsMap.get(country.getText());
                if (territory == null) {
                    territory = new DropTerritory();
                    dealsMap.put(country.getText(), territory);
                }
                territory.country = country.getText();
                territory.takeDown = (takeDown != null);
                territory.startdate = dealStartDate;
                territory.dealReference = reference;
                try {
                    if (price != null)
                        territory.price = Float.valueOf(price);
                } catch (NumberFormatException e) {
                }
                territory.currency = currency;
                territory.priceCode = priceType;
            }
        }
    }

    private boolean validDealUseType(Element dealTerms) {
        boolean validUseType = false;
        Element usage = dealTerms.getChild("Usage");
        if (usage != null) {
            List<Element> useTypes = usage.getChildren("UseType");
            for (Element useType : useTypes) {
                if ("AsPerContract".equals(useType.getText()) || "Download".equals(useType.getText())
                        || "PermanentDownload".equals(useType.getText())) {
                    LOG.info("Found valid usage " + useType.getText());
                    validUseType = true;
                    break;
                }
            }
        }
        return validUseType;
    }

    private DropAssetFile parseImageFile(String fileRoot, Element rootNode) {
        DropAssetFile imageFile = null;
        List<Element> mediaFiles;
        mediaFiles = rootNode.getChild("ResourceList").getChildren("Image");
        for (int i = 0; i < mediaFiles.size(); i++) {
            Element node = mediaFiles.get(i);
            Element details = node.getChild("ImageDetailsByTerritory");
            Element techDetail = details.getChild("TechnicalImageDetails");
            if (techDetail != null) {
                imageFile = new DropAssetFile();
                String fileName = techDetail.getChild("File").getChildText("FileName");
                imageFile.file = getAssetFile(fileRoot, fileName);

                imageFile.type = IMAGE;
                // Get Hash
                Element hash = techDetail.getChild("File").getChild("HashSum");
                if (hash != null) {
                    if ("MD5".equals(hash.getChildText("HashSumAlgorithmType"))) {
                        imageFile.md5 = hash.getChildText("HashSum");
                    }
                }
            }
        }
        return imageFile;
    }

    private Map<String, List<DropAssetFile>> parseMediaFiles(String fileRoot, Map<String, DropTrack> resourceDetails, Element rootNode) {
        Map<String, List<DropAssetFile>> files = new HashMap<String, List<DropAssetFile>>();
        List<Element> list = rootNode.getChild("ResourceList").getChildren("SoundRecording");

        for (Element node: list) {
            Element details = node.getChild("SoundRecordingDetailsByTerritory");
            String reference = node.getChildText("ResourceReference");
            DropTrack resourceDetail = new DropTrack();
            resourceDetail.isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
            String parentalWarningType = details.getChildText("ParentalWarningType");
            resourceDetail.explicit = "Explicit".equals(parentalWarningType);
            resourceDetails.put(reference, resourceDetail);
            if (details.getChild("PLine") != null) {
                resourceDetail.copyright = details.getChild("PLine").getChildText("PLineText");
                resourceDetail.year = details.getChild("PLine").getChildText("Year");
            }
            List<Element> techDetails = details.getChildren("TechnicalSoundRecordingDetails");
            for (Element techDetail : techDetails) {
                String preview = techDetail.getChildText("IsPreview");
                String fileName = techDetail.getChild("File").getChildText("FileName");
                DropAssetFile assetFile = new DropAssetFile();
                assetFile.file = getAssetFile(fileRoot, fileName);
                assetFile.isrc = resourceDetail.isrc;
                if (preview == null || "false".equals(preview)) {
                    String codecType = techDetail.getChildText("AudioCodecType");
                    if (codecType == null
                            || "MP3".equals(codecType)
                            || ("UserDefined".equals(codecType) && "MP3".equals(techDetail.getChild("AudioCodecType").getAttributeValue(
                            "UserDefinedValue")))) {
                        assetFile.type = DOWNLOAD;
                    } else {
                        assetFile.type = MOBILE;
                    }

                } else {
                    assetFile.type = PREVIEW;
                }
                List<DropAssetFile> resourceFiles = files.get(node.getChildText("ResourceReference"));
                if (resourceFiles == null) {
                    resourceFiles = new ArrayList<DropAssetFile>();
                    files.put(reference, resourceFiles);
                }
                resourceFiles.add(assetFile);

                // Get Hash
                Element hash = techDetail.getChild("File").getChild("HashSum");
                if (hash != null) {
                    if ("MD5".equals(hash.getChildText("HashSumAlgorithmType"))) {
                        assetFile.md5 = hash.getChildText("HashSum");
                    }
                }
            }
        }

        return files;
    }

    private String getDistributor(Element rootNode) {
        Element messageHeader = rootNode.getChild("MessageHeader");
        Element onBehalf = messageHeader.getChild("SentOnBehalfOf");
        Element general = onBehalf != null ? onBehalf : messageHeader.getChild("MessageSender");

        return general.getChild("PartyName").getChildText("FullName");
    }

    protected String getAssetFile(String root, String file) {
        return root + "/resources/" + file;
    }

    public abstract void getIds(Element release, DropTrack track, List<DropAssetFile> files);

    public void setUpc(DropTrack track, String upc) {
    }

    public void setGRid(DropTrack track, String GRid) {
    }

    public boolean checkAlbum(String type) {
        if ("Single".equals(type) || "Album".equals(type) || "SingleResourceRelease".equals(type)) {
            LOG.info("Album for " + type);
            return true;
        }
        LOG.info("Track for " + type);
        return false;

    }

}