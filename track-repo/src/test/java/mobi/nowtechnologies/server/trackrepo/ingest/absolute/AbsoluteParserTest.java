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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.*;
import static mobi.nowtechnologies.server.trackrepo.ingest.DropTrack.Type.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AbsoluteParserTest extends ParserTest<AbsoluteParser> {

    private Map<String,DropTrack> resultDropTrackMap;
    private NodeList expectedTrackReleaseIdNodeList;

    public void createParser() throws FileNotFoundException {
        parserFixture = new AbsoluteParser("classpath:media/absolute/");
    }

    public void populateXmlPrefixMap() {
        xmlPrefixMap.put("ern", "http://ddex.net/xml/2010/ern-main/312");
    }

    @Test
    @Ignore
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

        for (int i = 0; i < expectedTrackReleaseIdNodeList.getLength(); i++) {
            validateResultDropTrack(i);
        }

        DropTrack dropTrack = resultDropTrackMap.get("ROROT1302001_AbsoluteParser");

        List<DropTerritory> territories = dropTrack.getTerritories();

        assertThat(territories.size(), is(2));
        DropTerritory territory = territories.get(0);
        assertThat(territory.country, is("GB"));   // IE
        assertThat(territory.currency, is("GBP")); // GBP default
//        assertThat(territory.dealReference, is("")); //DealTerms
        assertThat(territory.distributor, is("Absolute Marketing & Distribution Ltd."));
        assertThat(territory.label, is("3 Beat Productions"));
        assertThat(territory.price, is(0.0f));    //default
        assertThat(territory.priceCode, is("0.0"));
        assertThat(territory.publisher, is(""));
        assertThat(territory.reportingId, is("ROROT1302001"));
        assertThat(territory.startdate, is(DateUtils.newDate(28, 07, 2013)));  // <StartDate>2013-07-28</StartDate>
        assertThat(territory.takeDown, is(false));

        List<DropAssetFile> files = dropTrack.getFiles();
        DropAssetFile asset = files.get(0);
        assertThat(asset.duration, is(0)); //PT3M50S
        assertThat(asset.file, is(""));
        assertThat(asset.isrc, is(""));
        assertThat(asset.md5, is(""));
        assertThat(asset.type, is(AssetFile.FileType.MOBILE));
    }

    private void validateResultDropTrack(int i) throws XpathException {
        int xPathExpressionIndex = i + 1;
        String expectedIsrc = getIsrc(xPathExpressionIndex);

        DropTrack resultDropTrack = getResultDropTrack(expectedIsrc);

        assertNotNull(resultDropTrack);

        //assertThat(resultDropTrack.xml, is("3BEATCD019"));
        assertThat(resultDropTrack.type, is(INSERT));
        assertThat(resultDropTrack.productCode, is(""));
        assertThat(resultDropTrack.title, is(getTitleText(xPathExpressionIndex)));
        assertThat(resultDropTrack.subTitle, is(getSubTitle(xPathExpressionIndex)));
        assertThat(resultDropTrack.artist, is(getArtist(xPathExpressionIndex)));
        assertThat(resultDropTrack.genre, is(getGenre(xPathExpressionIndex)));
        assertThat(resultDropTrack.copyright, is(getCopyright(xPathExpressionIndex)));
        assertThat(resultDropTrack.label, is(getLabel(xPathExpressionIndex)));
        assertThat(resultDropTrack.isrc, is(expectedIsrc));
        assertThat(resultDropTrack.year, is(getYear(xPathExpressionIndex)));
        assertThat(resultDropTrack.physicalProductId, is(expectedIsrc));
        // assertThat(resultDropTrack.album, is("Party Never Ends"));
        assertThat(resultDropTrack.info, is(""));
        assertThat(resultDropTrack.licensed, is(true));
        assertThat(resultDropTrack.exists, is(true));    // ?
        assertThat(resultDropTrack.explicit, is(false));
        assertThat(resultDropTrack.productId, is(expectedIsrc));

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
