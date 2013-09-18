package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import net.sf.saxon.s9api.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.primitives.Ints.checkedCast;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.UPDATE;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class AbsoluteParser extends DDEXParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParser.class);

    protected final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private Document document;
    private static XPathCompiler xpath;
    private static XdmNode doc;

    private List<DropTerritory> createTerritory(Element details, String distributor, String label, String isrc, String releaseReference) {
        try {
            List<DropTerritory> res = new ArrayList<DropTerritory>();
            List<Element> territoryCode = details.getChildren("TerritoryCode");
            for (Element e : territoryCode)
                res.add(new DropTerritory(e.getText())
                        .addCurrency(getCurrency(releaseReference))
                        .addDistributor(distributor)
                        .addLabel(label)
                        .addPrice(getPrice(releaseReference))
                        .addPriceCode(getPriceType(releaseReference))
                        .addPublisher(null)
                        .addReportingId(isrc)
                        .addDealReference(getDealReference(releaseReference))
                        .addStartDate(YYYY_MM_DD.parse(getStartDate(releaseReference)))
                        .addTakeDown(getTakeDown(releaseReference))
                );
            return res;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
            dropAssetFile.duration = getDuration(isrc);
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
        return evaluate("string-join(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/SubTitle,'/')");
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
        if ("MD5".equals(getImageFileHashSumAlgorithmType(index)))
            return getImageFileHashSum(index);
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
        return "Explicit".equals(evaluate("//ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/ParentalWarningType"));
    }

    private String getFileName(String isrc, int index) throws SaxonApiException {
        return evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/FileName)[" + index + "]");
    }

    private Integer getDuration(String isrc) throws SaxonApiException {
        String duration = evaluate("//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/Duration");

        PeriodParser periodParser = ISOPeriodFormat.standard().getParser();
        ReadWritablePeriod readWritablePeriod = new MutablePeriod();
        if (periodParser.parseInto(readWritablePeriod, duration, 0, null) > 0) {
            return checkedCast(readWritablePeriod.toPeriod().toStandardDuration().getStandardSeconds());
        }
        return null;
    }

    private String getProprietaryId(String isrc) throws SaxonApiException {
        return evaluate("//ResourceList/SoundRecording/SoundRecordingId[ISRC='" + isrc + "']/ProprietaryId");
    }

    private String getMD5(String isrc, int index) throws SaxonApiException {
        return evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/HashSum/HashSum)[" + index + "]");
    }

    private Float getPrice(String dealReleaseReference) throws SaxonApiException {
        String price = evaluate("//DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit");
        if (isNotBlank(price)) return Float.parseFloat(price);
        return null;
    }

    private String getCurrency(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/CurrencyCode");
    }

    private String getPriceType(String dealReleaseReference) throws SaxonApiException {
        return evaluate("//DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/PriceType");
    }

    private boolean getTakeDown(String dealReleaseReference) throws SaxonApiException {
        String takeDown = evaluate("//DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/TakeDown");
        if(isNotNull(takeDown)) return Boolean.parseBoolean(takeDown);
        return false;
    }

    private FileType getType(String isrc, int index) throws SaxonApiException {
        String isPreview = evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/IsPreview)[" + index + "]");

        FileType fileType;
        if (isEmpty(isPreview) || isPreview.equals("false")) {
            String audioCodecType = evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType)[" + index + "]");
            String userDefinedValue = evaluate("(//ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType/@UserDefinedValue)[" + index + "]");
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

    private String getXml(String isrc) throws SaxonApiException {
        return getXmlValue("//ReleaseList/Release[ReleaseId/ISRC='" + isrc + "']").toString();
    }

    private XdmValue getXmlValue(String xPathExpression) throws SaxonApiException {
        XPathSelector selector = xpath.compile(xPathExpression).load();
        selector.setContextItem(doc);
        return selector.evaluate();
    }

    private String getDropTrackKey(String isrc, String productCode) {
        return Joiner.on(productCode).join(isrc, getClass());
    }

    private String evaluate(String expression) throws SaxonApiException {
        XdmValue children = getXmlValue(expression);
        if(children.size()>0){
            return children.itemAt(0).getStringValue();
        }
        return null;
    }

    private void createDoc(File file) throws SaxonApiException {
        Processor proc = new Processor(false);
        xpath = proc.newXPathCompiler();
        DocumentBuilder builder = proc.newDocumentBuilder();

        doc = builder.build(file);
    }

    public AbsoluteParser(String root) throws FileNotFoundException {
        super(root);
    }

    //@Override
    public Map<String, DropTrack> loadXm1(File file) {
        Map<String, DropTrack> res = new HashMap<String, DropTrack>();
        try {
            createDoc(file);

            res = super.loadXml(file);
        } catch (SaxonApiException e) {
            LOGGER.error(e.getMessage());
        }
        return res;
    }

    @Override
    public Map<String, DropTrack> loadXml(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        try {
            if (!file.exists()) return res;
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

                res.put(getDropTrackKey(isrc, proprietaryId), new DropTrack()
                        .addType(actionType)
                        .addProductCode(proprietaryId)
                        .addTitle(title)
                        .addSubTitle(getSubTitle(isrc))
                        .addArtist(artist)
                        .addGenre(genre)
                        .addCopyright(copyright)
                        .addLabel(label)
                        .addYear(year)
                        .addIsrc(isrc)
                        .addPhysicalProductId(isrc)
                        .addInfo(null)
                        .addExplicit(getExplicit(isrc))
                        .addProductId(isrc)
                        .addTerritories(territories)
                        .addFiles(files)
                        .addAlbum(album)
                        .addXml(getXml(isrc))
                );
            }
        } catch (JDOMException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } catch (SaxonApiException e) {
            LOGGER.error(e.getMessage());
        }
        return res;
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

    @Override
    protected boolean checkAlbum(String type) {
        if ("Album".equals(type) || "SingleResourceRelease".equals(type)) {
            LOGGER.info("Album for [{}]", type);
            return true;
        }
        LOGGER.info("Track for [{}]", type);
        return false;
    }

    @Override
    protected boolean validDealUseType(Element dealTerms) {
        boolean validUseType = super.validDealUseType(dealTerms);
        if (!validUseType){
            Element commercialModelTypeElement = dealTerms.getChild("CommercialModelType");
            validUseType = "AsPerContract".equals(commercialModelTypeElement.getText());
        }

        return validUseType;
    }

    @Override
    protected void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
        String isrc = release.getChild("ReleaseId").getChildText("ISRC");
        try {
            track.productCode = getProprietaryId(isrc);
        } catch (SaxonApiException e) {
            LOGGER.error(e.getMessage());
        }
        track.physicalProductId = isrc;
        track.productId = isrc;
    }
}
