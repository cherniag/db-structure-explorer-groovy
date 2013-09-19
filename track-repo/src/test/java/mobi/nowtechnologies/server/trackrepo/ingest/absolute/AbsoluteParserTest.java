package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.ParserTest;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.hamcrest.Matchers;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.joda.time.MutablePeriod;
import org.joda.time.ReadWritablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodParser;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.google.common.primitives.Ints.checkedCast;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.shared.ObjectUtils.*;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.*;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.*;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AbsoluteParserTest extends ParserTest {

    private Map<String,DropTrack> resultDropTrackMap;
    private NodeList expectedTrackReleaseIdNodeList;
    private DropTrack resultDropTrack;
    private String expectedIsrc;
    private String expectedDistributor;
    private String expectedLabel;
    private int expectedResultDropTrackIndex;
    private String expectedAlbum;
    private String expectedReleaseReference;
    private String xmlFileParent;
    private int expectedTrackCount;
    private int expectedImageFileCount;
    private List<DropAssetFile> files;
    private int expectedFileCount;
    private Type expectedActionType;

    public void createParser() throws FileNotFoundException {
        parserFixture = new AbsoluteParserCleanerVersion("classpath:media/absolute/");
    }

    public void populateXmlPrefixMap() {
        xmlPrefixMap.put("ern", "http://ddex.net/xml/2010/ern-main/312");
    }

    @Test
    public void verifyThatAbsoluteParserReadBasicFieldCorrectly() throws Exception {
        //given
        xmlFile = new ClassPathResource("media/absolute/201307180007/5037128203551/absolute2.xml").getFile();

        //when
        resultDropTrackMap = parserFixture.loadXml(xmlFile);

        //then
        shouldParseCorrectly();
    }

    private void shouldParseCorrectly() throws Exception {
        document = getDocument();

        assertNotNull(resultDropTrackMap);
        assertThat(resultDropTrackMap.size(), is(getTrackReleaseCount()));

        expectedTrackReleaseIdNodeList = getTrackReleaseIdNodeList();
        expectedDistributor = getDistributor();
        expectedAlbum = getAlbum();
        expectedActionType = getActionType();

        for (expectedResultDropTrackIndex = 0; expectedResultDropTrackIndex < expectedTrackReleaseIdNodeList.getLength(); expectedResultDropTrackIndex++) {
            validateResultDropTrack();
        }
    }

    private void validateResultDropTrack() throws XpathException, ParseException, TransformerException, IOException, SAXException {
        expectedIsrc = getIsrc(expectedResultDropTrackIndex + 1);
        expectedLabel = getLabel(expectedIsrc);
        expectedImageFileCount = getImageCount();
        xmlFileParent = xmlFile.getParent();

        String proprietaryId = getProprietaryId(expectedIsrc);
        resultDropTrack = getResultDropTrack(expectedIsrc, proprietaryId);

        assertNotNull(resultDropTrack);
        assertXMLEqual(resultDropTrack.xml, getXml(expectedIsrc));

        assertThat(resultDropTrack.type, is(expectedActionType));
        assertThat(resultDropTrack.productCode, is(proprietaryId));
        assertThat(resultDropTrack.title, is(getTitleText(expectedIsrc)));
        assertThat(resultDropTrack.subTitle, is(getSubTitle(expectedIsrc)));
        assertThat(resultDropTrack.artist, is(getArtist(expectedIsrc)));
        //assertThat(resultDropTrack.genre, is(getGenre(expectedIsrc)));
        assertThat(resultDropTrack.copyright, is(getCopyright(expectedIsrc)));
        assertThat(resultDropTrack.label, is(expectedLabel));
        assertThat(resultDropTrack.isrc, is(expectedIsrc));
        assertThat(resultDropTrack.year, is(getYear(expectedIsrc)));
        assertThat(resultDropTrack.physicalProductId, is(expectedIsrc));
        assertThat(resultDropTrack.album, is(expectedAlbum));
        assertThat(resultDropTrack.info, is(nullValue()));
        assertThat(resultDropTrack.licensed, is(true));
        assertThat(resultDropTrack.explicit, is(getExplicit(expectedIsrc)));
        assertThat(resultDropTrack.productId, is(expectedIsrc));

        validateResultTerritories();
        validateResultFiles();
    }

    private void validateResultTerritories() throws XpathException, ParseException {
        List<DropTerritory> territories = resultDropTrack.getTerritories();

        assertNotNull(territories);
        assertThat(territories.size(), is(getTerritoryPerTrackCount(expectedIsrc)));

        expectedReleaseReference = getReleaseReference(expectedIsrc);

        for (int i = 0; i < territories.size(); i++) {
            DropTerritory territory = territories.get(i);

            assertThat(territory.country, is(getTerritoryPerTrack(expectedIsrc, i + 1)));
            assertThat(territory.currency, is(getCurrency(expectedReleaseReference)));
            assertThat(territory.dealReference, is(getDealReference(expectedReleaseReference)));
            assertThat(territory.distributor, is(expectedDistributor));
            assertThat(territory.label, is(expectedLabel));
            assertThat(territory.price, is(getPrice(expectedReleaseReference)));
            assertThat(territory.priceCode, is(getPriceType(expectedReleaseReference)));
            assertThat(territory.publisher, is(nullValue()));
            assertThat(territory.reportingId, is(expectedIsrc));
            assertThat(territory.startdate, is(YYYY_MM_DD.parse(getStartDate(expectedReleaseReference))));
            assertThat(territory.takeDown, is(getTakeDown(expectedReleaseReference)));
        }
    }

    private void validateResultFiles() throws XpathException, ParseException {
        files = resultDropTrack.getFiles();

        expectedTrackCount = getFilesCount(expectedIsrc);
        expectedFileCount = expectedTrackCount + expectedImageFileCount;

        assertNotNull(files);
        assertThat(files.size(), is(expectedFileCount));

        validateTrackFiles();
        validateImageFiles();
    }

    private void validateTrackFiles() throws XpathException, ParseException {
        for (int i = 0; i < expectedTrackCount; i++) {
            DropAssetFile asset = files.get(i);
            assertThat(asset.isrc, is(expectedIsrc));
            int xPathFileIndex = i + 1;
            assertThat(asset.file, is(getAssetFile(getFileName(expectedIsrc, xPathFileIndex))));
            //assertThat(asset.duration, is(getDuration(expectedIsrc)));
            assertThat(asset.md5, is(getMD5(expectedIsrc, xPathFileIndex)));
            assertThat(asset.type, is(getType(expectedIsrc, xPathFileIndex)));
        }
    }

    private void validateImageFiles () throws XpathException, ParseException {
        for (int i = expectedTrackCount; i < expectedFileCount; i++) {
            DropAssetFile asset = files.get(i);
            assertThat(asset.isrc, is(nullValue()));
            int xPathFileIndex = i - expectedTrackCount + 1;
            assertThat(asset.file, is(getAssetFile(getImageFileName(xPathFileIndex))));
            assertThat(asset.duration, is(nullValue()));
            assertThat(asset.md5, is(getImageMD5(xPathFileIndex)));
            assertThat(asset.type, is(IMAGE));
        }
    }

    private String getImageMD5(int index) throws XpathException {
        if ("MD5".equals(getImageFileHashSumAlgorithmType(index)))
            return getImageFileHashSum(index);
        return null;
    }

    private String getXml(String isrc) throws XpathException, TransformerException {
            NodeList nodeList = xpathEngine.getMatchingNodes("/ern:NewReleaseMessage/ReleaseList/Release[ReleaseId/ISRC='"+isrc+"']", document);
            TransformerFactory transFactory = new TransformerFactoryImpl();
            Transformer transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(nodeList.item(0)),
                    new StreamResult(buffer));
            return buffer.toString();
    }

    private String getImageFileName(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/FileName)[" + index + "]");
    }

    private String getImageFileHashSumAlgorithmType(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSumAlgorithmType)[" + index + "]");
    }

    private String getImageFileHashSum(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File/HashSum/HashSum)[" + index + "]");
    }

    private int getImageCount() throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File)"));
    }

    private String getTitleText(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/Title/TitleText");
    }

    private String getSubTitle(String isrc) throws XpathException {
        String subTitle = null;
        NodeList nodeList = xpathEngine.getMatchingNodes("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='" + isrc + "']/SoundRecordingDetailsByTerritory/SubTitle", document);
        for (int i = 0; i < nodeList.getLength(); i++) {
            subTitle += nodeList.item(i).getNodeValue();
            if (i < nodeList.getLength() - 1) subTitle += "/";
        }

        if (isEmpty(subTitle)) return null;
        return subTitle;
    }

    private String getArtist(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/DisplayArtist/PartyName/FullName");
    }

    private String getGenre(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/Genre/GenreText");
    }

    private String getCopyright(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/PLine/PLineText");
    }

    private String getLabel(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/LabelName");
    }

    private String getIsrc(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingId/ISRC)[" + index +"]");
    }

    private String getYear(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/PLine/Year");
    }

    private boolean getExplicit(String isrc) throws XpathException {
        String parentalWarningType = evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/ParentalWarningType");
        return "Explicit".equals(parentalWarningType);
    }

    private int getTerritoryPerTrackCount(String isrc) throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TerritoryCode)"));
    }

    private String getTerritoryPerTrack(String isrc, int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TerritoryCode)["+index +"]");
    }

    private String getDistributor() throws XpathException {
        return evaluate("/ern:NewReleaseMessage/MessageHeader/MessageSender/PartyName/FullName");
    }

    private String getAlbum() throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Album']/ReferenceTitle/TitleText");
    }

    private String getReleaseReference(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ReleaseList/Release[ReleaseId/ISRC='"+isrc+"']/ReleaseReference");
    }

    private String getDealReference(String dealReleaseReference) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealReference");
    }

    private String getStartDate(String dealReleaseReference) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/ValidityPeriod/StartDate");
    }

    private Float getPrice(String dealReleaseReference) throws XpathException {
        String price = evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit");
        if (isNotBlank(price)) return Float.parseFloat(price);
        return null;
    }

    private String getCurrency(String dealReleaseReference) throws XpathException {
        String currencyCode= evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/CurrencyCode");
        if(isNotBlank(currencyCode)) return currencyCode;
        return null;
    }

    private boolean getTakeDown(String dealReleaseReference) throws XpathException {
        String takeDown = evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/TakeDown");
        if(isNotNull(takeDown)) return Boolean.parseBoolean(takeDown);
        return false;
    }

    private int getFilesCount(String isrc) throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File)"));
    }

    private String getFileName(String isrc, int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/FileName)["+index+"]");
    }

    private int getDuration(String isrc) throws XpathException, ParseException {
        PeriodParser periodParser = ISOPeriodFormat.standard().getParser();
        ReadWritablePeriod readWritablePeriod = new MutablePeriod();
        periodParser.parseInto(readWritablePeriod, evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/Duration"), 0, null);
        return checkedCast(readWritablePeriod.toPeriod().toStandardDuration().getStandardSeconds());
    }

    private String getProprietaryId(String isrc) throws XpathException {
        return evaluate("/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingId[ISRC='"+isrc+"']/ProprietaryId");
    }

    private String getMD5(String isrc, int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File/HashSum/HashSum)["+index+"]");
    }

    private String getPriceType(String dealReleaseReference) throws XpathException {
        String priceType = evaluate("/ern:NewReleaseMessage/DealList/ReleaseDeal[DealReleaseReference='"+dealReleaseReference+"']/Deal/DealTerms/PriceInformation/WholesalePricePerUnit/PriceType");
        if(isNotBlank(priceType)) return priceType;
        return  null;
    }

    private FileType getType(String isrc, int index) throws XpathException {
        String isPreview = evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/IsPreview)["+index+"]");

        FileType fileType;
        if (isEmpty(isPreview) || isPreview.equals("false")){
            String audioCodecType = evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType)["+index+"]");
            String userDefinedValue = evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording[SoundRecordingId/ISRC='"+isrc+"']/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/AudioCodecType/@UserDefinedValue)["+index+"]");
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

    private Type getActionType() throws XpathException {
        return "UpdateMessage".equals(evaluate("/ern:NewReleaseMessage/UpdateIndicator")) ? UPDATE : INSERT;
    }

    private int getTrackReleaseCount() throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Single'])"));
    }

    private NodeList getTrackReleaseIdNodeList() throws XpathException {
        return xpathEngine.getMatchingNodes("/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Single']/ReleaseId", document);
    }

    private DropTrack getResultDropTrack(String expectedIsrc, String productCode) {
        return resultDropTrackMap.get(expectedIsrc + productCode + parserFixture.getClass());
    }

    private String getAssetFile(String fileName) {
        return xmlFileParent + "/resources/" + fileName;
    }
}
