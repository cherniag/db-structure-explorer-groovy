package mobi.nowtechnologies.server.trackrepo.utils;

import junit.framework.Assert;
import mobi.nowtechnologies.server.trackrepo.ingest.DropsData;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void testCopyProperty_BasicTypes_Success() throws Exception {
        IngestWizardData data = new IngestWizardData();
        data.setSuid("Suidffffaaaaa");

        IngestWizardData result = new IngestWizardData();
        result.setSuid("Suidffffbbbbb");

        fixture.copyProperties(result, data);

        Assert.assertEquals(data.getSuid(), result.getSuid());
    }

    @Test
    public void testCopyProperty_NullObject_Success() throws Exception {
        IngestWizardData data = new IngestWizardData();
        data.setSuid(null);
        data.setDropdata(null);

        IngestWizardData result = new IngestWizardData();
        result.setSuid("Suidffffbbbbb");
        result.setDropdata(new DropsData());

        fixture.copyProperties(result, data);

        Assert.assertNotNull(result.getSuid());
        Assert.assertNotNull(result.getDropdata());
    }

    @Test
    public void testCopyProperty_SimpleObject_Success() throws Exception {
        IngestWizardData data = new IngestWizardData();
        data.setSuid("Suidffffaaaaa");
        data.setDropdata(new DropsData());
        data.getDropdata().setDrops(new ArrayList<DropsData.Drop>());
        List<DropsData.Drop> drops = data.getDropdata().getDrops();

        DropsData.Drop drop = data.getDropdata().new Drop();
        drop.setSelected(true);
        drop.setName("aaaaa");
        drops.add(drop);

        IngestWizardData result = new IngestWizardData();
        result.setSuid("Suidffffbbbbb");
        result.setDropdata(new DropsData());
        result.getDropdata().setDrops(new ArrayList<DropsData.Drop>());
        List<DropsData.Drop> resultDrops = result.getDropdata().getDrops();

        DropsData.Drop dropResult = result.getDropdata().new Drop();
        dropResult.setSelected(false);
        dropResult.setName("bbbbb");
        resultDrops.add(dropResult);

        Map<String, Object> introspected = new org.apache.commons.beanutils.BeanMap(result);

        Map map = fixture.describe(result);
        fixture.copyProperties(result, data);

        Assert.assertNotSame(data.getDropdata(), result.getDropdata());
        Assert.assertEquals(drop.getSelected(), dropResult.getSelected());
        Assert.assertEquals(drop.getName(), dropResult.getName());
    }

    @Test
    public void testCopyProperty_NullDestObject_Success() throws Exception {
        IngestWizardData data = new IngestWizardData();
        data.setSuid("Suidffffaaaaa");
        data.setDropdata(new DropsData());
        data.getDropdata().setDrops(new ArrayList<DropsData.Drop>());
        List<DropsData.Drop> drops = data.getDropdata().getDrops();

        DropsData.Drop drop = data.getDropdata().new Drop();
        drop.setSelected(true);
        drop.setName("aaaaa");
        drops.add(drop);

        IngestWizardData result = new IngestWizardData();
        result.setSuid("Suidffffbbbbb");
        result.setDropdata(null);

        Map<String, Object> introspected = new org.apache.commons.beanutils.BeanMap(result);

        Map map = fixture.describe(result);
        fixture.copyProperties(result, data);

        Assert.assertSame(data.getDropdata(), result.getDropdata());
    }
}
