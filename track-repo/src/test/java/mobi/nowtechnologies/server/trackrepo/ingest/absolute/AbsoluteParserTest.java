package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.shared.util.DateUtils;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.*;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AbsoluteParserTest extends ParserTest {

    private Map<String,DropTrack> resultDropTrackMap;

    @Before
    public void setUp() throws FileNotFoundException {
        absoluteParser();

        HashMap m = new HashMap();
        m.put("ern", "http://ddex.net/xml/2010/ern-main/312");

        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(new SimpleNamespaceContext(m));
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

    private void absoluteParser() {
        parserFixture = new AbsoluteParser();
    }

    private int getTrackReleaseCount() throws XpathException {
        return parseInt(evaluate("count(/ern:NewReleaseMessage/ReleaseList/Release[ReleaseType='Single'])"));
    }

    private DropTrack getResultDropTrack(String expectedIsrc, String expectedProprietaryId) {
        return resultDropTrackMap.get(expectedIsrc + expectedProprietaryId + parserFixture.getClass());
    }
}
