package mobi.nowtechnologies.server.service;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.ManualCompilationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineUpdateAdminAsm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


/**
 * Author: Gennadii Cherniaiev
 * Date: 3/21/14
 */
@RunWith(MockitoJUnitRunner.class)
public class StreamzineUpdateAdminAsmTest {
    private static final String COMMUNITY = "c1";

    @Mock
    private MessageSource messageSource;
    @Mock
    private DeepLinkInfoService deepLinkInfoService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;

    @Test
    public void checkFromIncomingDtoWithoutUser() throws Exception {
        UpdateIncomingDto updateIncomingDto = getUpdateIncomingDto();
        updateIncomingDto.setUserName(null);
        Update update = streamzineUpdateAdminAsm.fromIncomingDto(updateIncomingDto, COMMUNITY);
        assertNull(update.getUser());
    }
    @Test
    public void checkFromIncomingDtoWithUser() throws Exception {
        int id = 15;
        String userName = "pedro";
        User user = getUser(id, userName);
        when(userRepository.findOne(eq(userName), eq(COMMUNITY))).thenReturn(user);
        UpdateIncomingDto updateIncomingDto = getUpdateIncomingDto();
        updateIncomingDto.setUserName(userName);

        Update update = streamzineUpdateAdminAsm.fromIncomingDto(updateIncomingDto, COMMUNITY);
        assertNotNull(update.getUser());
        assertEquals(id, update.getUser().getId());
        assertEquals(userName, update.getUser().getUserName());
    }

    @Test
    public void checkFromIncomingDtoWithBlocks() throws Exception {
        Media media = new Media();
        ManualCompilationDeeplinkInfo deeplinkInfo = new ManualCompilationDeeplinkInfo(Lists.newArrayList(media));

        when(deepLinkInfoService.create(Matchers.any(DeepLinkInfoService.DeeplinkInfoData.class))).thenReturn(deeplinkInfo);

        OrdinalBlockDto ordinalBlockDto = new OrdinalBlockDto();
        ordinalBlockDto.setContentType(ContentType.MUSIC);
        ordinalBlockDto.setPosition(1);
        ordinalBlockDto.setShapeType(ShapeType.WIDE);
        ordinalBlockDto.setKey("MANUAL_COMPILATION");
        ordinalBlockDto.setValue("ISRC-10#ISRC-11#ISRC-12");
        UpdateIncomingDto updateIncomingDto = getUpdateIncomingDto();
        updateIncomingDto.getBlocks().add(ordinalBlockDto);

        Update update = streamzineUpdateAdminAsm.fromIncomingDto(updateIncomingDto, COMMUNITY);
        assertNotNull(update);
        assertEquals(1, update.getBlocks().size());

        Block block = update.getBlocks().get(0);
        assertEquals(ShapeType.WIDE, block.getShapeType());
        assertTrue(block.getDeeplinkInfo() instanceof ManualCompilationDeeplinkInfo);

    }

    private User getUser(int id, String userName) {
        User user = new User();
        user.setUserName(userName);
        user.setId(id);
        return user;
    }

    private UpdateIncomingDto getUpdateIncomingDto() {
        UpdateIncomingDto updateIncomingDto = new UpdateIncomingDto();
        updateIncomingDto.setTimestamp(System.currentTimeMillis() + 10000L);
        return updateIncomingDto;
    }
}
