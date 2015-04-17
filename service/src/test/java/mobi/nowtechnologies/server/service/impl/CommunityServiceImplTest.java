package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;

import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.unmodifiableList;

import org.junit.*;
import org.mockito.*;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import static org.hamcrest.core.Is.is;

import static org.powermock.api.mockito.PowerMockito.when;

public class CommunityServiceImplTest {

    @Mock
    CommunityRepository communityRepositoryMock;
    @InjectMocks
    CommunityServiceImpl communityService;

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