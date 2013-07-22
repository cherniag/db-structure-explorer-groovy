package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/10/13
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class UniversalParserTest {
    private UniversalParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new UniversalParser("classpath:media/universal_cdu/");
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsNotTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsNotTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("ROCRP1002948").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002948").explicit);
    }
}
