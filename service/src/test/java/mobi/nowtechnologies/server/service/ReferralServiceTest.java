package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.referral.ReferralState;
import mobi.nowtechnologies.server.persistence.domain.referral.UserReferralsSnapshot;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReferralRepository;
import mobi.nowtechnologies.server.persistence.repository.UserReferralsSnapshotRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReferralServiceTest {
    @Mock
    private ReferralRepository referralRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommunityResourceBundleMessageSource messageSource;
    @Mock
    private UserReferralsSnapshotRepository userReferralsSnapshotRepository;

    @Mock
    private CommunityRepository communityRepository;
    @InjectMocks
    private ReferralService referralService;

    @Captor
    private ArgumentCaptor<List<String>> contactsCaptor;

    @Test
    public void testSaveReferrals() throws Exception {
        Referral r1 = createReferral("contact1@dot.com", 17);
        Referral r2 = createReferral("contact2@dot.com", 17);

        User user = createUser("user_name@dot.com", 33, 17);

        when(userService.getWithSocial(user.getId())).thenReturn(user);

        referralService.refer(Arrays.asList(r1, r2));

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

        referralService.refer(Arrays.asList(r1));

        verify(referralRepository, never()).save(r1);
    }

    @Test
    public void testSaveAlreadyExistingReferrals() throws Exception {
        Referral r1 = createReferral("contact1@dot.com", 17);
        Referral r2 = createReferral("contact2@dot.com", 17);

        User user = createUser("user_name@dot.com", 34, 17);

        when(userService.getWithSocial(user.getId())).thenReturn(user);
        when(referralRepository.save(r1)).thenThrow(new DataIntegrityViolationException(""));

        referralService.refer(Arrays.asList(r1, r2));

        verify(referralRepository).saveAndFlush(r1);
        verify(referralRepository).saveAndFlush(r2);
    }

    @Test
    public void testAcknowledgeByEmail() throws Exception {
        // given
        final int userId = 1;
        final int communityId = 2;
        final List<Integer> referralUserIds = Arrays.asList(1);
        UserReferralsSnapshot snapshot1 = createSnapshot(22, false);
        UserReferralsSnapshot snapshot2 = createSnapshot(33, true);
        final List<UserReferralsSnapshot> snapshots = Arrays.asList(snapshot1, snapshot2);

        UserGroup g = mock(UserGroup.class);
        Community c = mock(Community.class);

        User user = createUser("user_name@dot.com", 37, communityId);
        when(user.getId()).thenReturn(userId);
        when(user.getUserGroup()).thenReturn(g);
        when(g.getCommunity()).thenReturn(c);
        when(c.getId()).thenReturn(communityId);

        when(referralRepository.findReferralUserIdsByContacts(eq(communityId), anyList())).thenReturn(referralUserIds);
        when(userReferralsSnapshotRepository.findAll(referralUserIds)).thenReturn(snapshots);

        // when
        referralService.acknowledge(user, "test.email@domain.com");

        // then
        verify(referralRepository).updateReferrals(contactsCaptor.capture(), eq(communityId), eq(ReferralState.ACTIVATED), eq(ReferralState.PENDING));

        List<String> contacts = contactsCaptor.getValue();
        assertEquals(1, contacts.size());
        assertEquals("test.email@domain.com", contacts.get(0));

        verify(userReferralsSnapshotRepository).findAll(referralUserIds);

        verify(referralRepository).getCountByCommunityIdUserIdAndStates(communityId, snapshot1.getUserId(), ReferralState.ACTIVATED);
        snapshot1.updateMatchesData(anyInt());
    }

    @Test
    public void testAcknowledgeBySocial() throws Exception {
        // given
        final int userId = 1;
        final int communityId = 2;

        final List<Integer> referralUserIds = Arrays.asList(1);
        UserReferralsSnapshot snapshot1 = createSnapshot(22, false);
        UserReferralsSnapshot snapshot2 = createSnapshot(33, true);
        final List<UserReferralsSnapshot> snapshots = Arrays.asList(snapshot1, snapshot2);

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

        when(referralRepository.findReferralUserIdsByContacts(eq(communityId), anyList())).thenReturn(referralUserIds);
        when(userReferralsSnapshotRepository.findAll(referralUserIds)).thenReturn(snapshots);

        // when
        referralService.acknowledge(user, socialInfo);

        // then
        verify(referralRepository).updateReferrals(contactsCaptor.capture(), eq(communityId), eq(ReferralState.ACTIVATED), eq(ReferralState.PENDING));
        assertEquals(2, contactsCaptor.getValue().size());
        assertTrue(contactsCaptor.getValue().contains("user_name@dot.com"));
        assertTrue(contactsCaptor.getValue().contains("social.id.1"));

        List<String> contacts = contactsCaptor.getValue();
        assertEquals(2, contacts.size());
        assertTrue(contacts.contains("user_name@dot.com"));
        assertTrue(contacts.contains("social.id.1"));

        verify(userReferralsSnapshotRepository).findAll(referralUserIds);

        verify(referralRepository).getCountByCommunityIdUserIdAndStates(communityId, snapshot1.getUserId(), ReferralState.ACTIVATED);
        snapshot1.updateMatchesData(anyInt());
    }

    private UserReferralsSnapshot createSnapshot(int userId, boolean isMatched) {
        UserReferralsSnapshot snapshot = mock(UserReferralsSnapshot.class);
        when(snapshot.getUserId()).thenReturn(userId);
        when(snapshot.isMatched()).thenReturn(isMatched);
        return snapshot;
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