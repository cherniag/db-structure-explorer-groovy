package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.INSERT;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.UPDATE;

public class SonyDDEXParserTest extends ParserTest{

    private Map<String, DropTrack> resultDropTrackMap;
    private String expectedAlbum;
    private Type expectedDropTrackType;
    private String expectedProductCode;
    private NodeList expectedTrackReleaseIdNodeList;
    private Map<String, Map<Integer, DropAssetFile>> expectedDropAssetsMap;
    private Map<String, Map<Integer, DropTerritory>> expectedTerritoriesMap;
    private Map<String, List<DropAssetFile>> dropAssetsByResourceReferenceMap;
    public static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    public void createParser() throws FileNotFoundException {
        parserFixture = new SonyDDEXParser("classpath:media/sony_cdu/ern.v3.4.1/");
    }

    public void populateXmlPrefixMap() {
        xmlPrefixMap.put("ernm", "http://ddex.net/xml/ern/341");
    }

    @Test
    public void shouldParseFormat3_4_1() throws Exception {
        //given
        xmlFile = new ClassPathResource("media/sony_cdu/ern.v3.4.1/A10301A00002442286.xml").getFile();

        //when
        resultDropTrackMap = parserFixture.loadXml(xmlFile);

        //then
        shouldParseFormat3_4_1Successfully();
    }

    private void shouldParseFormat3_4_1Successfully() throws IOException, SAXException, XpathException, ParseException {
        document = getDocument();

        assertNotNull(resultDropTrackMap);
        assertEquals(getTrackReleaseCount(), resultDropTrackMap.entrySet().size());

        expectedTrackReleaseIdNodeList = getTrackReleaseIdNodeList();
        NodeList fileNodeList = getFileNodeList();

        dropAssetsByResourceReferenceMap = getDropAssetsByResourceReferenceMap(fileNodeList);
        expectedDropAssetsMap = getExpectedDropAssetsMap();
        expectedTerritoriesMap = getExpectedTerritoryMap();
        expectedAlbum = getAlbum();
        expectedDropTrackType = getDropTrackType();
        expectedProductCode = getProductCode();

        for (int i = 0; i < expectedTrackReleaseIdNodeList.getLength(); i++) {
            validateResultDropTrack(i);
        }
    }

    private Map<String, List<DropAssetFile>> getDropAssetsByResourceReferenceMap(NodeList fileNodeList) throws XpathException {

        DropAssetFile imageDropAssetFile = getImageDropAssetFile();

        dropAssetsByResourceReferenceMap = new HashMap<String, List<DropAssetFile>>();
        String xmlFileParentPath = xmlFile.getParent();
        for (int i = 0; i < fileNodeList.getLength(); i++) {
            Node fileNode = fileNodeList.item(i);
            Node soundRecordingNode = getsSoundRecordingNode(fileNode);
            Element soundRecordingChildNodesElement = getChildNodesElement(soundRecordingNode);
            String resourceReference = getResourceReference(soundRecordingChildNodesElement);

            Element fileChildNodesElement = getChildNodesElement(fileNode);
            DropAssetFile dropAssetFile = new DropAssetFile();
            dropAssetFile.file = xmlFileParentPath + getFileName(fileChildNodesElement);

            putInToDropAssetsByResourceReferenceMap(resourceReference, dropAssetFile, imageDropAssetFile);
        }
        return dropAssetsByResourceReferenceMap;
    }

    private Node getImageFileNode(NodeList imageFileNodeList) {
        return imageFileNodeList.item(0);
    }

    private NodeList getImageFileNodeList() throws XpathException {
        return xpathEngine.getMatchingNodes("/ernm:NewReleaseMessage/ResourceList/Image/ImageDetailsByTerritory/TechnicalImageDetails/File", document);
    }

    private DropAssetFile getImageDropAssetFile() throws XpathException {
        DropAssetFile dropAssetFile = null;
        Node imageFileNode = getImageFileNode(getImageFileNodeList());
        Element imageFileChildNodesElement = getChildNodesElement(imageFileNode);
        if (imageFileChildNodesElement != null) {
            dropAssetFile = new DropAssetFile();
            dropAssetFile.file = "/resources/" + getFileName(imageFileChildNodesElement);
        }
        return dropAssetFile;
    }

    private void putInToDropAssetsByResourceReferenceMap(String resourceReference, DropAssetFile dropAssetFile, DropAssetFile imageDropAssetFile) {
        List<DropAssetFile> dropAssetFiles = dropAssetsByResourceReferenceMap.get(resourceReference);
        if (dropAssetFiles == null) {
            dropAssetFiles = new ArrayList<DropAssetFile>();
            dropAssetsByResourceReferenceMap.put(resourceReference, dropAssetFiles);
            dropAssetFiles.add(imageDropAssetFile);
        }
        dropAssetFiles.add(dropAssetFile);
    }

    private void validateResultDropTrack(int i) {
        Node releaseIdNode = expectedTrackReleaseIdNodeList.item(i);
        Element releaseIdNChildElement = getChildNodesElement(releaseIdNode);
        Element releaseElement = getReleaseElement(releaseIdNode);

        String expectedIsrc = getIsrc(releaseIdNChildElement);
        String expectedProprietaryId = getProprietaryId(releaseIdNChildElement);
        String expectedGRid = getGRid(releaseIdNChildElement);

        DropTrack resultDropTrack = getResultDropTrack(expectedIsrc, expectedProprietaryId);
        assertNotNull(resultDropTrack);

        Element referenceTitleElement = getReferenceTitleElement(releaseElement);
        Element releaseDetailsByTerritoryElement = getReleaseDetailsByTerritoryElement(releaseElement);
        Element genreElement = getGenreElement(releaseDetailsByTerritoryElement);
        Element pLineElement = getPLineElement(releaseElement);

        assertEquals(expectedDropTrackType, resultDropTrack.type);
        assertEquals(expectedProductCode, resultDropTrack.productCode);
        assertEquals(getTitleText(referenceTitleElement), resultDropTrack.title);
        assertEquals(getSubTitle(referenceTitleElement), resultDropTrack.subTitle);
        assertEquals(getDisplayArtistName(releaseDetailsByTerritoryElement), resultDropTrack.artist);
        assertEquals(getGenre(genreElement), resultDropTrack.genre);
        assertEquals(getCopyright(pLineElement), resultDropTrack.copyright);
        assertEquals(getLabel(releaseDetailsByTerritoryElement), resultDropTrack.label);
        assertEquals(expectedIsrc, resultDropTrack.isrc);
        assertEquals(getYear(pLineElement), resultDropTrack.year);
        assertEquals(expectedGRid, resultDropTrack.physicalProductId);
        assertEquals(expectedAlbum, resultDropTrack.album);
        assertEquals(null, resultDropTrack.info);
        assertEquals(true, resultDropTrack.licensed);
        assertEquals(false, resultDropTrack.exists);
        assertEquals(false, resultDropTrack.explicit);
        assertEquals(expectedGRid, resultDropTrack.productId);

        Element releaseResourceReferenceListElement = getReleaseResourceReferenceListElement(releaseElement);
        String resourceRef = getReleaseResourceReference(releaseResourceReferenceListElement);

        assertNotNull(resultDropTrack.files);

        List<DropAssetFile> expectedDropAssetFiles = dropAssetsByResourceReferenceMap.get(resourceRef);
        assertEquals(expectedDropAssetFiles.size(), resultDropTrack.files.size());
        int j = 0;
        for (DropAssetFile file : resultDropTrack.files) {
            validateAssetFile(file, j, resourceRef);
            j++;
        }

        assertNotNull(resultDropTrack.territories);
        assertEquals(expectedDropAssetFiles.size(), resultDropTrack.files.size());
        j = 0;
        for (DropTerritory territory: resultDropTrack.territories) {
            validateTerritory(territory, j, resourceRef);
            j++;
        }
    }

    private void validateAssetFile(DropAssetFile file, Integer j, String resourceRef){
          Map<Integer, DropAssetFile> expectedMap = expectedDropAssetsMap.get(resourceRef);

          if(expectedMap != null){
              DropAssetFile expected = expectedMap.get(j);

              assertEquals(expected.file, file.file);
              assertEquals(expected.type, file.type);
              assertEquals(expected.isrc, file.isrc);
              assertEquals(expected.md5, file.md5);
          }
    }

    private void validateTerritory(DropTerritory territory, Integer j, String resourceRef){
        Map<Integer, DropTerritory> expectedMap = expectedTerritoriesMap.get(resourceRef);

        if(expectedMap != null){
            DropTerritory expected = expectedMap.get(j);

            assertEquals(expected.country, territory.country);
            assertEquals(expected.label, territory.label);
            assertEquals(expected.currency, territory.currency);
            assertEquals(expected.price, territory.price);
            assertEquals(expected.startdate, territory.startdate);
            assertEquals(expected.reportingId, territory.reportingId);
            assertEquals(expected.distributor, territory.distributor);
            assertEquals(expected.takeDown, territory.takeDown);
            assertEquals(expected.priceCode, territory.priceCode);
            assertEquals(expected.dealReference, territory.dealReference);
            assertEquals(expected.publisher, territory.publisher);
        }
    }

    private Map<String,Map<Integer,DropAssetFile>> getExpectedDropAssetsMap() throws IOException {
        Map<String,Map<Integer,DropAssetFile>> map = new HashMap<String, Map<Integer, DropAssetFile>>();

        Map<Integer,DropAssetFile> assetMap = new HashMap<Integer, DropAssetFile>();
        map.put("A1", assetMap);

        String dirVersionAbsolutePath = new ClassPathResource("media/sony_cdu/ern.v3.4.1/").getFile().getAbsolutePath();

        assetMap.put(0, createDropAssetFile(AssetFile.FileType.PREVIEW, dirVersionAbsolutePath + "/resources/A10301A00002442286_T-10413_SoundRecording_001-001.aac", "FIBMB9100008", "e2767ec5f8a5ac0116c88f8b8e6dc533"));
        assetMap.put(1, createDropAssetFile(AssetFile.FileType.DOWNLOAD, dirVersionAbsolutePath + "/resources/A10301A00002442286_T-11006_SoundRecording_001-001.mp3", "FIBMB9100008", "3a2e576a50ea0d80444108a11cd6b134"));
        assetMap.put(2, createDropAssetFile(AssetFile.FileType.MOBILE, dirVersionAbsolutePath + "/resources/A10301A00002442286_T-10253_SoundRecording_001-001.m4a", "FIBMB9100008", "19b6c9dbdd030d608d9710c215d1c648"));
        assetMap.put(3, createDropAssetFile(AssetFile.FileType.IMAGE, dirVersionAbsolutePath + "/resources/A10301A00002442286_T-10026_Image.jpg", null, "68eac0c555283fd215cff48321815ad0"));

        return map;
    }

    private Map<String,Map<Integer,DropTerritory>> getExpectedTerritoryMap() throws ParseException {
        Map<String,Map<Integer, DropTerritory>> map = new HashMap<String, Map<Integer, DropTerritory>>();

        Map<Integer,DropTerritory> assetMap = new HashMap<Integer, DropTerritory>();
        map.put("A1", assetMap);
        assetMap.put(0, createDropTerritory());

        return map;
    }

    private DropAssetFile createDropAssetFile(AssetFile.FileType type, String file, String isrc, String md5){
        DropAssetFile dropAssetFile = new DropAssetFile();

        dropAssetFile.type = type;
        dropAssetFile.file = file;
        dropAssetFile.isrc = isrc;
        dropAssetFile.md5 = md5;

        return dropAssetFile;
    }

    private DropTerritory createDropTerritory() throws ParseException {
        DropTerritory dropTerritory= new DropTerritory();

        dropTerritory.country = "GB";
        dropTerritory.label = "RCA Camden";
        dropTerritory.currency = "GBP";
        dropTerritory.price = 0.83f;

        dropTerritory.startdate = DATE_PARSER.parse("2000-01-14");
        dropTerritory.reportingId = "FIBMB9100008";
        dropTerritory.distributor = "Sony Music Entertainment";
        dropTerritory.takeDown = false;
        dropTerritory.priceCode = null;
        dropTerritory.dealReference = null;
        dropTerritory.publisher = null;

        return dropTerritory;
    }

    private Node getsSoundRecordingNode(Node fileNode) {
        return fileNode.getParentNode().getParentNode().getParentNode();
    }

    private String getResourceReference(Element soundRecordingChildNodesElement) {
        return getElementValue(soundRecordingChildNodesElement, "ResourceReference");
    }

    private String getFileName(Element fileChildNodesElement) {
        return getElementValue(fileChildNodesElement, "FileName");
    }

    private DropTrack getResultDropTrack(String expectedIsrc, String expectedProprietaryId) {
        return resultDropTrackMap.get(expectedIsrc + expectedProprietaryId + parserFixture.getClass());
    }

    private String getReleaseResourceReference(Element releaseResourceReferenceListElement) {
        return getElementValue(releaseResourceReferenceListElement, "ReleaseResourceReference");
    }

    private Element getReleaseElement(Node releaseIdNode) {
        return getChildNodesElement(releaseIdNode.getParentNode());
    }

    private Element getReleaseResourceReferenceListElement(Element releaseElement) {
        return (Element) releaseElement.getElementsByTagName("ReleaseResourceReferenceList").item(0);
    }

    private Element getPLineElement(Element releaseElement) {
        return (Element) releaseElement.getElementsByTagName("PLine").item(0);
    }

    private Element getGenreElement(Element releaseDetailsByTerritoryElement) {
        return (Element) releaseDetailsByTerritoryElement.getElementsByTagName("Genre").item(0);
    }

    private Element getReleaseDetailsByTerritoryElement(Element releaseElement) {
        return (Element) releaseElement.getElementsByTagName("ReleaseDetailsByTerritory").item(0);
    }

    private Element getReferenceTitleElement(Element releaseElement) {
        return (Element) releaseElement.getElementsByTagName("ReferenceTitle").item(0);
    }

    private String getGRid(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "GRid");
    }

    private String getYear(Element pLineElement) {
        return getElementValue(pLineElement, "Year");
    }

    private String getLabel(Element releaseDetailsByTerritoryElement) {
        return getElementValue(releaseDetailsByTerritoryElement, "LabelName");
    }

    private String getCopyright(Element pLineElement) {
        return getElementValue(pLineElement, "PLineText");
    }

    private String getGenre(Element genreElement) {
        return getElementValue(genreElement, "GenreText");
    }

    private String getDisplayArtistName(Element releaseDetailsByTerritoryElement) {
        return getElementValue(releaseDetailsByTerritoryElement, "DisplayArtistName");
    }

    private String getSubTitle(Element referenceTitleElement) {
        return getElementValue(referenceTitleElement, "SubTitle");
    }

    private String getTitleText(Element referenceTitleElement) {
        return getElementValue(referenceTitleElement, "TitleText");
    }

    private String getProductCode() throws XpathException {
        return evaluate("/ernm:NewReleaseMessage/ReleaseList/Release[ReleaseType='Album']/ReleaseId/ICPN");
    }

    private NodeList getTrackReleaseIdNodeList() throws XpathException {
        return xpathEngine.getMatchingNodes("/ernm:NewReleaseMessage/ReleaseList/Release[ReleaseType='TrackRelease']/ReleaseId", document);
    }

    private NodeList getFileNodeList() throws XpathException {
        return xpathEngine.getMatchingNodes("/ernm:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/TechnicalSoundRecordingDetails/File", document);
    }

    private int getTrackReleaseCount() throws XpathException {
        return parseInt(evaluate("count(/ernm:NewReleaseMessage/ReleaseList/Release[ReleaseType='TrackRelease'])"));
    }

    private Type getDropTrackType() throws XpathException {
        String updateIndicator = getUpdateIndicator();
        if ("UpdateMessage".equals(updateIndicator)) {
            return UPDATE;
        } else {
            return INSERT;
        }
    }

    private String getUpdateIndicator() throws XpathException {
        return evaluate("/ernm:NewReleaseMessage/UpdateIndicator");
    }

    private String getAlbum() throws XpathException {
        return evaluate("/ernm:NewReleaseMessage/ReleaseList/Release[ReleaseType='Album']/ReferenceTitle/TitleText");
    }

}
