package mobi.nowtechnologies.server.trackrepo.ingest.absolute;

import mobi.nowtechnologies.server.shared.util.DateUtils;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class AbsoluteParserTest {

    @Test
    @Ignore
    public void verifyThatAbsoluteParserReadBasicFieldCorrectly() throws IOException {
        //given
        File file = new ClassPathResource("media/absolute/absolute.xml").getFile();

        //when
        Map<String, DropTrack> trackMap = new AbsoluteParser().parse(file);

        //then
        DropTrack track = trackMap.get("ROROT1302001_AbsoluteParser");

        assertThat(trackMap.size(), is(greaterThan(0)));
        //assertThat(track.xml, is("3BEATCD019"));
        assertThat(track.type, is(DropTrack.Type.INSERT));
        assertThat(track.productCode, is(""));
        assertThat(track.title, is("In Your Eyes"));
        assertThat(track.subTitle, is("NotExplicit"));
        assertThat(track.artist, is("Inna"));
        assertThat(track.genre, is("Rock/Pop"));
        assertThat(track.copyright, is("2012 3 Beat Productions Ltd under exclusive licence from ROTON"));
        assertThat(track.label, is("3 Beat Productions"));
        assertThat(track.isrc, is("ROROT1302001"));
        assertThat(track.year, is("2012"));
        assertThat(track.physicalProductId, is("ROROT1302001"));
       // assertThat(track.album, is("Party Never Ends"));
        assertThat(track.info, is(""));
        assertThat(track.licensed, is(true));
        assertThat(track.exists, is(true));    // ?
        assertThat(track.explicit, is(false));
        assertThat(track.productId, is("ROROT1302001"));

        List<DropTerritory> territories = track.getTerritories();

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

        List<DropAssetFile> files = track.getFiles();
        DropAssetFile asset = files.get(0);
        assertThat(asset.duration, is(0)); //PT3M50S
        assertThat(asset.file, is(""));
        assertThat(asset.isrc, is(""));
        assertThat(asset.md5, is(""));
        assertThat(asset.type, is(AssetFile.FileType.MOBILE));
    }

}
