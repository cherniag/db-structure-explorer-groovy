package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.MusicTrackDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType.BUTTON;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
public class StreamzineUpdateServiceTest {

    @InjectMocks
    StreamzineUpdateService streamzineUpdateServiceFixture;

    @Mock
    StreamzineUpdateRepository streamzineUpdateRepositoryMock;
    private Answer<Object> streamzineUpdateRepositoryUpdateAndFlushAnswer;

    @Before
    public void setUp() throws Exception {
        streamzineUpdateRepositoryUpdateAndFlushAnswer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        };
    }

    @Test
    public void shouldCreateNewIncludedUpdateWhenPreviousUpdateExists() throws Exception {
        //given
        Update prevUpdate = new Update(addDays(new Date(), 2));
        Block block = new Block(666, BUTTON, new MusicTrackDeeplinkInfo(null));
        block.exclude();
        prevUpdate.addBlock(block);

        Date newUpdateDate = addDays(prevUpdate.getDate(), 3);
        when(streamzineUpdateRepositoryMock.findLatestUpdateBeforeDate(newUpdateDate)).thenReturn(prevUpdate);

        when(streamzineUpdateRepositoryMock.saveAndFlush(any(Update.class))).thenAnswer(streamzineUpdateRepositoryUpdateAndFlushAnswer);

        //when
        Update update = streamzineUpdateServiceFixture.create(newUpdateDate);

        //then
        assertThat(update, is(not(prevUpdate)));
        assertThat(update.getBlocks().size(), is(prevUpdate.getBlocks().size()));
        assertThat(update.getBlocks().get(0).isIncluded(), is(true));

        verify(streamzineUpdateRepositoryMock, times(1)).findLatestUpdateBeforeDate(newUpdateDate);
        verify(streamzineUpdateRepositoryMock, times(1)).saveAndFlush(any(Update.class));
    }

    @Test
    public void shouldCreateNewIncludedUpdateEvenWhenPreviousUpdateDoesNotExist() throws Exception {
        //given
        Date newUpdateDate = addDays(new Date(), 1);
        Update prevUpdate = null;

        when(streamzineUpdateRepositoryMock.findLatestUpdateBeforeDate(newUpdateDate)).thenReturn(prevUpdate);

        when(streamzineUpdateRepositoryMock.saveAndFlush(any(Update.class))).thenAnswer(streamzineUpdateRepositoryUpdateAndFlushAnswer);

        //when
        Update update = streamzineUpdateServiceFixture.create(newUpdateDate);

        //then
        assertThat(update, is(not(prevUpdate)));
        assertThat(update.getBlocks().size(), is(0));

        verify(streamzineUpdateRepositoryMock, times(1)).findLatestUpdateBeforeDate(newUpdateDate);
        verify(streamzineUpdateRepositoryMock, times(1)).saveAndFlush(any(Update.class));
    }

    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }
}
