package mobi.nowtechnologies.server.service.impl;

import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.*;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.CommunityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class CommunityServiceImplTest {

    @Mock CommunityRepository communityRepositoryMock;
    @InjectMocks CommunityServiceImpl communityService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetLiveCommunities() {
        //given
        List<Community> liveCommunities = unmodifiableList(new ArrayList<Community>());
        when(communityRepositoryMock.findByLive(true)).thenReturn(liveCommunities);

        //when
        List<Community> actualLiveCommunities = communityService.getLiveCommunities();

        //then
        assertThat(actualLiveCommunities, is(liveCommunities));

        verify(communityRepositoryMock).findByLive(true);
    }
}