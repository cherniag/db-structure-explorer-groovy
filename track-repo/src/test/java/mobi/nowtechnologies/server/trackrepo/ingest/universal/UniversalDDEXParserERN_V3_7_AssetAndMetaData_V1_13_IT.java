package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import mobi.nowtechnologies.server.trackrepo.ingest.IParserFactory;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.UNIVERSAL;
import static mobi.nowtechnologies.server.trackrepo.ingest.Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/META-INF/application-test.xml"})
public class UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13_IT {

    Logger LOGGER = LoggerFactory.getLogger(UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13_IT.class);

    @Resource(name = "trackRepo.ParserFactory")
    IParserFactory parserFactory;

    UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13 universalDDEXParserERN_v3_7_assetAndMetaData_v1_13;
    UniversalParser universalParser;

    @Before
    public void setUp() throws FileNotFoundException {
        universalDDEXParserERN_v3_7_assetAndMetaData_v1_13 = (UniversalDDEXParserERN_V3_7_AssetAndMetaData_V1_13) parserFactory.getParser(UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);
        universalParser = (UniversalParser) parserFactory.getParser(UNIVERSAL);
    }

    @Test
    public void shouldReturnTheSameAsStandardUniversalParser() throws Exception {
        //given
        String code = "00008811295929";
        String drop = "3000022388379";
        File xmlFile = new ClassPathResource("media\\universal_cdu\\ddex\\" + code + "_" + drop + "\\UMG_DDEX_Metadata_ChartsNow.mobi_00008811295929_2015-02-06_02-20-09.xml").getFile();

        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<>();

        //when
        Map<String, DropTrack> actualIngestResultMap = universalDDEXParserERN_v3_7_assetAndMetaData_v1_13.loadXml(xmlFile);
        Map<String, DropTrack> standardIngestResultMap = universalParser.loadXml(drop, code, fulfillmentFiles);

        //then
        assertEq(actualIngestResultMap, standardIngestResultMap);
    }

    private void assertEq(Map<String, DropTrack> actualIngestResultMap, Map<String, DropTrack> standardIngestResultMap) {
        assertThat(actualIngestResultMap.size(), is(standardIngestResultMap.size()));

        for (Map.Entry<String, DropTrack> actualDropTrackEntry : actualIngestResultMap.entrySet()) {
            DropTrack standardDropTrack = standardIngestResultMap.get(actualDropTrackEntry.getValue().isrc);
            DropTrack actualDropTrack = actualDropTrackEntry.getValue();

            LOGGER.info("Actual   {}", actualDropTrack);
            LOGGER.info("Standard {}", standardDropTrack);

            //assertThat(actualDropTrack.xml, is(standardDropTrack.xml));
            //assertThat(actualDropTrack.type, is(standardDropTrack.type));
            assertThat(actualDropTrack.productCode, is(standardDropTrack.productCode));
            assertThat(actualDropTrack.title, is(standardDropTrack.title));
            //assertThat(actualDropTrack.subTitle, is(standardDropTrack.subTitle));
            assertThat(actualDropTrack.artist, is(standardDropTrack.artist));
            //assertThat(actualDropTrack.genre, is(standardDropTrack.genre));
            //assertThat(actualDropTrack.copyright, is(standardDropTrack.copyright));
            assertThat(actualDropTrack.label, is(standardDropTrack.label));
            assertThat(actualDropTrack.isrc, is(standardDropTrack.isrc));
            //ssertThat(actualDropTrack.year, is(standardDropTrack.year));
            //ssertThat(actualDropTrack.physicalProductId, is(standardDropTrack.physicalProductId));
            assertThat(actualDropTrack.album, is(standardDropTrack.album));
            assertThat(actualDropTrack.info, is(standardDropTrack.info));
            assertThat(actualDropTrack.licensed, is(standardDropTrack.licensed));
            assertThat(actualDropTrack.exists, is(standardDropTrack.exists));
            assertThat(actualDropTrack.explicit, is(standardDropTrack.explicit));
            //assertThat(actualDropTrack.productId, is(standardDropTrack.productId));
            assertThat(actualDropTrack.reportingType, is(standardDropTrack.reportingType));

            assertEqDropAssetFiles(actualDropTrack.files, standardDropTrack.files);
            assertEqDropTerritories(actualDropTrack.territories, standardDropTrack.territories);
        }
    }

    public void assertEqDropAssetFiles(List<DropAssetFile> actualDropTrackFiles, List<DropAssetFile> standardDropTrackFiles) {

        for (int i = 0; i < standardDropTrackFiles.size(); i++) {
            DropAssetFile actualDropAssetFile = actualDropTrackFiles.get(i);
            DropAssetFile standardDropAssetFile = standardDropTrackFiles.get(i);

            assertThat(actualDropAssetFile.duration, is(standardDropAssetFile.duration));
            assertThat(actualDropAssetFile.type, is(standardDropAssetFile.type));
            assertThat(actualDropAssetFile.file, is(standardDropAssetFile.file));
            assertThat(actualDropAssetFile.isrc, is(standardDropAssetFile.isrc));
            assertThat(actualDropAssetFile.md5, is(standardDropAssetFile.md5));
        }
    }

    public void assertEqDropTerritories(List<DropTerritory> actualDropTerritories, List<DropTerritory> standardDropTerritories) {
        //assertThat(actualDropTerritories.size(), is(standardDropTerritories.size()));

        for (int i = 0; i < standardDropTerritories.size(); i++) {
            DropTerritory actualDropTerritory = actualDropTerritories.get(i);
            DropTerritory standardDropTerritory = standardDropTerritories.get(i);

            LOGGER.info("Actual   {}", actualDropTerritory);
            LOGGER.info("Standard {}", standardDropTerritory);

            assertThat(actualDropTerritory.country, is(standardDropTerritory.country));
            assertThat(actualDropTerritory.label, is(standardDropTerritory.label));
            assertThat(actualDropTerritory.currency, is(standardDropTerritory.currency));
            assertThat(actualDropTerritory.price, is(standardDropTerritory.price));
            //assertThat(actualDropTerritory.startdate, is(standardDropTerritory.startdate));
            assertThat(actualDropTerritory.reportingId, is(standardDropTerritory.reportingId));
            //assertThat(actualDropTerritory.distributor, is(standardDropTerritory.distributor));
            assertThat(actualDropTerritory.takeDown, is(standardDropTerritory.takeDown));
            assertThat(actualDropTerritory.priceCode, is(standardDropTerritory.priceCode));
            assertThat(actualDropTerritory.dealReference, is(standardDropTerritory.dealReference));
            assertThat(actualDropTerritory.publisher, is(standardDropTerritory.publisher));
        }
    }

}