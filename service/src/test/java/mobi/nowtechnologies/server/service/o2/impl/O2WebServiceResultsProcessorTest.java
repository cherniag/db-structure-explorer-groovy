package mobi.nowtechnologies.server.service.o2.impl;

import org.junit.Test;

import static mobi.nowtechnologies.server.service.o2.impl.O2WebServiceResultsProcessor.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class O2WebServiceResultsProcessorTest {

    @Test
    public void shouldBe4GWhenTariffIdIs43() throws Exception {
        //given
        int tariffId = 43;

        //when
        boolean is4G = is4GTariffId(tariffId);

        //then
        assertThat(is4G, is(true));
    }

    @Test
    public void shouldBe4GWhenTariffIdIs44() throws Exception {
        //given
        int tariffId = 44;

        //when
        boolean is4G = is4GTariffId(tariffId);

        //then
        assertThat(is4G, is(true));
    }

    @Test
    public void shouldBe4GWhenTariffIdIs45() throws Exception {
        //given
        int tariffId = 45;

        //when
        boolean is4G = is4GTariffId(tariffId);

        //then
        assertThat(is4G, is(true));
    }

    @Test
    public void shouldBe4GWhenTariffIdIs47() throws Exception {
        //given
        int tariffId = 47;

        //when
        boolean is4G = is4GTariffId(tariffId);

        //then
        assertThat(is4G, is(true));
    }

    @Test
    public void shouldBe4GWhenTariffIdIs48() throws Exception {
        //given
        int tariffId = 48;

        //when
        boolean is4G = is4GTariffId(tariffId);

        //then
        assertThat(is4G, is(true));
    }
}