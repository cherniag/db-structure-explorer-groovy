package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.primitives.Ints.checkedCast;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.*;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.MOBILE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.PREVIEW;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.UPDATE;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class AbsoluteParser extends DDEXParser {

    protected final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger LOGGER = LoggerFactory.getLogger(AbsoluteParser.class);

    public AbsoluteParser(String root) throws FileNotFoundException {
        super(root);
        LOGGER.info("Absolute parser loading from [{}]" , root);
    }

    @Override
    public Map<String, DropTrack> loadXml(File file) {
        HashMap<String, DropTrack> res = new HashMap<String, DropTrack>();
        if (!file.exists()) return res;

        String fileRoot = file.getParent();

        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(file);
            Element root = document.getRootElement();
            String distributor = root.getChild("MessageHeader").getChild("MessageSender").getChild("PartyName").getChildText("FullName");
            List<Element> sounds = root.getChild("ResourceList").getChildren("SoundRecording");
            List<Element> releases = root.getChild("ReleaseList").getChildren("Release");
            List<Element> deals = root.getChild("DealList").getChildren("ReleaseDeal");
            String album = getAlbum(document);

            for (Element node : sounds) {
                String isrc = node.getChild("SoundRecordingId").getChildText("ISRC");
                Element details = node.getChild("SoundRecordingDetailsByTerritory");
                String artist = details.getChild("DisplayArtist").getChild("PartyName").getChildText("FullName");
                String title = details.getChild("Title").getChildText("TitleText");
                String subTitle = details.getChildText("ParentalWarningType");
                String genre = details.getChild("Genre").getChildText("GenreText");
                String copyright = details.getChild("PLine").getChildText("PLineText");
                String label = details.getChildText("LabelName");
                String year = details.getChild("PLine").getChildText("Year");
                String releaseReference = getReleaseReference(document, isrc);
                List<DropTerritory> territories = createTerritory(document, details, distributor, label, isrc, releaseReference);
                List<DropAssetFile> files = createFiles(document, isrc, fileRoot);

                res.put(getDropTrackKey(isrc), new DropTrack()
                        .addType(getActionType(document))
                        .addProductCode(getProprietaryId(document, isrc))
                        .addTitle(title)
                        .addSubTitle(subTitle)
                        .addArtist(artist)
                        .addGenre(genre)
                        .addCopyright(copyright)
                        .addLabel(label)
                        .addYear(year)
                        .addIsrc(isrc)
                        .addPhysicalProductId(isrc)
                        .addInfo("")
                        .addExists(true)
                        .addExplicit(false)
                        .addProductId(isrc)
                        .addTerritories(territories)
                        .addFiles(files)
                );
            }

            for (Element node : releases) {
                String isrc = node.getChild("ReleaseId").getChildText("ISRC");
                if (isrc == null) continue;
                DropTrack track = res.get(getDropTrackKey(isrc));

                track.addAlbum(album);
            }
        } catch (JDOMException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return res;
    }

    private List<DropAssetFile> createFiles(Document doc, String isrc, String fileRoot) throws JDOMException {
        int filesCount = getFilesCount(doc, isrc);
        List<DropAssetFile> dropAssetFiles = new ArrayList<DropAssetFile>(filesCount);

        for (int i = 0; i < filesCount; i++) {
            DropAssetFile dropAssetFile = new DropAssetFile();
            dropAssetFile.isrc = isrc;
            int xPathFileIndex = i + 1;
            dropAssetFile.file = getAssetFile(fileRoot, getFileName(doc, isrc, xPathFileIndex));
            dropAssetFile.duration = getDuration(doc, isrc);
            dropAssetFile.md5 = getMD5(doc, isrc, xPathFileIndex);
            dropAssetFile.type = getType(doc, isrc, xPathFileIndex);

            dropAssetFiles.add(dropAssetFile);
        }
        return dropAssetFiles;
    }

    private List<DropTerritory> createTerritory(Document doc, Element details, String distributor, String label, String isrc, String releaseReference) {
        try{
        List<DropTerritory> res = new ArrayList<DropTerritory>();
        List<Element> territoryCode = details.getChildren("TerritoryCode");
        for (Element e : territoryCode)
            res.add(new DropTerritory(e.getText())
                    .addCurrency("GBP")
                    .addDistributor(distributor)
                    .addLabel(label)
                    .addPrice(0.0f)
                    .addPriceCode("0.0")
                    .addPublisher("")
                    .addReportingId(isrc)
                    .addDealReference(getDealReference(doc, releaseReference))
                    .addStartDate(YYYY_MM_DD.parse(getStartDate(doc, releaseReference)))
            );
        return res;
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String getAlbum(Document doc) throws JDOMException {
        return evaluate(doc, "/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Album']/ReferenceTitle/TitleText");
    }

    private String getStartDate(Document doc, String dealReleaseReference) throws JDOMException {
        return evaluate(doc, "/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='" + dealReleaseReference + "']/Deal/DealTerms/ValidityPeriod/StartDate");
    }

    private String getReleaseReference(Document doc, String isrc) throws JDOMException {
        return evaluate(doc, "/ern:NewReleaseMessage/ReleaseList/Release[ReleaseId/ISRC='"+isrc+"']/ReleaseReference");
    }

    private String getDealReference(Document doc, String dealReleaseReference) throws JDOMException {
        return evaluate(doc, "/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealReference");
    }

    private int getFilesCount(Document doc, String isrc) throws JDOMException {
        XPath xPath = XPath.newInstance("count(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File)");
        xPath.addNamespace("ern", "http://ddex.net/xml/2010/ern-main/312");
        return ((Double) xPath.selectSingleNode(doc)).intValue();

    }

    private String getFileName(Document doc, String isrc, int index) throws JDOMException {
        return evaluate(doc, "(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/FileName)["+index+"]");
    }

    private Integer getDuration(Document doc, String isrc) throws JDOMException {
        String duration = evaluate(doc, "/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/Duration");

        PeriodParser periodParser = ISOPeriodFormat.standard().getParser();
        ReadWritablePeriod readWritablePeriod = new MutablePeriod();
        if (periodParser.parseInto(readWritablePeriod, duration, 0, null)>0){
            return checkedCast(readWritablePeriod.toPeriod().toStandardDuration().getStandardSeconds());
        };
        return null;
    }

    private Type getActionType(Document doc) throws JDOMException {
        return "UpdateMessage".equals(evaluate(doc, "/ern:NewReleaseMessage/UpdateIndicator")) ? UPDATE : INSERT;
    }

    private String getProprietaryId(Document doc, String isrc) throws JDOMException {
        return evaluate(doc, "/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingId[ISRC='"+isrc+"']/ProprietaryId");
    }

    private String getMD5(Document doc, String isrc, int index) throws JDOMException {
        return evaluate(doc, "(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/HashSum/HashSum)["+index+"]");
    }

    private FileType getType(Document doc, String isrc, int index) throws JDOMException {
        String isPreview = evaluate(doc, "(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/IsPreview)["+index+"]");

        FileType fileType;
        if (isEmpty(isPreview) || isPreview.equals("false")){
            String audioCodecType = evaluate(doc, "(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType)["+index+"]");
            XPath xPath = XPath.newInstance("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType/@UserDefinedValue)["+index+"]");
            xPath.addNamespace("ern", "http://ddex.net/xml/2010/ern-main/312");
            String userDefinedValue = ((Attribute)xPath.selectSingleNode(doc)).getValue();
            if (isNull(audioCodecType)|| audioCodecType.equals("MP3")|| (audioCodecType.equals("UserDefined") && "MP3".equals(userDefinedValue))){
                fileType = DOWNLOAD;
            }else{
                fileType = MOBILE;
            }
        }else {
            fileType = PREVIEW;
        }
        return fileType;
    }

    private String evaluate(Document doc, String xPathExpression) throws JDOMException {
        XPath xPath = XPath.newInstance(xPathExpression);
        xPath.addNamespace("ern", "http://ddex.net/xml/2010/ern-main/312");
        Element singleNode = (Element) xPath.selectSingleNode(doc);
        if (isNull(singleNode)) return null;
        return singleNode.getValue();
    }

    private String getDropTrackKey(String isrc) {
        return Joiner.on('_').join(isrc, getClass().getSimpleName());
    }

    @Override
    public Map<String, DropTrack> ingest(DropData drop) {
        return null;
    }

    @Override
    public List<DropData> getDrops(boolean auto) {
        return null;
    }

    @Override
    public void getIds(Element release, DropTrack track, List<DropAssetFile> files) {
    }

    @Override
    public boolean checkAlbum(String type) {
        if ("Album".equals(type)) {
            LOGGER.info("Album for " + type);
            return true;
        }
        LOGGER.info("Track for " + type);
        return false;

    }
}
