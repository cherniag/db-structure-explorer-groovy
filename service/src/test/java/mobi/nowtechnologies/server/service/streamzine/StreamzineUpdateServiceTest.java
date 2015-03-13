package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType.SLIM_BANNER;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import static org.apache.commons.lang3.time.DateUtils.addDays;

import org.junit.*;
import org.junit.rules.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class StreamzineUpdateServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    StreamzineUpdateService streamzineUpdateServiceFixture;
    @Mock
    StreamzineUpdateRepository streamzineUpdateRepositoryMock;
    private Answer<Object> streamzineUpdateRepositoryUpdateAndFlushAnswer;
    @Captor
    private ArgumentCaptor<Date> from;
    @Captor
    private ArgumentCaptor<Date> till;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        streamzineUpdateRepositoryUpdateAndFlushAnswer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        };

        streamzineUpdateServiceFixture.setAvailableCommunites("hl_uk");
    }

    @Test
    public void shouldCreateNewIncludedUpdateWhenPreviousUpdateExists() throws Exception {
        //given
        Community community = mock(Community.class);
        when(community.getRewriteUrlParameter()).thenReturn("hl_uk");

        Update prevUpdate = new Update(addDays(new Date(), 2), community);
        Block block = new Block(666, SLIM_BANNER, new MusicTrackDeeplinkInfo(null, PlayerType.MINI_PLAYER_ONLY));
        block.exclude();
        prevUpdate.addBlock(block);

        Date newUpdateDate = addDays(prevUpdate.getDate(), 3);
        when(streamzineUpdateRepositoryMock.findLastDateSince(newUpdateDate, community)).thenReturn(prevUpdate.getDate());
        when(streamzineUpdateRepositoryMock.findByPublishDate(prevUpdate.getDate(), community)).thenReturn(prevUpdate);

        when(streamzineUpdateRepositoryMock.saveAndFlush(any(Update.class))).thenAnswer(streamzineUpdateRepositoryUpdateAndFlushAnswer);

        //when
        Update update = streamzineUpdateServiceFixture.create(newUpdateDate, community);

        //then
        assertThat(update, is(not(prevUpdate)));
        assertThat(update.getBlocks().size(), is(prevUpdate.getBlocks().size()));
        assertThat(update.getBlocks().get(0).isIncluded(), is(true));

        verify(streamzineUpdateRepositoryMock, times(1)).findLastDateSince(newUpdateDate, community);
        verify(streamzineUpdateRepositoryMock, times(1)).findByPublishDate(prevUpdate.getDate(), community);
        verify(streamzineUpdateRepositoryMock, times(1)).saveAndFlush(any(Update.class));
    }

    @Test
    public void shouldCreateNewIncludedUpdateEvenWhenPreviousUpdateDoesNotExist() throws Exception {
        //given
        Community community = mock(Community.class);
        when(community.getRewriteUrlParameter()).thenReturn("hl_uk");
        Date newUpdateDate = addDays(new Date(), 1);
        Update prevUpdate = null;


        when(streamzineUpdateRepositoryMock.findLastDateSince(newUpdateDate, community)).thenReturn(null);

        when(streamzineUpdateRepositoryMock.saveAndFlush(any(Update.class))).thenAnswer(streamzineUpdateRepositoryUpdateAndFlushAnswer);

        //when
        Update update = streamzineUpdateServiceFixture.create(newUpdateDate, community);

        //then
        assertThat(update, is(not(prevUpdate)));
        assertThat(update.getBlocks().size(), is(0));

        verify(streamzineUpdateRepositoryMock, times(1)).findLastDateSince(newUpdateDate, community);
        verify(streamzineUpdateRepositoryMock, times(1)).saveAndFlush(any(Update.class));
    }

    @Test
    public void testList() throws Exception {
        // given
        Community community = mock(Community.class);

        Calendar c = Calendar.getInstance();
        c.set(2015, Calendar.NOVEMBER, 3);

        // when
        streamzineUpdateServiceFixture.list(c.getTime(), community);

        // then
        verify(streamzineUpdateRepositoryMock).findAllByDate(from.capture(), till.capture(), eq(community));
        assertEquals(DateUtils.addDays(from.getValue(), 1), till.getValue());
    }

    @Test
    public void testDeleteWhenCanEdit() throws Exception {
        // given
        long id = 1L;
        Update update = mock(Update.class);
        when(update.canEdit()).thenReturn(true);
        when(streamzineUpdateRepositoryMock.findOne(id)).thenReturn(update);

        // when
        streamzineUpdateServiceFixture.delete(id);

        // then
        verify(streamzineUpdateRepositoryMock).findOne(id);
        verify(streamzineUpdateRepositoryMock).delete(id);
    }

    @Test
    public void testDeleteWhenCanNotEdit() throws Exception {
        // given
        long id = 1L;
        Update update = mock(Update.class);
        when(update.canEdit()).thenReturn(false);
        when(streamzineUpdateRepositoryMock.findOne(id)).thenReturn(update);

        thrown.expect(IllegalArgumentException.class);

        // when
        streamzineUpdateServiceFixture.delete(id);
    }

    @Test
    public void testUpdateWhenCanEdit() throws Exception {
        // given
        long id = 1L;
        Update update = mock(Update.class);
        when(update.canEdit()).thenReturn(true);
        when(streamzineUpdateRepositoryMock.findOne(id)).thenReturn(update);

        // when
        Update incoming = mock(Update.class);
        streamzineUpdateServiceFixture.update(id, incoming);

        // then
        verify(update).updateFrom(incoming);
        verify(update).canEdit();
        verify(streamzineUpdateRepositoryMock).save(update);
    }

    @Test
    public void testUpdateWhenCanNotEdit() throws Exception {
        // given
        long id = 1L;
        Update update = mock(Update.class);
        when(update.canEdit()).thenReturn(false);
        when(streamzineUpdateRepositoryMock.findOne(id)).thenReturn(update);

        thrown.expect(IllegalArgumentException.class);

        // when
        Update incoming = mock(Update.class);
        streamzineUpdateServiceFixture.update(id, incoming);
    }

    @Test
    public void testGet() throws Exception {
        // given
        long id = 1L;
        Update update = mock(Update.class);
        when(streamzineUpdateRepositoryMock.findOne(id)).thenReturn(update);
        when(streamzineUpdateRepositoryMock.findById(id)).thenReturn(update);

        // when
        Update found = streamzineUpdateServiceFixture.get(id);

        // then
        verify(streamzineUpdateRepositoryMock).findById(id);
        assertSame(found, update);
    }
}
