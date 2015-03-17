package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.MOBILE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.PREVIEW;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.io.File.separator;

import net.ddex.xml.ern.x37.Artist;
import net.ddex.xml.ern.x37.CurrentTerritoryCode;
import net.ddex.xml.ern.x37.Deal;
import net.ddex.xml.ern.x37.DealReference;
import net.ddex.xml.ern.x37.DealTerms;
import net.ddex.xml.ern.x37.Genre;
import net.ddex.xml.ern.x37.HashSum;
import net.ddex.xml.ern.x37.Image;
import net.ddex.xml.ern.x37.ImageDetailsByTerritory;
import net.ddex.xml.ern.x37.LabelName;
import net.ddex.xml.ern.x37.MessageHeader;
import net.ddex.xml.ern.x37.MessagingParty;
import net.ddex.xml.ern.x37.Name;
import net.ddex.xml.ern.x37.NewReleaseMessageDocument;
import net.ddex.xml.ern.x37.NewReleaseMessageDocument.NewReleaseMessage;
import net.ddex.xml.ern.x37.PLine;
import net.ddex.xml.ern.x37.Price;
import net.ddex.xml.ern.x37.PriceInformation;
import net.ddex.xml.ern.x37.Release;
import net.ddex.xml.ern.x37.ReleaseDeal;
import net.ddex.xml.ern.x37.ReleaseDetailsByTerritory;
import net.ddex.xml.ern.x37.ReleaseId;
import net.ddex.xml.ern.x37.ResourceList;
import net.ddex.xml.ern.x37.SoundRecording;
import net.ddex.xml.ern.x37.SoundRecordingDetailsByTerritory;
import net.ddex.xml.ern.x37.SubTitle;
import net.ddex.xml.ern.x37.TechnicalImageDetails;
import net.ddex.xml.ern.x37.TechnicalSoundRecordingDetails;
import net.ddex.xml.ern.x37.TechnicalVideoDetails;
import net.ddex.xml.ern.x37.Title;
import net.ddex.xml.ern.x37.TitleText;
import net.ddex.xml.ern.x37.TypedSubTitle;
import net.ddex.xml.ern.x37.Usage;
import net.ddex.xml.ern.x37.UseType;
import net.ddex.xml.ern.x37.Video;
import net.ddex.xml.ern.x37.VideoDetailsByTerritory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.xmlbeans.GDuration;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.primitives.Ints.checkedCast;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class DDEXParserERN_V3_7_AssetAndMetaData_V1_13 extends IParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DDEXParserERN_V3_7_AssetAndMetaData_V1_13.class);

    public DDEXParserERN_V3_7_AssetAndMetaData_V1_13(String root) throws FileNotFoundException {
        super(root);
    }

    private Map<String, DropTrack> parseReleases(DropAssetFile imageFile, Map<String, List<DropAssetFile>> files, Map<String, DropTrack> resourceDetails, Map<String, Map<String, DropTerritory>> deals,
                                                 String distributor, Type action, NewReleaseMessage newReleaseMessage) {
        Release albumElement = null;
        Map<String, DropTrack> result = new HashMap<>();
        Release[] releaseArray = newReleaseMessage.getReleaseList().getReleaseArray();

        for (Release release : releaseArray) {
            String type = release.getReleaseTypeArray(0).getStringValue();
            LOGGER.info("release type [{}]", type);

            boolean isAlbum = checkAlbum(type);

            if (!isAlbum) {
                isAlbum = isWrongAlbum(release);
            }

            if (!isAlbum) {
                DropTrack track = parseTrack(distributor, action, deals, files, resourceDetails, imageFile, release, newReleaseMessage);

                result.put(track.isrc + track.productCode + getClass(), track);
            }
            else {
                albumElement = release;
            }
        }

        parseAlbum(result, albumElement);

        return result;
    }

    private void parseAlbum(Map<String, DropTrack> result, Release albumElement) {
        String upc = null;
        String grid = null;

        ReleaseId[] releaseIdArray = albumElement.getReleaseIdArray();
        if (ArrayUtils.isNotEmpty(releaseIdArray)) {
            ReleaseId releaseId = releaseIdArray[0];
            upc = releaseId.getICPN().getStringValue();
            grid = releaseId.getGRid();
        }
        // Add album title to all tracks
        TitleText titleText = albumElement.getReferenceTitle().getTitleText();
        LOGGER.info("album [{}]", titleText);
        if (isNotNull(titleText)) {
            for (DropTrack track : result.values()) {
                track.album = titleText.getStringValue();
                setUpc(track, upc);
                setGRid(track, grid);
            }
        }
    }

    private DropTrack parseTrack(String distributor, Type action, Map<String, Map<String, DropTerritory>> deals, Map<String, List<DropAssetFile>> files, Map<String, DropTrack> resourceDetails,
                                 DropAssetFile imageFile, Release release, NewReleaseMessage newReleaseMessage) {
        DropTrack track = new DropTrack();
        track.type = action;

        String resourceRef = release.getReleaseResourceReferenceList().getReleaseResourceReferenceArray(0).getStringValue();
        LOGGER.info("Resource reference [{}]", resourceRef);

        if (files.get(resourceRef) != null) {
            track.files.addAll(files.get(resourceRef));
        }

        if (imageFile != null) {
            track.files.add(imageFile);
        }

        getIds(release, track, track.files, newReleaseMessage);

        DropTrack resourceDetail = resourceDetails.get(resourceRef);

        if (isEmpty(track.isrc)) {
            if (release.getReleaseResourceReferenceList().getReleaseResourceReferenceArray().length == 1 && resourceDetail != null) {
                LOGGER.info("Getting ISRC from resource [{}]", resourceRef);
                track.isrc = resourceDetail.isrc;
            }
        }

        PLine[] pLineArray = release.getPLineArray();
        if (ArrayUtils.isNotEmpty(pLineArray)) {
            PLine pLine = pLineArray[0];
            track.year = getYear(pLine.getYear());
            track.copyright = pLine.getPLineText();
        }
        else if (resourceDetail != null) {
            track.year = resourceDetail.year;
            track.copyright = resourceDetail.copyright;
        }

        if (resourceDetail != null) {
            track.explicit = resourceDetail.explicit;
        }

        ReleaseDetailsByTerritory releaseDetailsByTerritory = release.getReleaseDetailsByTerritoryArray(0);

        track.title = release.getReferenceTitle().getTitleText().getStringValue();
        track.subTitle = getSubTitle(release, releaseDetailsByTerritory);
        track.artist = getArtist(releaseDetailsByTerritory);
        LabelName[] labelNameArray = releaseDetailsByTerritory.getLabelNameArray();
        if (ArrayUtils.isNotEmpty(labelNameArray)) {
            track.label = labelNameArray[0].getStringValue();
        }
        track.genre = resourceDetail.genre;
        track.xml = release.xmlText();

        parseTerritories(distributor, deals, release, track);

        return track;
    }

    private String getArtist(ReleaseDetailsByTerritory releaseDetailsByTerritory) {
        Name[] displayArtistNameArray = releaseDetailsByTerritory.getDisplayArtistNameArray();
        if (ArrayUtils.isEmpty(displayArtistNameArray) || "".equals(displayArtistNameArray[0].getStringValue())) {
            // Try another format (used by CI)
            String artistName = null;
            Artist[] displayArtistArray = releaseDetailsByTerritory.getDisplayArtistArray();
            if (ArrayUtils.isNotEmpty(displayArtistArray)) {
                artistName = displayArtistArray[0].getPartyNameArray(0).getFullName().getStringValue();
                for (int i = 1; i < displayArtistArray.length; i++) {
                    Artist artist = displayArtistArray[i];
                    if (artist.getArtistRoleArray(0).getStringValue().equals("MainArtist")) {
                        artistName = artist.getPartyNameArray(0).getFullName().getStringValue();
                    }
                }
            }
            return artistName;
        }
        else {
            return displayArtistNameArray[0].getStringValue();
        }
    }

    private String getSubTitle(Release release, ReleaseDetailsByTerritory releaseDetailsByTerritory) {
        SubTitle subTitle = release.getReferenceTitle().getSubTitle();
        String subTitleValue = null;
        if (isNotNull(subTitle)) {
            subTitleValue = subTitle.getStringValue();
        }

        //Get all sub titles from ReleaseDetailsByTerritory
        Title titleArray = releaseDetailsByTerritory.getTitleArray(0);
        TypedSubTitle[] subTitleArray = titleArray.getSubTitleArray();
        if (subTitleArray != null) {
            String fullSubTitle = "";
            for (int si = 0; si < subTitleArray.length; si++) {
                TypedSubTitle typedSubTitle = subTitleArray[si];
                fullSubTitle += typedSubTitle.getStringValue();
                if (si < subTitleArray.length - 1) {
                    fullSubTitle += " / ";
                }
            }
            if (isNotEmpty(fullSubTitle)) {
                subTitleValue = fullSubTitle;
            }
        }

        return subTitleValue;
    }

    private void parseTerritories(String distributor, Map<String, Map<String, DropTerritory>> deals, Release release, DropTrack track) {
        ReleaseDetailsByTerritory[] releaseDetailsByTerritoryArray = release.getReleaseDetailsByTerritoryArray();
        for (ReleaseDetailsByTerritory releaseDetailsByTerritory : releaseDetailsByTerritoryArray) {
            CurrentTerritoryCode[] territoryCodeArray = releaseDetailsByTerritory.getTerritoryCodeArray();
            for (CurrentTerritoryCode aTerritoryCodeArray : territoryCodeArray) {
                String releaseReference = release.getReleaseReferenceArray(0);
                Genre[] genreArray = releaseDetailsByTerritory.getGenreArray();
                if (ArrayUtils.isNotEmpty(genreArray)) {
                    track.genre = genreArray[0].getGenreText().getStringValue();
                }

                Map<String, DropTerritory> deal = deals.get(releaseReference);
                LOGGER.info("Deal for release ref [{}] [{}]", releaseReference, deal);

                if (deal == null) {
                    continue;
                }
                String code = aTerritoryCodeArray.getStringValue();
                if ("Worldwide".equals(code)) {
                    Set<String> countries = deal.keySet();
                    for (String country : countries) {
                        parseTerritory(distributor, track, releaseDetailsByTerritory, deal, country);
                    }
                }
                else {
                    parseTerritory(distributor, track, releaseDetailsByTerritory, deal, code);
                }
            }
        }
    }

    private void parseTerritory(String distributor, DropTrack track, ReleaseDetailsByTerritory releaseDetailsByTerritory, Map<String, DropTerritory> deal, String country) {
        LOGGER.info("Adding country [{}]", country);
        DropTerritory territoryData = DropTerritory.getTerritory(country, track.territories);
        DropTerritory dealTerritory = deal.get(country);
        if (dealTerritory != null) {
            territoryData.country = dealTerritory.country;
            territoryData.takeDown = dealTerritory.takeDown;
            territoryData.startdate = dealTerritory.startdate;
            territoryData.price = dealTerritory.price;
            territoryData.priceCode = dealTerritory.priceCode;
            territoryData.currency = dealTerritory.currency;
            territoryData.dealReference = dealTerritory.dealReference;
        }
        territoryData.distributor = distributor;
        LabelName[] labelNameArray = releaseDetailsByTerritory.getLabelNameArray();
        if (ArrayUtils.isNotEmpty(labelNameArray)) {
            territoryData.label = labelNameArray[0].getStringValue();
        }
        territoryData.reportingId = track.isrc;
    }

    private Map<String, Map<String, DropTerritory>> parseDeals(NewReleaseMessage newReleaseMessage) {
        Map<String, Map<String, DropTerritory>> deals = new HashMap<>();
        ReleaseDeal[] releaseDealArray = newReleaseMessage.getDealList().getReleaseDealArray();
        for (ReleaseDeal releaseDeal : releaseDealArray) {
            String[] dealReleaseReferenceArray = releaseDeal.getDealReleaseReferenceArray();

            Map<String, DropTerritory> dealsMap = new HashMap<>();

            Deal[] dealArray = releaseDeal.getDealArray();
            for (Deal deal : dealArray) {
                DealTerms dealTerms = deal.getDealTerms();

                boolean validUseType = validDealUseType(dealTerms);

                DealReference[] dealReferenceArray = deal.getDealReferenceArray();
                String reference = null;
                if (ArrayUtils.isNotEmpty(dealReferenceArray)) {
                    reference = dealReferenceArray[0].getStringValue();
                }

                parseTerritoryCodes(dealsMap, reference, dealTerms, validUseType);
            }

            for (String dealReleaseReference : dealReleaseReferenceArray) {
                LOGGER.info("Loading deal reference [{}]", dealReleaseReference);
                Map<String, DropTerritory> existingDealsMap = deals.get(dealReleaseReference);
                if (existingDealsMap == null) {
                    deals.put(dealReleaseReference, dealsMap);
                }
                else {
                    existingDealsMap.putAll(dealsMap);
                }
            }
        }
        return deals;
    }

    private void parseTerritoryCodes(Map<String, DropTerritory> dealsMap, String reference, DealTerms dealTerms, boolean validUseType) {
        boolean takeDown = dealTerms.getTakeDown();
        if (takeDown || validUseType) {
            CurrentTerritoryCode[] territoryCodeArray = dealTerms.getTerritoryCodeArray();
            String startDate = dealTerms.getValidityPeriodArray(0).getStartDate().getStringValue();
            Date dealStartDate = null;
            SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dealStartDate = dateParse.parse(startDate);
            }
            catch (ParseException e) {
                LOGGER.error(e.getMessage());
            }

            PriceInformation[] priceInformationArray = dealTerms.getPriceInformationArray();
            Float price = null;
            String currency = null;
            String priceType = null;
            if (ArrayUtils.isNotEmpty(priceInformationArray)) {
                PriceInformation priceInformation = priceInformationArray[0];
                Price wholesalePricePerUnit = priceInformation.getWholesalePricePerUnit();
                if (isNotNull(wholesalePricePerUnit)) {
                    currency = wholesalePricePerUnit.getCurrencyCode();
                    price = wholesalePricePerUnit.getBigDecimalValue().floatValue();
                }
                priceType = priceInformation.getPriceType().getStringValue();
                if (priceType == null) {
                    priceType = priceInformation.getPriceRangeType().getStringValue();
                }
            }
            for (CurrentTerritoryCode currentTerritoryCode : territoryCodeArray) {
                LOGGER.info("Deal for country [{}]", currentTerritoryCode.getStringValue());
                DropTerritory territory = dealsMap.get(currentTerritoryCode.getStringValue());
                if (territory == null) {
                    territory = new DropTerritory();
                    dealsMap.put(currentTerritoryCode.getStringValue(), territory);
                }
                territory.country = currentTerritoryCode.getStringValue();
                territory.takeDown = takeDown;
                territory.startdate = dealStartDate;
                territory.dealReference = reference;
                territory.price = price;
                territory.currency = currency;
                territory.priceCode = priceType;
            }
        }
    }

    protected boolean validDealUseType(DealTerms dealTerms) {
        boolean validUseType = false;
        Usage[] usageArray = dealTerms.getUsageArray();
        if (ArrayUtils.isNotEmpty(usageArray)) {
            Usage usage = usageArray[0];
            UseType[] useTypeArray = usage.getUseTypeArray();
            for (UseType useType : useTypeArray) {
                String useTypeStringValue = useType.getStringValue();
                if ("AsPerContract".equals(useTypeStringValue) || "Download".equals(useTypeStringValue) || "PermanentDownload".equals(useTypeStringValue) ||
                    "ConditionalDownload".equals(useTypeStringValue) || "Stream".equals(useTypeStringValue)) {
                    LOGGER.info("Found valid usage, [{}] ", useTypeStringValue);
                    validUseType = true;
                    break;
                }
            }
        }
        return validUseType;
    }

    private DropAssetFile parseImageFile(String fileRoot, NewReleaseMessage newReleaseMessage) {
        DropAssetFile imageFile = null;
        Image[] imageArray = newReleaseMessage.getResourceList().getImageArray();
        for (Image image : imageArray) {
            ImageDetailsByTerritory imageDetailsByTerritory = image.getImageDetailsByTerritoryArray(0);
            TechnicalImageDetails[] technicalImageDetailsArray = imageDetailsByTerritory.getTechnicalImageDetailsArray();
            if (ArrayUtils.isNotEmpty(technicalImageDetailsArray)) {
                imageFile = new DropAssetFile();
                String fileName = technicalImageDetailsArray[0].getFileArray(0).getFileName();
                imageFile.file = getAssetFile(fileRoot, fileName);

                imageFile.type = IMAGE;
                HashSum hashSum = technicalImageDetailsArray[0].getFileArray(0).getHashSum();
                if (isNotNull(hashSum)) {
                    if ("MD5".equals(hashSum.getHashSumAlgorithmType().getStringValue())) {
                        imageFile.md5 = hashSum.getHashSum();
                    }
                }
            }
            if (isPriorityImage(image)) {
                return imageFile;
            }
        }
        return imageFile;
    }

    private Map<String, List<DropAssetFile>> parseMediaFiles(String fileRoot, Map<String, DropTrack> resourceDetails, NewReleaseMessage newReleaseMessage) {
        Map<String, List<DropAssetFile>> files = new HashMap<>();

        ResourceList resourceList = newReleaseMessage.getResourceList();
        SoundRecording[] soundRecordingArray = resourceList.getSoundRecordingArray();

        for (SoundRecording soundRecording : soundRecordingArray) {
            SoundRecordingDetailsByTerritory soundRecordingDetailsByTerritory = soundRecording.getSoundRecordingDetailsByTerritoryArray(0);
            String resourceReference = soundRecording.getResourceReference();

            DropTrack dropTrack = new DropTrack();
            dropTrack.isrc = soundRecording.getSoundRecordingIdArray(0).getISRC();

            String parentalWarningType = soundRecordingDetailsByTerritory.getParentalWarningTypeArray(0).getStringValue();
            dropTrack.explicit = "Explicit".equals(parentalWarningType);
            Genre[] genreArray = soundRecordingDetailsByTerritory.getGenreArray();
            if (ArrayUtils.isNotEmpty(genreArray)) {
                dropTrack.genre = genreArray[0].getGenreText().getStringValue();
            }
            resourceDetails.put(resourceReference, dropTrack);
            PLine pLine = soundRecordingDetailsByTerritory.getPLineArray(0);
            if (isNotNull(pLine)) {
                dropTrack.copyright = pLine.getPLineText();
                dropTrack.year = getYear(pLine.getYear());
            }
            TechnicalSoundRecordingDetails[] technicalSoundRecordingDetailsArray = soundRecordingDetailsByTerritory.getTechnicalSoundRecordingDetailsArray();
            for (TechnicalSoundRecordingDetails technicalSoundRecordingDetails : technicalSoundRecordingDetailsArray) {
                String fileName = technicalSoundRecordingDetails.getFileArray(0).getFileName();
                DropAssetFile assetFile = new DropAssetFile();
                assetFile.file = getAssetFile(fileRoot, fileName);
                assetFile.isrc = dropTrack.isrc;
                assetFile.type = getFileType(technicalSoundRecordingDetails);
                assetFile.duration = getDuration(soundRecording.getDuration());
                List<DropAssetFile> resourceFiles = files.get(soundRecording.getResourceReference());
                if (resourceFiles == null) {
                    resourceFiles = new ArrayList<>();
                    files.put(resourceReference, resourceFiles);
                }
                resourceFiles.add(assetFile);

                HashSum hashSum = technicalSoundRecordingDetails.getFileArray(0).getHashSum();
                if (isNotNull(hashSum)) {
                    if ("MD5".equals(hashSum.getHashSumAlgorithmType().toString())) {
                        assetFile.md5 = hashSum.getHashSum();
                    }
                }
            }
        }

        //check for video content
        Video[] videoArray = newReleaseMessage.getResourceList().getVideoArray();
        if (isNotNull(videoArray)) {
            for (Video video : videoArray) {
                VideoDetailsByTerritory videoDetailsByTerritory = video.getVideoDetailsByTerritoryArray(0);
                String reference = video.getResourceReference();
                DropTrack resourceDetail = new DropTrack();
                resourceDetail.isrc = video.getVideoIdArray(0).getISRC();
                String parentalWarningType = videoDetailsByTerritory.getParentalWarningTypeArray(0).getStringValue();
                resourceDetail.explicit = "Explicit".equals(parentalWarningType);
                Genre genre = videoDetailsByTerritory.getGenreArray(0);
                if (isNotNull(genre)) {
                    resourceDetail.genre = genre.getGenreText().getStringValue();
                }
                resourceDetails.put(reference, resourceDetail);

                PLine[] pLineArray = videoDetailsByTerritory.getPLineArray();
                if (ArrayUtils.isNotEmpty(pLineArray)) {
                    PLine pLine = pLineArray[0];
                    resourceDetail.copyright = pLine.getPLineText();
                    resourceDetail.year = getYear(pLine.getYear());
                }
                TechnicalVideoDetails[] technicalVideoDetailsArray = videoDetailsByTerritory.getTechnicalVideoDetailsArray();
                for (TechnicalVideoDetails technicalVideoDetails : technicalVideoDetailsArray) {
                    String fileName = technicalVideoDetails.getFileArray(0).getFileName();
                    DropAssetFile assetFile = new DropAssetFile();
                    assetFile.file = getAssetFile(fileRoot, fileName);
                    assetFile.isrc = resourceDetail.isrc;
                    assetFile.type = getFileType(technicalVideoDetails);
                    assetFile.duration = getDuration(video.getDuration());
                    List<DropAssetFile> resourceFiles = files.get(video.getResourceReference());
                    if (resourceFiles == null) {
                        resourceFiles = new ArrayList<>();
                        files.put(reference, resourceFiles);
                    }
                    resourceFiles.add(assetFile);
                    HashSum hashSum = technicalVideoDetails.getFileArray(0).getHashSum();
                    if (isNotNull(hashSum)) {
                        if ("MD5".equals(hashSum.getHashSumAlgorithmType().toString())) {
                            assetFile.md5 = hashSum.getHashSum();
                        }
                    }
                }

            }
        }

        return files;
    }

    private String getYear(Calendar year) {
        if (isNotNull(year)) {
            return String.valueOf(year.get(Calendar.YEAR));
        }
        return null;
    }

    protected Integer getDuration(GDuration duration) {
        if (isNotNull(duration)) {
            PeriodParser periodParser = ISOPeriodFormat.standard().getParser();
            ReadWritablePeriod readWritablePeriod = new MutablePeriod();
            if (periodParser.parseInto(readWritablePeriod, duration.toString(), 0, null) > 0) {
                return checkedCast(readWritablePeriod.toPeriod().toStandardDuration().getMillis());
            }
        }
        return null;
    }

    protected String getDistributor(NewReleaseMessage newReleaseMessage) {
        MessageHeader messageHeader = newReleaseMessage.getMessageHeader();
        MessagingParty sentOnBehalfOf = messageHeader.getSentOnBehalfOf();

        if (isNotNull(sentOnBehalfOf)) {
            return sentOnBehalfOf.getPartyName().getFullName().getStringValue();
        }
        else {
            return messageHeader.getMessageSender().getPartyName().getFullName().getStringValue();
        }
    }

    protected String getAssetFile(String root, String fileName) {
        return root + File.separator + fileName;
    }

    protected List<DropData> getDrops(File folder, boolean auto) {
        List<DropData> result = new ArrayList<>();
        if (!folder.exists()) {
            LOGGER.warn("Skipping drops scanning: folder [{}] does not exists!", folder.getAbsolutePath());
            return result;
        }

        File[] content = folder.listFiles();
        boolean deliveryComplete = false;
        boolean processed = false;
        for (File file : content) {
            if (isDirectory(file)) {
                LOGGER.info("Scanning directory [{}]", file.getAbsolutePath());
                result.addAll(getDrops(file, auto));
            }
            else if (file.getName().startsWith("BatchComplete")) {
                deliveryComplete = true;
            }
            else if (INGEST_ACK.equals(file.getName())) {
                processed = true;
            }
            else if (auto && AUTO_INGEST_ACK.equals(file.getName())) {
                processed = true;
            }
        }
        if (deliveryComplete && !processed) {
            LOGGER.debug("Adding [{}] to drops", folder.getAbsolutePath());
            DropData drop = new DropData();
            drop.name = folder.getAbsolutePath();
            drop.date = new Date(folder.lastModified());
            LOGGER.info("The drop was found: [{}]", drop.name);

            result.add(drop);
        }
        return result;
    }

    protected void getIds(Release release, DropTrack track, List<DropAssetFile> files, NewReleaseMessage newReleaseMessage) {
    }

    protected void setUpc(DropTrack track, String upc) {
    }

    protected void setGRid(DropTrack track, String GRid) {
    }

    protected boolean checkAlbum(String type) {
        if ("Single".equals(type) || "Album".equals(type) || "SingleResourceRelease".equals(type) || "VideoSingle".equals(type)) {
            LOGGER.info("Album for [{}]", type);
            return true;
        }
        LOGGER.info("Track for [{}]", type);
        return false;
    }

    protected FileType getFileType(TechnicalSoundRecordingDetails technicalSoundRecordingDetails) {
        FileType fileType;
        boolean isPreview = technicalSoundRecordingDetails.getIsPreview();
        if (!isPreview) {
            String audioCodecType = technicalSoundRecordingDetails.getAudioCodecType().getStringValue();

            String userDefinedValue = technicalSoundRecordingDetails.getAudioCodecType().getUserDefinedValue();
            if (isNull(audioCodecType) || audioCodecType.equals("MP3") || (audioCodecType.equals("UserDefined") && "MP3".equals(userDefinedValue))) {
                fileType = DOWNLOAD;
            }
            else {
                fileType = MOBILE;
            }
        }
        else {
            fileType = PREVIEW;
        }
        return fileType;
    }

    protected FileType getFileType(TechnicalVideoDetails technicalVideoDetails) {
        FileType fileType;
        boolean isPreview = technicalVideoDetails.getIsPreview();
        if (!isPreview) {
            String audioCodecType = technicalVideoDetails.getAudioCodecType().getStringValue();
            String videoCodecType = technicalVideoDetails.getVideoCodecType().getStringValue();
            if (isNotNull(videoCodecType)) {
                return FileType.VIDEO;
            }

            String userDefinedValue = technicalVideoDetails.getAudioCodecType().getUserDefinedValue();
            if (isNull(audioCodecType) || audioCodecType.equals("MP3") || (audioCodecType.equals("UserDefined") && "MP3".equals(userDefinedValue))) {
                fileType = DOWNLOAD;
            }
            else {
                fileType = MOBILE;
            }
        }
        else {
            fileType = PREVIEW;
        }
        return fileType;
    }

    private String getXmlFileName(String parentFolderName) {
        return parentFolderName + ".xml";
    }

    private File getXmlFileParentFolder(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            return fileOrDir;
        }
        else if (fileOrDir.isFile()) {
            return fileOrDir.getParentFile();
        }
        else {
            throw new IllegalArgumentException("Unknown folder content [" + fileOrDir + "]");
        }
    }

    private File getXmlFile(File fileOrDir) {
        File xmlFileParentFolder = getXmlFileParentFolder(fileOrDir);
        return new File(xmlFileParentFolder + separator + getXmlFileName(xmlFileParentFolder.getName()));
    }

    @Override
    public Map<String, DropTrack> ingest(DropData drop) {
        Map<String, DropTrack> tracks = new HashMap<>();
        try {
            File folder = new File(drop.name);
            File[] content = folder.listFiles();
            for (File file : content) {
                Map<String, DropTrack> result = loadXml(getXmlFile(file));

                if (result != null) {
                    tracks.putAll(result);
                }
            }

        }
        catch (Exception e) {
            LOGGER.error("Ingest failed: [{}]", e.getMessage(), e);
        }
        return tracks;
    }

    public Map<String, DropTrack> loadXml(File xmlFile) {

        LOGGER.info("Loading [{}]", xmlFile.getAbsolutePath());

        try {
            String fileRoot = xmlFile.getParent();
            Map<String, DropTrack> resourceDetails = new HashMap<>();

            NewReleaseMessageDocument newReleaseMessageDocument = NewReleaseMessageDocument.Factory.parse(xmlFile);
            NewReleaseMessage newReleaseMessage = newReleaseMessageDocument.getNewReleaseMessage();

            String distributor = getDistributor(newReleaseMessage);

            Type action = Type.INSERT;

            Map<String, List<DropAssetFile>> files = parseMediaFiles(fileRoot, resourceDetails, newReleaseMessage);

            DropAssetFile imageFile = parseImageFile(fileRoot, newReleaseMessage);

            Map<String, Map<String, DropTerritory>> deals = parseDeals(newReleaseMessage);

            return parseReleases(imageFile, files, resourceDetails, deals, distributor, action, newReleaseMessage);

        }
        catch (Exception e) {
            LOGGER.error("Can't load xml file [{}]", xmlFile.getAbsolutePath(), e);
        }
        return null;
    }

    @Override
    public List<DropData> getDrops(boolean auto) {
        List<DropData> result = new ArrayList<>();
        File rootFolder = new File(root);
        result.addAll(getDrops(rootFolder, auto));
        return result;
    }

    protected boolean isWrongAlbum(Release release) {
        return false;
    }

    protected boolean isPriorityImage(Image image) {
        return false;
    }

}