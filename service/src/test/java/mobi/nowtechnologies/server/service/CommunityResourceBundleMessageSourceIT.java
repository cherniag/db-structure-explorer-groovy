package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import java.util.Locale;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
public class CommunityResourceBundleMessageSourceIT {

    @Resource(name = "serviceMessageSource")
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @Test
    public void shouldGetMessageWhenDefaultMessageAndLocaleAndArgsAreNull() {
        //given
        String community = "o2";
        String code = "paypal.password";
        Object[] args = null;
        String defaultMessage = null;
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is("o2_password"));
    }

    @Test
    public void shouldGetMessageWhenDefaultMessageAndLocaleAreNullAndArgsIsEmptyArray() {
        //given
        String community = "o2";
        String code = "sms.weekReminder";
        Object[] args = {};
        String defaultMessage = null;
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is("FreeMsg:\nReminder that you are subscribed to the {0} at {1} GBP for {2} weeks.\nTo unsubscribe text STOP to {3} or call 08702480277"));
    }

    @Test
    public void shouldGetMessageFromGeneralPropertiesFileWhenDefaultMessageAndLocaleAreNullAndArgsIsNotEmptyArrayAndNoSuchKeyInSpecificCommunityPropertiesFile() {
        //given
        String community = "o2";
        String code = "sms.weekReminder";
        Object[] args = {"MusicQubed", "10", "666", "102"};
        String defaultMessage = null;
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is("FreeMsg:\nReminder that you are subscribed to the MusicQubed at 10 GBP for 666 weeks.\nTo unsubscribe text STOP to 102 or call 08702480277"));
    }

    @Test
    public void shouldGetDefaultMessageWhenDefaultMessageAndLocaleAreNullAndArgsIsNotEmptyArrayAndNoSuchKeyInPropertiesFiles() {
        //given
        String community = "o2";
        String code = "some.key";
        Object[] args = {"666"};
        String defaultMessage = "Some value: {0}";
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is("Some value: 666"));
    }

    @Test
    public void shouldGetMessageFromSpecificCommunityAndLocalPropertiesFileWhenDefaultMessageIsNullAndNoLocaleIsNotNullAndArgsIsNotEmptyArray() {
        //given
        String community = "vf";
        String code = "sms.charge.reminder.text.for.vf";
        Object[] args = {"MusicQubed", "10", "666", "102"};
        String defaultMessage = null;
        Locale locale = new Locale("nz");

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is("You are charged for 28 days continuously"));
    }

    @Test
    public void shouldGetMessageWhenLocaleAndArgsAreNull() {
        //given
        String community = "o2";
        String code = "paypal.password";
        Object[] args = null;
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, locale);

        //then
        assertThat(message, is("o2_password"));
    }

    @Test
    public void shouldGetMessageWhenLocaleIsNullAndArgsIsEmptyArray() {
        //given
        String community = "o2";
        String code = "sms.weekReminder";
        Object[] args = {};
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, locale);

        //then
        assertThat(message, is("FreeMsg:\nReminder that you are subscribed to the {0} at {1} GBP for {2} weeks.\nTo unsubscribe text STOP to {3} or call 08702480277"));
    }

    @Test
    public void shouldGetMessageFromGeneralPropertiesFileWhenDefaultMessageAndLocaleIsNullAndArgsIsNotEmptyArrayAndNoSuchKeyInSpecificCommunityPropertiesFile() {
        //given
        String community = "o2";
        String code = "sms.weekReminder";
        Object[] args = {"MusicQubed", "10", "666", "102"};
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, locale);

        //then
        assertThat(message, is("FreeMsg:\nReminder that you are subscribed to the MusicQubed at 10 GBP for 666 weeks.\nTo unsubscribe text STOP to 102 or call 08702480277"));
    }

    @Test
    public void shouldGetDefaultMessageWhenLocaleIsNullAndArgsIsNotEmptyArrayAndNoSuchKeyInPropertiesFiles() {
        //given
        String community = "o2";
        String code = "some.key";
        Object[] args = {"666"};
        Locale locale = null;

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, locale);

        //then
        assertThat(message, is("some.key"));
    }

    @Test
    public void shouldGetMessageFromSpecificCommunityLocalPropertiesFileWhenLocaleIsNotNullAndArgsIsNotEmptyArray() {
        //given
        String community = "vf";
        String code = "sms.charge.reminder.text.for.vf";
        Object[] args = {"MusicQubed", "10", "666", "102"};
        Locale locale = new Locale("nz");

        //when
        String message = communityResourceBundleMessageSource.getMessage(community, code, args, locale);

        //then
        assertThat(message, is("You are charged for 28 days continuously"));
    }
}