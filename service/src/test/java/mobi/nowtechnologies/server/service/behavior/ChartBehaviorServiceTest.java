package mobi.nowtechnologies.server.service.behavior;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkUrlFactory;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehavior;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartBehaviorType;
import mobi.nowtechnologies.server.persistence.domain.behavior.ChartUserStatusBehavior;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChartBehaviorServiceTest {
    @Mock
    private User user;
    @Mock
    private Community community;
    @Mock
    private ChartUserStatusBehavior limitedChartUserStatusBehavior;
    @Mock
    private ChartUserStatusBehavior subscribedChartUserStatusBehavior;
    @Mock
    private ChartBehavior limitedChartBehavior;
    @Mock
    private ChartBehavior subscribedChartBehavior;
    @Mock
    private DeepLinkUrlFactory deepLinkUrlFactory;
    @InjectMocks
    private ChartBehaviorService chartBehaviorService;

    private Map<Integer, Map<UserStatusType, ChartUserStatusBehavior>> chartToStatusBehaviorMapping = new HashMap<>();


    @Before
    public void setUp() throws Exception {
        when(community.getRewriteUrlParameter()).thenReturn("mtv1");
        when(user.getCommunity()).thenReturn(community);

        when(deepLinkUrlFactory.createForChart(community, 1, "action")).thenReturn("mtv1://unlock?action");

        when(subscribedChartBehavior.getType()).thenReturn(ChartBehaviorType.NORMAL);
        when(limitedChartBehavior.getType()).thenReturn(ChartBehaviorType.PREVIEW);

        when(limitedChartUserStatusBehavior.getAction()).thenReturn("action");
        when(limitedChartUserStatusBehavior.getUserStatusType()).thenReturn(UserStatusType.LIMITED);
        when(limitedChartUserStatusBehavior.getChartId()).thenReturn(1);
        when(limitedChartUserStatusBehavior.getChartBehavior()).thenReturn(limitedChartBehavior);

        when(subscribedChartUserStatusBehavior.getAction()).thenReturn(null);
        when(subscribedChartUserStatusBehavior.getUserStatusType()).thenReturn(UserStatusType.SUBSCRIBED);
        when(subscribedChartUserStatusBehavior.getChartId()).thenReturn(1);
        when(subscribedChartUserStatusBehavior.getChartBehavior()).thenReturn(subscribedChartBehavior);

        chartToStatusBehaviorMapping.put(1, getUserStatusChartBehaviorMap());
    }

    @Test
    public void testCreateInfos() throws Exception {
        final Date currentDate = new Date();
        final Date futureDate = DateUtils.addDays(new Date(), 1);

        List<Pair<UserStatusType, Date>> userStatusTypeSinceChronology = new ArrayList<>();
        userStatusTypeSinceChronology.add(new ImmutablePair<>(UserStatusType.SUBSCRIBED, currentDate));
        userStatusTypeSinceChronology.add(new ImmutablePair<>(UserStatusType.LIMITED, futureDate));

        NavigableSet<ChartBehaviorInfo> infos = chartBehaviorService.createInfos(user, Iterables.get(chartToStatusBehaviorMapping.entrySet(), 0), userStatusTypeSinceChronology);

        assertEquals(2, infos.size());

        ChartBehaviorInfo first = Iterables.get(infos, 0);
        assertEquals(ChartBehaviorType.NORMAL, first.chartBehaviorType);
        assertFalse(first.canBeUnlocked);
        assertNull(first.lockedAction);

        ChartBehaviorInfo second = Iterables.get(infos, 1);
        assertEquals(ChartBehaviorType.PREVIEW, second.chartBehaviorType);
        assertFalse(second.canBeUnlocked);
        assertEquals("mtv1://unlock?action", second.lockedAction);
    }

    @Test
    public void testCreateInfosWithReferAFriendLockAction() throws Exception {
        when(limitedChartUserStatusBehavior.getAction()).thenReturn("refer_a_friend");
        when(deepLinkUrlFactory.createForChart(community, 1, "refer_a_friend")).thenReturn("mtv1://unlock?refer_a_friend");

        final Date currentDate = new Date();
        final Date futureDate = DateUtils.addDays(new Date(), 1);

        List<Pair<UserStatusType, Date>> userStatusTypeSinceChronology = new ArrayList<>();
        userStatusTypeSinceChronology.add(new ImmutablePair<>(UserStatusType.SUBSCRIBED, currentDate));
        userStatusTypeSinceChronology.add(new ImmutablePair<>(UserStatusType.LIMITED, futureDate));

        NavigableSet<ChartBehaviorInfo> infos = chartBehaviorService.createInfos(user, Iterables.get(chartToStatusBehaviorMapping.entrySet(), 0), userStatusTypeSinceChronology);

        assertEquals(2, infos.size());

        ChartBehaviorInfo first = Iterables.get(infos, 0);
        assertEquals(ChartBehaviorType.NORMAL, first.chartBehaviorType);
        assertFalse(first.canBeUnlocked);
        assertNull(first.lockedAction);

        ChartBehaviorInfo second = Iterables.get(infos, 1);
        assertEquals(ChartBehaviorType.PREVIEW, second.chartBehaviorType);
        assertTrue(second.canBeUnlocked);
        assertEquals("mtv1://unlock?refer_a_friend", second.lockedAction);
    }

    private Map<UserStatusType, ChartUserStatusBehavior> getUserStatusChartBehaviorMap() {
        Map<UserStatusType, ChartUserStatusBehavior> userStatusBehaviorMap = new HashMap<>();
        userStatusBehaviorMap.put(UserStatusType.LIMITED, limitedChartUserStatusBehavior);
        userStatusBehaviorMap.put(UserStatusType.SUBSCRIBED, subscribedChartUserStatusBehavior);
        return userStatusBehaviorMap;
    }
}