package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static java.lang.Integer.parseInt;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.*;

public class SonyDDEXParserTest {

    private File xmlFile;
    private SonyDDEXParser sonyDdexParserFixture;
    private Map<String, DropTrack> resultDropTrackMap;
    private XpathEngine xpathEngine;
    private Document document;
    private String expectedAlbum;
    private Type expectedDropTrackType;
    private String expectedProductCode;
    private NodeList expectedReleaseIdNodeList;
    private Map<String, List<DropAssetFile>> dropAssetsByResourceReferenceMap;

    @Before
    public void setUp() throws FileNotFoundException {
        sonyDDEXParser();

        HashMap m = new HashMap();
        m.put("ernm", "http://ddex.net/xml/ern/341");

        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(new SimpleNamespaceContext(m));

    }

    @Test
    public void shouldParseFormat3_4_1() throws Exception {
        //given
        xmlFile = new ClassPathResource("media/sony_cdu/ern.v3.4.1/A10301A00002442286.xml").getFile();

        //when
        resultDropTrackMap = sonyDdexParserFixture.loadXml(xmlFile.getAbsolutePath());

        //then
        shouldParseFormat3_4_1Successfully();
    }

    private void shouldParseFormat3_4_1Successfully() throws IOException, SAXException, XpathException {
        document = getDocument();

        assertEquals(getTrackReleaseCount(), resultDropTrackMap.entrySet().size());

        expectedReleaseIdNodeList = getReleaseIdNodeList();
        NodeList fileNodeList = getFileNodeList();

        dropAssetsByResourceReferenceMap = getDropAssetsByResourceReferenceMap(fileNodeList);
        expectedAlbum = getAlbum();
        expectedDropTrackType = getDropTrackType();
        expectedProductCode = getProductCode();


        for (int i = 0; i < expectedReleaseIdNodeList.getLength(); i++) {
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
        Node releaseIdNode = expectedReleaseIdNodeList.item(i);
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

        assertNotNull(resultDropTrack.files);

        List<DropAssetFile> expectedDropAssetFiles = dropAssetsByResourceReferenceMap.get(getReleaseResourceReference(releaseResourceReferenceListElement));
        assertEquals(expectedDropAssetFiles.size(), resultDropTrack.files.size());
        //assertEquals(expectedDropAssetFiles, resultDropTrack.files);

        assertNotNull(resultDropTrack.territories);
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
        return resultDropTrackMap.get(expectedIsrc + expectedProprietaryId + sonyDdexParserFixture.getClass());
    }

    private String getReleaseResourceReference(Element releaseResourceReferenceListElement) {
        return getElementValue(releaseResourceReferenceListElement, "ReleaseResourceReference");
    }

    private Document getDocument() throws IOException, SAXException {
        return XMLUnit.buildControlDocument(new InputSource(new FileInputStream(xmlFile)));
    }

    private Element getChildNodesElement(Node node) {
        return (Element) node.getChildNodes();
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

    private String getProprietaryId(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "ProprietaryId");
    }

    private String getGRid(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "GRid");
    }

    private String getIsrc(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "ISRC");
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

    private NodeList getReleaseIdNodeList() throws XpathException {
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

    private String evaluate(String expression) throws XpathException {
        return xpathEngine.evaluate(expression, document);
    }

    private String getElementValue(Element element, String tagName) {
        Node item = element.getElementsByTagName(tagName).item(0);
        if (item != null)
            return item.getTextContent();
        return null;
    }

    private void sonyDDEXParser() throws FileNotFoundException {
        sonyDdexParserFixture = new SonyDDEXParser("classpath:media/sony_cdu/ern.v3.4.1/");
    }
}
