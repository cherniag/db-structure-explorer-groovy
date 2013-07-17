package mobi.nowtechnologies.server.trackrepo.utils;

import junit.framework.Assert;
import mobi.nowtechnologies.server.trackrepo.ingest.DropsData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/16/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class NullAwareBeanUtilsBeanTest {

    private NullAwareBeanUtilsBean fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new NullAwareBeanUtilsBean();
    }

    @Test
    public void testCopyProperty_Success() throws Exception {
        IngestWizardData data = new IngestWizardData();
        data.setSuid("Suidffffaaaaa");
        data.setDropdata(new DropsData());
        data.getDropdata().setDrops(new ArrayList<DropsData.Drop>());

        IngestWizardData result = new IngestWizardData();
        result.setSuid("Suidffffbbbbb");
        result.setDropdata(new DropsData());
        result.getDropdata().setDrops(new ArrayList<DropsData.Drop>());

        fixture.copyProperties(result, data);

        Assert.assertEquals(data.getSuid(), result.getSuid());
    }
}
