package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.shared.util.DateUtils;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.ParserTest;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.*;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AbsoluteParserTest extends ParserTest<AbsoluteParser> {

    private Map<String,DropTrack> resultDropTrackMap;
    private NodeList expectedTrackReleaseIdNodeList;
    private DropTrack resultDropTrack;
    private int xPathExpressionResultDropTrackIndex;
    private String expectedIsrc;
    private String expectedDistributor;
    private String expectedLabel;
    private int expectedResultDropTrackIndex;

    public void createParser() throws FileNotFoundException {
        parserFixture = new AbsoluteParser("classpath:media/absolute/");
    }

    public void populateXmlPrefixMap() {
        xmlPrefixMap.put("ern", "http://ddex.net/xml/2010/ern-main/312");
    }

    @Test
    public void verifyThatAbsoluteParserReadBasicFieldCorrectly() throws Exception {
        //given
        xmlFile = new ClassPathResource("media/absolute/absolute.xml").getFile();

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

        for (expectedResultDropTrackIndex = 0; expectedResultDropTrackIndex < expectedTrackReleaseIdNodeList.getLength(); expectedResultDropTrackIndex++) {
            validateResultDropTrack();
        }
    }

    private void validateResultDropTrack() throws XpathException {
        xPathExpressionResultDropTrackIndex = expectedResultDropTrackIndex + 1;
        expectedIsrc = getIsrc(xPathExpressionResultDropTrackIndex);
        expectedLabel = getLabel(xPathExpressionResultDropTrackIndex);

        resultDropTrack = getResultDropTrack(expectedIsrc);

        assertNotNull(resultDropTrack);
        //assertThat(resultDropTrack.xml, is("3BEATCD019"));
        assertThat(resultDropTrack.type, is(INSERT));
        assertThat(resultDropTrack.productCode, is(""));
        assertThat(resultDropTrack.title, is(getTitleText(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.subTitle, is(getSubTitle(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.artist, is(getArtist(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.genre, is(getGenre(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.copyright, is(getCopyright(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.label, is(expectedLabel));
        assertThat(resultDropTrack.isrc, is(expectedIsrc));
        assertThat(resultDropTrack.year, is(getYear(xPathExpressionResultDropTrackIndex)));
        assertThat(resultDropTrack.physicalProductId, is(expectedIsrc));
        // assertThat(resultDropTrack.album, is("Party Never Ends"));
        assertThat(resultDropTrack.info, is(""));
        assertThat(resultDropTrack.licensed, is(true));
        assertThat(resultDropTrack.exists, is(true));    // ?
        assertThat(resultDropTrack.explicit, is(false));
        assertThat(resultDropTrack.productId, is(expectedIsrc));

        validateResultTerritories();
        //validateResultFiles();
    }

    private void validateResultFiles() {
        List<DropAssetFile> files = resultDropTrack.getFiles();

        DropAssetFile asset = files.get(0);
        assertThat(asset.duration, is(0)); //PT3M50S
        assertThat(asset.file, is(""));
        assertThat(asset.isrc, is(""));
        assertThat(asset.md5, is(""));
        assertThat(asset.type, is(MOBILE));
    }

    private void validateResultTerritories() throws XpathException {
        List<DropTerritory> territories = resultDropTrack.getTerritories();

        assertNotNull(territories);
        assertThat(territories.size(), is(getTerritoryPerTrackCount(xPathExpressionResultDropTrackIndex)));

        for (int i = 0; i < territories.size(); i++) {
            DropTerritory territory = territories.get(i);

            int territoryPerTrackIndex = i + 1;
            assertThat(territory.country, is(getTerritoryPerTrack(xPathExpressionResultDropTrackIndex, territoryPerTrackIndex)));
            assertThat(territory.currency, is("GBP"));
//        assertThat(territory.dealReference, is("")); //DealTerms
            assertThat(territory.distributor, is(expectedDistributor));
            assertThat(territory.label, is(expectedLabel));
            assertThat(territory.price, is(0.0f));
            assertThat(territory.priceCode, is("0.0"));
            assertThat(territory.publisher, is(""));
            assertThat(territory.reportingId, is(expectedIsrc));
//            assertThat(territory.startdate, is(DateUtils.newDate(28, 07, 2013)));
            assertThat(territory.takeDown, is(false));
        }
    }

    private String getTitleText(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/Title/TitleText)[" + index +"]");
    }

    private String getSubTitle(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/ParentalWarningType)[" + index +"]");
    }

    private String getArtist(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/DisplayArtist/PartyName/FullName)[" + index +"]");
    }

    private String getGenre(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/Genre/GenreText)[" + index +"]");
    }

    private String getCopyright(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/PLine/PLineText)[" + index +"]");
    }

    private String getLabel(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/LabelName)[" + index +"]");
    }

    private String getIsrc(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingId/ISRC)[" + index +"]");
    }

    private String getYear(int index) throws XpathException {
        return evaluate("(/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory/PLine/Year)[" + index +"]");
    }

    private int getTerritoryPerTrackCount(int index) throws XpathException {
        return parseInt(evaluate("count((/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory)[" + index +"]/TerritoryCode)"));
    }

    private String getTerritoryPerTrack(int soundRecordingDetailsByTerritoryIndex, int index) throws XpathException {
        return evaluate("((/ern:NewReleaseMessage/ResourceList/SoundRecording/SoundRecordingDetailsByTerritory)[" + soundRecordingDetailsByTerritoryIndex +"]/TerritoryCode)["+index +"]");
    }

    private String getDistributor() throws XpathException {
        return evaluate("/ern:NewReleaseMessage/MessageHeader/MessageSender/PartyName/FullName");
    }

    private int getTrackReleaseCount() throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Single'])"));
    }

    private NodeList getTrackReleaseIdNodeList() throws XpathException {
        return xpathEngine.getMatchingNodes("/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Single']/ReleaseId", document);
    }

    private DropTrack getResultDropTrack(String expectedIsrc) {
        return resultDropTrackMap.get(expectedIsrc + "_" + parserFixture.getClass().getSimpleName());
    }
}
