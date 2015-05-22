package mobi.nowtechnologies.server.service.o2.impl;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

public class O2WebServiceResultsProcessorTest {
    
    private O2WebServiceResultsProcessor processor = new O2WebServiceResultsProcessor();
    private int[] tariffIds = {43, 44, 45, 46, 47, 48, 52};

    @Test
    public void shouldBe4GTariff() throws Exception {
        for (int tariffId : tariffIds) {
            assertThat(processor.is4GTariffId(tariffId), is(true));
        }
    }

}