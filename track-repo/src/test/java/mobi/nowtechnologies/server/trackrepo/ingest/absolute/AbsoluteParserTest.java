package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.ParserTest;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
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

        //assertThat(dropTrack.xml, is("3BEATCD019"));
        assertThat(dropTrack.type, is(DropTrack.Type.INSERT));
        assertThat(dropTrack.productCode, is(""));
        assertThat(dropTrack.title, is("In Your Eyes"));
        assertThat(dropTrack.subTitle, is("NotExplicit"));
        assertThat(dropTrack.artist, is("Inna"));
        assertThat(dropTrack.genre, is("Rock/Pop"));
        assertThat(dropTrack.copyright, is("2012 3 Beat Productions Ltd under exclusive licence from ROTON"));
        assertThat(dropTrack.label, is("3 Beat Productions"));
        assertThat(dropTrack.isrc, is("ROROT1302001"));
        assertThat(dropTrack.year, is("2012"));
        assertThat(dropTrack.physicalProductId, is("ROROT1302001"));
        // assertThat(dropTrack.album, is("Party Never Ends"));
        assertThat(dropTrack.info, is(""));
        assertThat(dropTrack.licensed, is(true));
        assertThat(dropTrack.exists, is(true));    // ?
        assertThat(dropTrack.explicit, is(false));
        assertThat(dropTrack.productId, is("ROROT1302001"));

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
        //assertThat(territory.startdate, is(DateUtils.newDate(28, 07, 2013)));  // <StartDate>2013-07-28</StartDate>
        assertThat(territory.takeDown, is(false));
       assertThat(dropTrack.getFiles().isEmpty(), is(true));
    }

    private void validateResultDropTrack(int i) {
        Node releaseIdNode = expectedTrackReleaseIdNodeList.item(i);
        Element releaseIdNChildElement = getChildNodesElement(releaseIdNode);

        String expectedIsrc = getIsrc(releaseIdNChildElement);
        String expectedProprietaryId = getProprietaryId(releaseIdNChildElement);

        //DropTrack resultDropTrack = getResultDropTrack(expectedIsrc, expectedProprietaryId);
        //assertNotNull(resultDropTrack);
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
