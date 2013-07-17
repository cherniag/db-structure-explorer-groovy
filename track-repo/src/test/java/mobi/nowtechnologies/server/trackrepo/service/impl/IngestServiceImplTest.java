package mobi.nowtechnologies.server.trackrepo.service.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestSessionClosed;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/17/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class IngestServiceImplTest {
    private IngestServiceImpl fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new IngestServiceImpl();
    }

    @Test
    public void testGetIngestData_NullDataAndFreeBuffer_Success() throws Exception {
        IngestWizardData data = null;
        Long curTime = System.currentTimeMillis();
        curTime = curTime - curTime % 100000;

        IngestWizardData result = fixture.getIngestData(data, false);

        Assert.assertNotNull(result);
        Long suid = new Long(result.getSuid());
        Assert.assertEquals(curTime.longValue(), suid - suid % 100000);
        Assert.assertEquals(1, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testGetIngestData_NullDataAndFullBufferWithExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i < IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime + i * 1000));
            fixture.ingestDataBuffer.put(data.getSuid(), data);
        }
        IngestWizardData dataExpired = new IngestWizardData();
        dataExpired.setSuid(String.valueOf(curTime - IngestServiceImpl.EXPIRE_PERIOD_BUFFER * 2));
        fixture.ingestDataBuffer.put(dataExpired.getSuid(), dataExpired);

        IngestWizardData data = null;

        IngestWizardData result = fixture.getIngestData(data, false);

        Assert.assertNull(fixture.ingestDataBuffer.get(dataExpired.getSuid()));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testGetIngestData_NullDataAndFullBufferWithoutExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i <= IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime - i * 1000));
            fixture.ingestDataBuffer.put(data.getSuid(), data);
        }

        IngestWizardData data = null;

        IngestWizardData result = fixture.getIngestData(data, false);

        Assert.assertNull(fixture.ingestDataBuffer.get(curTime - IngestServiceImpl.MAX_SIZE_DATA_BUFFER * 1000));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, fixture.ingestDataBuffer.size());
    }

    @Test(expected = IngestSessionClosed.class)
    public void testGetIngestData_NotNullDataNotInBuffer_Failure() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data = new IngestWizardData();

        fixture.getIngestData(data, false);
    }

    @Test
    public void testGetIngestData_NotNullDataAndRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        fixture.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = fixture.getIngestData(data, true);

        Assert.assertSame(data1, result);
        Assert.assertEquals(0, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testGetIngestData_NotNullDataAndNotRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        fixture.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = fixture.getIngestData(data, false);

        Assert.assertSame(data1, result);
        Assert.assertEquals(1, fixture.ingestDataBuffer.size());
    }
}
