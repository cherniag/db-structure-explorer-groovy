package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.trackrepo.ingest.DDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.MOBILE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.PREVIEW;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.lang.Integer.parseInt;

import com.google.common.base.Joiner;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class AbsoluteParserCleanerVersion extends DDEXParser {

    protected final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParserCleanerVersion.class);
    private static XPathCompiler xPathCompiler;
    private static XdmNode xdmNode;

    public AbsoluteParserCleanerVersion(String root) throws FileNotFoundException {
        super(root);
    }

    private List<DropTerritory> createTerritory(Element details, String distributor, String label, String isrc, String releaseReference) {
        try {
            List<DropTerritory> res = new ArrayList<DropTerritory>();
            List<Element> territoryCode = details.getChildren("TerritoryCode");
            for (Element e : territoryCode) {
                res.add(new DropTerritory(e.getText()).addCurrency(getCurrency(releaseReference)).addDistributor(distributor).addLabel(label).addPrice(getPrice(releaseReference))
                                                      .addPriceCode(getPriceCode(releaseReference)).addPublisher(null).addReportingId(isrc).addDealReference(getDealReference(releaseReference))
                                                      .addStartDate(YYYY_MM_DD.parse(getStartDate(releaseReference))).addTakeDown(getTakeDown(releaseReference)));
            }
            return res;
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String getPriceCode(String dealReleaseReference) throws SaxonApiException {
        String priceType = getPriceType(dealReleaseReference);
        if (isEmpty(priceType)) {
            return getPriceRange(dealReleaseReference);
        }
        return priceType;
    }

    private String getPriceRange(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/PriceInformation/PriceRangeType");
    }

    private List<DropAssetFile> createFiles(String isrc, String fileRoot) throws SaxonApiException {
        int filesCount = getFilesCount(isrc);
        List<DropAssetFile> imageDropAssetFiles = createImageDropAssetFiles(fileRoot);

        List<DropAssetFile> dropAssetFiles = new ArrayList<DropAssetFile>(filesCount + imageDropAssetFiles.size());

        for (int i = 0; i < filesCount; i++) {
            DropAssetFile dropAssetFile = new DropAssetFile();
            dropAssetFile.isrc = isrc;
            int xPathFileIndex = i + 1;
            dropAssetFile.file = getAssetFile(fileRoot, getFileName(isrc, xPathFileIndex));
            dropAssetFile.duration = getDurationBy(isrc);
            dropAssetFile.md5 = getMD5(isrc, xPathFileIndex);
            dropAssetFile.type = getType(isrc, xPathFileIndex);

            dropAssetFiles.add(dropAssetFile);
        }
        dropAssetFiles.addAll(imageDropAssetFiles);

        return dropAssetFiles;
    }

    private List<DropAssetFile> createImageDropAssetFiles(String fileRoot) throws SaxonApiException {
        int filesCount = getImageCount();

        List<DropAssetFile> dropAssetFiles = new ArrayList<DropAssetFile>(filesCount);

        for (int i = 0; i < filesCount; i++) {
            DropAssetFile dropAssetFile = new DropAssetFile();
            int xPathFileIndex = i + 1;
            dropAssetFile.file = getAssetFile(fileRoot, getImageFileName(xPathFileIndex));
            dropAssetFile.md5 = getImageMD5(xPathFileIndex);
            dropAssetFile.type = IMAGE;

            dropAssetFiles.add(dropAssetFile);
        }
        return dropAssetFiles;
    }

    private String getAlbum() throws SaxonApiException {
        return evaluate("//ReleaseList/Release[ReleaseType='Album']/ReferenceTitle/TitleText");
    }

    private String getSubTitle(String isrc) throws SaxonApiException {
        String subTitle = evaluate("string-join(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/SubTitle,'/')");
        if (isNotEmpty(subTitle)) {
            return subTitle;
        }
        return null;
    }

    private String getStartDate(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/ValidityPeriod/StartDate");
    }

    private String getReleaseReference(String isrc) throws SaxonApiException {
        return evaluate("//ReleaseList/Release[ReleaseId/ISRC='" + isrc + "']/ReleaseReference");
    }

    private String getDealReference(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealReference");
    }

    private int getFilesCount(String isrc) throws SaxonApiException {
        return parseInt(evaluate("count(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File)"));
    }

    private String getImageMD5(int index) throws SaxonApiException {
        if ("MD5".equals(getImageFileHashSumAlgorithmType(index))) {
            return getImageFileHashSum(index);
        }
        return null;
    }

    private String getImageFileName(int index) throws SaxonApiException {
        return evaluate("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/FileName)[" + index + "]");
    }

    private String getImageFileHashSumAlgorithmType(int index) throws SaxonApiException {
        return evaluate("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSumAlgorithmType)[" + index + "]");
    }

    private String getImageFileHashSum(int index) throws SaxonApiException {
        return evaluate("(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSum)[" + index + "]");
    }

    private int getImageCount() throws SaxonApiException {
        return parseInt(evaluate("count(//ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File)"));
    }

    private boolean getExplicit(String isrc) throws SaxonApiException {
        return "Explicit".equals(evaluate("//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/ParentalWarningType"));
    }

    private String getFileName(String isrc, int index) throws SaxonApiException {
        return evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/FileName)[" + index + "]");
    }

    private Integer getDurationBy(String isrc) throws SaxonApiException {
        return getDuration(evaluate("//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/Duration"));
    }

    private String getProprietaryId(String isrc) throws SaxonApiException {
        return evaluate("//ResourceList/SoundRecording/SoundRecordingId[ISRC='" + isrc + "']/ProprietaryId");
    }

    private String getMD5(String isrc, int index) throws SaxonApiException {
        return evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/HashSum/HashSum)[" + index + "]");
    }

    private Float getPrice(String dealReleaseReference) throws SaxonApiException {
        String price = evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit");
        if (isNotBlank(price)) {
            return Float.parseFloat(price);
        }
        return null;
    }

    private String getCurrency(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/CurrencyCode");
    }

    private String getPriceType(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/PriceType");
    }

    private boolean getTakeDown(String dealReleaseReference) throws SaxonApiException {
        String takeDown = evaluate("//DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/TakeDown");
        if (isNotNull(takeDown)) {
            return Boolean.parseBoolean(takeDown);
        }
        return false;
    }

    private FileType getType(String isrc, int index) throws SaxonApiException {
        String isPreview = evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/IsPreview)[" + index + "]");

        FileType fileType;
        if (isEmpty(isPreview) || isPreview.equals("false")) {
            String audioCodecType =
                evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType)[" + index + "]");
            String userDefinedValue = evaluate(
                "(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType/@UserDefinedValue)[" + index +
                "]");
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

    private String getXml(String isrc) throws SaxonApiException {
        return getXmlValue("//ReleaseList/Release[ReleaseId/ISRC='" + isrc + "']").toString();
    }

    private XdmValue getXmlValue(String xPathExpression) throws SaxonApiException {
        XPathSelector selector = xPathCompiler.compile(xPathExpression).load();
        selector.setContextItem(xdmNode);
        return selector.evaluate();
    }

    private String getDropTrackKey(String isrc, String productCode) {
        return Joiner.on(productCode).join(isrc, getClass());
    }

    private String evaluate(String expression) throws SaxonApiException {
        XdmValue children = getXmlValue(expression);
        if (children.size() > 0) {
            return children.itemAt(0).getStringValue();
        }
        return null;
    }

    private void createDoc(File file) throws SaxonApiException {
        Processor proc = new Processor(false);
        xPathCompiler = proc.newXPathCompiler();
        DocumentBuilder builder = proc.newDocumentBuilder();

        xdmNode = builder.build(file);
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
            }
            else if (DELIVERY_COMPLETE.equals(file.getName())) {
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

            result.add(drop);
        }
        return result;
    }

    @Override
    public Map<String, DropTrack> loadXml(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        try {
            if (!file.exists()) {
                return res;
            }
            createDoc(file);

            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element root = document.getRootElement();
            String distributor = getDistributor(root);
            List<Element> sounds = root.getChild("ResourceList").getChildren("SoundRecording");
            String album = getAlbum();
            Type actionType = getActionType(root);

            for (Element node : sounds) {
                String isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String artist = details.getChild("DisplayArtist").getChild("PartyName").getChildText("FullName");
                String title = details.getChild("Title").getChildText("TitleText");
                String genre = details.getChild("Genre").getChildText("GenreText");
                String copyright = details.getChild("PLine").getChildText("PLineText");
                String label = details.getChildText("LabelName");
                String year = details.getChild("PLine").getChildText("Year");
                String releaseReference = getReleaseReference(isrc);
                List<DropTerritory> territories = createTerritory(details, distributor, label, isrc, releaseReference);
                List<DropAssetFile> files = createFiles(isrc, file.getParent());
                String proprietaryId = getProprietaryId(isrc);

                res.put(getDropTrackKey(isrc, proprietaryId),
                        new DropTrack().addType(actionType).addProductCode(proprietaryId).addTitle(title).addSubTitle(getSubTitle(isrc)).addArtist(artist).addGenre(genre).addCopyright(copyright)
                                       .addLabel(label).addYear(year).addIsrc(isrc).addPhysicalProductId(isrc).addInfo(null).addExplicit(getExplicit(isrc)).addProductId(isrc)
                                       .addTerritories(territories).addFiles(files).addAlbum(album).addXml(getXml(isrc)));
            }
        }
        catch (JDOMException e) {
            LOGGER.error(e.getMessage());
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        catch (SaxonApiException e) {
            LOGGER.error(e.getMessage());
        }
        return res;
    }
}
