package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import net.sf.saxon.s9api.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.primitives.Ints.checkedCast;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class AbsoluteParserCleanerWithPreCompiledXPathVersion extends DDEXParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParserCleanerWithPreCompiledXPathVersion.class);

    private static final String ISRC = "isrc";
    private static final String DEAL_RELEASE_REFERENCE = "dealReleaseReference";
    private static final String X_PATH_IMAGE_FILE_INDEX = "xPathImageFileIndex";
    private static final String X_PATH_FILE_INDEX = "xPathFileIndex";

    protected final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    private static XPathCompiler xPathCompiler;
    private static XdmNode xdmNode;

    private XPathSelector proprietaryIdXPathSelector;
    private XPathSelector albumXPathSelector;
    private XPathSelector subTitleXPathSelector;
    private XPathSelector startDateXPathSelector;
    private XPathSelector releaseReferenceXPathSelector;
    private XPathSelector dealReferenceXPathSelector;
    private XPathSelector fileCountXPathSelector;
    private XPathSelector takeDownXPathSelector;
    private XPathSelector priceTypeXPathSelector;
    private XPathSelector currencyCodeXPathSelector;
    private XPathSelector durationXPathSelector;
    private XPathSelector wholesalePricePerUnitXPathSelector;
    private XPathSelector parentalWarningTypeXPathSelector;
    private XPathSelector imageCountXPathSelector;
    private XPathSelector xmlXPathSelector;
    private XPathSelector imageFileNameXPathSelector;
    private XPathSelector fileNameXPathSelector;
    private XPathSelector fileHashSumXPathSelector;
    private XPathSelector isPreviewFileXPathSelector;
    private XPathSelector userDefinedValueFileXPathSelector;
    private XPathSelector imageFileHashSumXPathSelector;
    private XPathSelector audioCodecTypeFileXPathSelector;
    private XPathSelector imageFileHashSumAlgorithmTypeXPathSelector;

    private QName isrcQName;
    private QName dealReleaseReferenceQName;
    private QName xPathImageFileIndexQName;
    private QName xPathFileIndexQName;

    private String isrc;
    private String productCode;
    private String dealReleaseReference;
    private int xPathImageFileIndex;
    private int xPathFileIndex;
    private XPathSelector priceRangeXPathSelector;

    private List<DropTerritory> createTerritory(Element details, String distributor, String label) throws SaxonApiException, ParseException {
        List<DropTerritory> res = new ArrayList<DropTerritory>();
        List<Element> territoryCode = details.getChildren("TerritoryCode");
        for (Element e : territoryCode)
            res.add(new DropTerritory(e.getText())
                    .addCurrency(evaluate(currencyCodeXPathSelector))
                    .addDistributor(distributor)
                    .addLabel(label)
                    .addPrice(getPrice())
                    .addPriceCode(getPriceCode())
                    .addPublisher(null)
                    .addReportingId(isrc)
                    .addDealReference(evaluate(dealReferenceXPathSelector))
                    .addStartDate(YYYY_MM_DD.parse(evaluate(startDateXPathSelector)))
                    .addTakeDown(getTakeDown())
            );
        return res;
    }

    private String getPriceCode() throws SaxonApiException {
        String priceType = evaluate(priceTypeXPathSelector);
        if (isEmpty(priceType)) return getPriceRange(priceRangeXPathSelector);
        return null;
    }

    private String getPriceRange(XPathSelector priceRangeXPathSelector) throws SaxonApiException {
        String priceRange = evaluate(priceRangeXPathSelector);
        if(isNotEmpty(priceRange)) return priceRange;
        return null;
    }

    private List<DropAssetFile> createFiles(String fileRoot) throws SaxonApiException {
        int filesCount = getFilesCount();
        List<DropAssetFile> imageDropAssetFiles = createImageDropAssetFiles(fileRoot);

        List<DropAssetFile> dropAssetFiles = new ArrayList<DropAssetFile>(filesCount + imageDropAssetFiles.size());

        for (int i = 0; i < filesCount; i++) {
            DropAssetFile dropAssetFile = new DropAssetFile();
            dropAssetFile.isrc = isrc;
            xPathFileIndex = i + 1;
            dropAssetFile.file = getAssetFile(fileRoot, evaluate(fileNameXPathSelector));
            dropAssetFile.duration = getDuration();
            dropAssetFile.md5 = evaluate(fileHashSumXPathSelector);
            dropAssetFile.type = getType();

            dropAssetFiles.add(dropAssetFile);
        }
        dropAssetFiles.addAll(imageDropAssetFiles);

        return dropAssetFiles;
    }

    private List<DropAssetFile> createImageDropAssetFiles(String fileRoot) throws SaxonApiException {
        int imageCount = getImageCount();

        List<DropAssetFile> dropAssetFiles = new ArrayList<DropAssetFile>(imageCount);

        for (int i = 0; i < imageCount; i++) {
            DropAssetFile dropAssetFile = new DropAssetFile();
            xPathImageFileIndex = i + 1;
            dropAssetFile.file = getAssetFile(fileRoot, evaluate(imageFileNameXPathSelector));
            dropAssetFile.md5 = getImageMD5();
            dropAssetFile.type = IMAGE;

            dropAssetFiles.add(dropAssetFile);
        }
        return dropAssetFiles;
    }

    private int getFilesCount() throws SaxonApiException {
        return parseInt(evaluate(fileCountXPathSelector));
    }

    private String getImageMD5() throws SaxonApiException {
        if ("MD5".equals(getImageFileHashSumAlgorithmType()))
            return evaluate(imageFileHashSumXPathSelector);
        return null;
    }

    private String getImageFileHashSumAlgorithmType() throws SaxonApiException {
        return evaluate(imageFileHashSumAlgorithmTypeXPathSelector);
    }

    private int getImageCount() throws SaxonApiException {
        return parseInt(evaluate(imageCountXPathSelector));
    }

    private boolean getExplicit() throws SaxonApiException {
        return "Explicit".equals(evaluate(parentalWarningTypeXPathSelector));
    }

    private Integer getDuration() throws SaxonApiException {
        return getDuration(evaluate(durationXPathSelector));
    }

    private Float getPrice() throws SaxonApiException {
        String price = evaluate(wholesalePricePerUnitXPathSelector);
        if (isNotBlank(price)) return Float.parseFloat(price);
        return null;
    }

    private boolean getTakeDown() throws SaxonApiException {
        if (isNotNull(evaluate(takeDownXPathSelector))) return Boolean.parseBoolean(evaluate(takeDownXPathSelector));
        return false;
    }

    private FileType getType() throws SaxonApiException {
        String isPreview = evaluate(isPreviewFileXPathSelector);

        FileType fileType;
        if (isEmpty(isPreview) || isPreview.equals("false")) {
            String audioCodecType = evaluate(audioCodecTypeFileXPathSelector);
            String userDefinedValue = evaluate(userDefinedValueFileXPathSelector);
            if (isNull(audioCodecType) || audioCodecType.equals("MP3") || (audioCodecType.equals("UserDefined") && "MP3".equals(userDefinedValue))) {
                fileType = DOWNLOAD;
            } else {
                fileType = MOBILE;
            }
        } else {
            fileType = PREVIEW;
        }
        return fileType;
    }

    private XdmValue getXmlValue(XPathSelector xPathSelector) throws SaxonApiException {
        xPathSelector.setContextItem(xdmNode);
        xPathSelector.setVariable(isrcQName, new XdmAtomicValue(isrc));
        xPathSelector.setVariable(dealReleaseReferenceQName, new XdmAtomicValue(dealReleaseReference));
        xPathSelector.setVariable(xPathFileIndexQName, new XdmAtomicValue(xPathFileIndex));
        xPathSelector.setVariable(xPathImageFileIndexQName, new XdmAtomicValue(xPathImageFileIndex));
        return xPathSelector.evaluate();
    }

    private String getXml() throws SaxonApiException {
        return getXmlValue(xmlXPathSelector).toString();
    }

    private String getDropTrackKey() {
        return Joiner.on(productCode).join(isrc, getClass());
    }

    private String getSubTitle() throws SaxonApiException {
        String subTile = evaluate(subTitleXPathSelector);
        if (isNotEmpty(subTile)) return subTile;
        return null;
    }

    private void prepareXPath(File file) throws SaxonApiException {
        Processor processor = new Processor(false);
        xPathCompiler = processor.newXPathCompiler();

        isrcQName = new QName(ISRC);
        dealReleaseReferenceQName = new QName(DEAL_RELEASE_REFERENCE);
        dealReleaseReferenceQName = new QName(DEAL_RELEASE_REFERENCE);
        xPathImageFileIndexQName = new QName(X_PATH_IMAGE_FILE_INDEX);
        xPathFileIndexQName = new QName(X_PATH_FILE_INDEX);

        xPathCompiler.declareVariable(isrcQName);
        xPathCompiler.declareVariable(dealReleaseReferenceQName);
        xPathCompiler.declareVariable(xPathImageFileIndexQName);
        xPathCompiler.declareVariable(xPathFileIndexQName);

        DocumentBuilder builder = processor.newDocumentBuilder();
        xdmNode = builder.build(file);

        compileXPathExpressions();
    }

    private void compileXPathExpressions() throws SaxonApiException {
        proprietaryIdXPathSelector = xPathCompiler.compile("//ResourceList/SoundRecording/SoundRecordingId[ISRC eq $" + ISRC + "]/ProprietaryId").load();
        albumXPathSelector = xPathCompiler.compile("//ReleaseList/Release[ReleaseType='Album']/ReferenceTitle/TitleText").load();
        subTitleXPathSelector = xPathCompiler.compile("string-join(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/SubTitle,'/')").load();
        startDateXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/ValidityPeriod/StartDate").load();
        releaseReferenceXPathSelector = xPathCompiler.compile("//ReleaseList/Release[ReleaseId/ISRC eq $" + ISRC + "]/ReleaseReference").load();
        dealReferenceXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealReference").load();
        fileCountXPathSelector = xPathCompiler.compile("count(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File)").load();
        takeDownXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/TakeDown").load();
        priceTypeXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/PriceType").load();
        priceRangeXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/PriceInformation/PriceRangeType").load();
        currencyCodeXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/CurrencyCode").load();
        durationXPathSelector = xPathCompiler.compile("//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/Duration").load();
        wholesalePricePerUnitXPathSelector = xPathCompiler.compile("//DealList/ReleaseDeal[DealReleaseReference eq $" + DEAL_RELEASE_REFERENCE + "]/Deal/DealTerms/PriceInformation/WholesalePricePerUnit").load();
        parentalWarningTypeXPathSelector = xPathCompiler.compile("//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/ParentalWarningType").load();
        imageCountXPathSelector = xPathCompiler.compile("count(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File)").load();
        xmlXPathSelector = xPathCompiler.compile("//ReleaseList/Release[ReleaseId/ISRC eq $" + ISRC + "]").load();
        imageFileNameXPathSelector = xPathCompiler.compile("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/FileName)[$" + X_PATH_IMAGE_FILE_INDEX + "]").load();
        imageFileHashSumXPathSelector = xPathCompiler.compile("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSum)[$" + X_PATH_IMAGE_FILE_INDEX + "]").load();
        imageFileHashSumAlgorithmTypeXPathSelector = xPathCompiler.compile("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSumAlgorithmType)[$" + X_PATH_IMAGE_FILE_INDEX + "]").load();
        fileNameXPathSelector = xPathCompiler.compile("(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/FileName)[$" + X_PATH_FILE_INDEX + "]").load();
        fileHashSumXPathSelector = xPathCompiler.compile("(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/HashSum/HashSum)[$" + X_PATH_FILE_INDEX + "]").load();
        isPreviewFileXPathSelector = xPathCompiler.compile("(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/IsPreview)[$" + X_PATH_FILE_INDEX + "]").load();
        audioCodecTypeFileXPathSelector = xPathCompiler.compile("(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType)[$" + X_PATH_FILE_INDEX + "]").load();
        userDefinedValueFileXPathSelector = xPathCompiler.compile("(//ResourceList/SoundRecording[SoundRecordingId/ISRC eq $" + ISRC + "]/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType/@UserDefinedValue)[$" + X_PATH_FILE_INDEX + "]").load();
    }

    private String getProprietaryId() throws SaxonApiException {
        return evaluate(proprietaryIdXPathSelector);
    }

    private String evaluate(XPathSelector xPathSelector) throws SaxonApiException {
        XdmValue children = getXmlValue(xPathSelector);
        if (children.size() > 0) {
            return children.itemAt(0).getStringValue();
        }
        return null;
    }

    @Override
    protected List<DropData> getDrops(File folder, boolean auto) {
        List<DropData> result = new ArrayList<DropData>();
        File[] content = folder.listFiles();
        boolean deliveryComplete = false;
        boolean processed = false;
        for (File file : content) {
            if (isDirectory(file)) {
                result.addAll(getDrops(file, auto));
            } else if (DELIVERY_COMPLETE.equals(file.getName())) {
                deliveryComplete = true;
            } else if (INGEST_ACK.equals(file.getName())) {
                processed = true;
            } else if (auto && AUTO_INGEST_ACK.equals(file.getName())) {
                processed = true;
            }
        }
        if (deliveryComplete && !processed) {
            LOGGER.debug("Adding [{}] to drops", folder.getAbsolutePath());
            DropData drop = new DropData();
            drop.name = folder.getAbsolutePath();
            drop.date = new Date(folder.lastModified());

            result.add(drop);
        }
        return result;
    }

    public AbsoluteParserCleanerWithPreCompiledXPathVersion(String root) throws FileNotFoundException {
        super(root);
    }

    @Override
    public Map<String, DropTrack> loadXml(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        try {
            if (!file.exists()) return res;
            prepareXPath(file);

            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element root = document.getRootElement();
            String distributor = getDistributor(root);
            List<Element> sounds = root.getChild("ResourceList").getChildren("SoundRecording");
            String album = evaluate(albumXPathSelector);
            Type actionType = getActionType(root);

            for (Element node : sounds) {
                isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
                dealReleaseReference = evaluate(releaseReferenceXPathSelector);
                productCode = getProprietaryId();

                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String artist = details.getChild("DisplayArtist").getChild("PartyName").getChildText("FullName");
                String title = details.getChild("Title").getChildText("TitleText");
                String genre = details.getChild("Genre").getChildText("GenreText");
                String copyright = details.getChild("PLine").getChildText("PLineText");
                String label = details.getChildText("LabelName");
                String year = details.getChild("PLine").getChildText("Year");
                List<DropTerritory> territories = createTerritory(details, distributor, label);
                List<DropAssetFile> files = createFiles(file.getParent());

                res.put(getDropTrackKey(), new DropTrack()
                        .addType(actionType)
                        .addProductCode(productCode)
                        .addTitle(title)
                        .addSubTitle(getSubTitle())
                        .addArtist(artist)
                        .addGenre(genre)
                        .addCopyright(copyright)
                        .addLabel(label)
                        .addYear(year)
                        .addIsrc(isrc)
                        .addPhysicalProductId(isrc)
                        .addInfo(null)
                        .addExplicit(getExplicit())
                        .addProductId(isrc)
                        .addTerritories(territories)
                        .addFiles(files)
                        .addAlbum(album)
                        .addXml(getXml())
                );
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return res;
    }
}
