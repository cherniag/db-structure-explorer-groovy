/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.runners.*;
import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class PropLocaleTest {

    PropLocale propLocale = new PropLocale();

    @Test
    public void shouldGetEmptyLocaleAsCommunityLocaleWhenCommunityAndLocaleAreNull() {
        //given
        final String community = null;
        final Locale locale = null;

        //when
        final Locale communityLocale = propLocale.getCommunityLocale(community, locale);

        //then
        assertThat(communityLocale, is(new Locale("")));
    }

    @Test
    public void shouldGetCommunityLocaleWhenCommunityIsNotNullAndLocaleIsNull() {
        //given
        final String community = "o2";
        final Locale locale = null;

        //when
        final Locale communityLocale = propLocale.getCommunityLocale(community, locale);

        //then
        assertThat(communityLocale, is(new Locale(community)));
    }

    @Test
    public void shouldGetCommunityLocaleWhenCommunityIsNotNullAndLocaleIsNotNull() {
        //given
        final String community = "o2";
        final Locale locale = Locale.ENGLISH;

        //when
        final Locale communityLocale = propLocale.getCommunityLocale(community, locale);

        //then
        assertThat(communityLocale, is(new Locale("o2_en")));
    }
}