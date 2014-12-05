package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ReferralServiceTest {
    @Mock
    private ReferralRepository referralRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommunityResourceBundleMessageSource messageSource;

    @Mock
    private CommunityRepository communityRepository;
    @InjectMocks
    private ReferralService referralService;

    @Captor
    private ArgumentCaptor<List<String>> contactsCaptor;

    @Test
    public void testGetRequiredReferralsCount() {
        String requiredPropertyName = "requiredPropertyName";
        String community = "community";
        int result = 3;

        when(messageSource.readInt(community, requiredPropertyName, 5, null)).thenReturn(result);

        referralService.setRequiredPropertyName(requiredPropertyName);
        assertEquals(result, referralService.getRequiredReferralsCount(community));

        verify(messageSource, times(1)).readInt(community, requiredPropertyName, 5, null);
        verifyNoMoreInteractions(messageSource, referralRepository, userService);
    }

    @Test
    public void testGetActivatedReferralsCount() {
        int result = 3;
        int communityId = 1;
        int userId = 11;

        User user = mock(User.class);
        when(user.getCommunityId()).thenReturn(communityId);
        when(user.getId()).thenReturn(userId);

        when(referralRepository.getCountByCommunityIdUserIdAndStates(
                communityId,
                userId,
                Arrays.asList(ReferralState.ACTIVATED))).thenReturn(result);

        assertEquals(result, referralService.getActivatedReferralsCount(user));

        verify(user, times(1)).getCommunityId();
        verify(user, times(1)).getId();
        verify(referralRepository, times(1)).
                getCountByCommunityIdUserIdAndStates(communityId, userId, Arrays.asList(ReferralState.ACTIVATED));
        verifyNoMoreInteractions(referralRepository, userService, messageSource, user);
    }

    @Test
    public void testSaveReferrals() throws Exception {
        Referral r1 = createReferral("contact1@dot.com", 17);
        Referral r2 = createReferral("contact2@dot.com", 17);

        User user = createUser("user_name@dot.com", 33, 17);

        when(userService.getWithSocial(user.getId())).thenReturn(user);

        referralService.refer(Lists.newArrayList(r1, r2));

        verify(referralRepository).saveAndFlush(r1);
        verify(referralRepository).saveAndFlush(r2);
    }

    @Test
    public void testSaveReferralWithTheSameUserName() throws Exception {
        Referral r1 = createReferral("user_name@dot.com", 17);
        when(r1.getProviderType()).thenReturn(ProviderType.EMAIL);

        User user = createUser("user_name@dot.com", 34, 17);

        when(userService.findByName(user.getUserName())).thenReturn(user);
        Community someCommunity = CommunityFactory.createCommunityMock(17, "some_community");
        when(communityRepository.findOne(17)).thenReturn(someCommunity);

        referralService.refer(Lists.newArrayList(r1));

        verify(referralRepository, never()).save(r1);
    }

    @Test
    public void testSaveAlreadyExistingReferrals() throws Exception {
        Referral r1 = createReferral("contact1@dot.com", 17);
        Referral r2 = createReferral("contact2@dot.com", 17);

        User user = createUser("user_name@dot.com", 34, 17);

        when(userService.getWithSocial(user.getId())).thenReturn(user);
        when(referralRepository.save(r1)).thenThrow(new DataIntegrityViolationException(""));

        referralService.refer(Lists.newArrayList(r1, r2));

        verify(referralRepository).saveAndFlush(r1);
        verify(referralRepository).saveAndFlush(r2);
    }

    @Test
    public void testAcknowledgeByEmail() throws Exception {
        // given
        final int userId = 1;
        final int communityId = 2;

        UserGroup g = mock(UserGroup.class);
        Community c = mock(Community.class);

        User user = createUser("user_name@dot.com", 37, communityId);
        when(user.getId()).thenReturn(userId);
        when(user.getUserGroup()).thenReturn(g);
        when(g.getCommunity()).thenReturn(c);
        when(c.getId()).thenReturn(communityId);

        // when
        referralService.acknowledge(user, "test.email@domain.com");

        // then
        verify(referralRepository).updateReferrals(contactsCaptor.capture(), eq(communityId), eq(ReferralState.ACTIVATED), eq(ReferralState.PENDING));
        assertEquals(1, contactsCaptor.getValue().size());
        assertEquals("test.email@domain.com", contactsCaptor.getValue().get(0));
    }

    @Test
    public void testAcknowledgeBySocial() throws Exception {
        // given
        final int userId = 1;
        final int communityId = 2;

        UserGroup g = mock(UserGroup.class);
        Community c = mock(Community.class);
        SocialInfo socialInfo = mock(SocialInfo.class);
        when(socialInfo.getSocialId()).thenReturn("social.id.1");
        when(socialInfo.getEmail()).thenReturn("user_name@dot.com");

        User user = createUser("user_name@dot.com", 37, communityId);
        when(user.getId()).thenReturn(userId);
        when(user.getUserGroup()).thenReturn(g);
        when(g.getCommunity()).thenReturn(c);
        when(c.getId()).thenReturn(communityId);

        // when
        referralService.acknowledge(user, socialInfo);

        // then
        verify(referralRepository).updateReferrals(contactsCaptor.capture(), eq(communityId), eq(ReferralState.ACTIVATED), eq(ReferralState.PENDING));
        assertEquals(2, contactsCaptor.getValue().size());
        assertTrue(contactsCaptor.getValue().contains("user_name@dot.com"));
        assertTrue(contactsCaptor.getValue().contains("social.id.1"));
    }

    private User createUser(String name, int id, int communityId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getUserName()).thenReturn(name);
        when(user.getCommunityId()).thenReturn(communityId);
        return user;
    }

    private Referral createReferral(String contact, int communityId) {
        Referral r2 = mock(Referral.class);
        when(r2.getProviderType()).thenReturn(ProviderType.EMAIL);
        when(r2.getContact()).thenReturn(contact);
        when(r2.getCommunityId()).thenReturn(communityId);
        return r2;
    }


}