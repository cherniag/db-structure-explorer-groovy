package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.ReferralService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

// @author Titov Mykhaylo (titov) on 16.12.2014.
@RunWith(PowerMockRunner.class)
public class ReferralContextDtoFactoryImplTest {

    @Mock ReferralService referralServiceMock;
    @Mock CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;

    @InjectMocks ReferralContextDtoFactoryImpl referralContextDtoFactory;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetReferralContextDtoForUsersWithRegistrationDateAfterReferralLogicActivation() {
        //given
        DateTime referralLogicActivationDateTime = new DateTime(2015, 1, 28, 0, 0, 0, 0);
        User user = new User().withFirstDeviceLoginMillis(referralLogicActivationDateTime.minusYears(1).getMillis()).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("mtv1")));

        Date referralLogicActivationDate = referralLogicActivationDateTime.toDate();
        when(communityResourceBundleMessageSourceMock.readDate(user.getCommunityRewriteUrl(), "referral.logic.activation.date", referralLogicActivationDate)).thenReturn(referralLogicActivationDate);

        when(referralServiceMock.getRequiredReferralsCount(user.getCommunityRewriteUrl())).thenReturn(10);
        when(referralServiceMock.getActivatedReferralsCount(user)).thenReturn(9);

        //when
        ReferralContextDto referralContextDto = referralContextDtoFactory.getReferralContextDto(user);

        //then
        assertThat(referralContextDto, is(notNullValue()));

        assertThat(referralContextDto.getRequired(), is(-1));
        assertThat(referralContextDto.getActivated(), is(-1));
    }

    @Test
    public void shouldGetReferralContextDtoForUsersWithRegistrationDateBeforeReferralLogicActivation() {
        //given
        DateTime referralLogicActivationDateTime = new DateTime(2015, 1, 28, 0, 0, 0, 0);
        User user = new User().withFirstDeviceLoginMillis(referralLogicActivationDateTime.plusYears(1).getMillis()).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("mtv1")));

        Date referralLogicActivationDate = referralLogicActivationDateTime.toDate();
        when(communityResourceBundleMessageSourceMock.readDate(user.getCommunityRewriteUrl(), "referral.logic.activation.date", referralLogicActivationDate)).thenReturn(referralLogicActivationDate);

        when(referralServiceMock.getRequiredReferralsCount(user.getCommunityRewriteUrl())).thenReturn(10);
        when(referralServiceMock.getActivatedReferralsCount(user)).thenReturn(9);

        //when
        ReferralContextDto referralContextDto = referralContextDtoFactory.getReferralContextDto(user);

        //then
        assertThat(referralContextDto, is(notNullValue()));

        assertThat(referralContextDto.getRequired(), is(10));
        assertThat(referralContextDto.getActivated(), is(9));
    }
}
