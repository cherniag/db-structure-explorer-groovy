package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static mobi.nowtechnologies.server.shared.enums.MessageActionType.A_SPECIFIC_TRACK;
import static mobi.nowtechnologies.server.shared.enums.MessageType.FREE_TRIAL_BANNER;
import static mobi.nowtechnologies.server.shared.enums.MessageType.LIMITED_BANNER;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertThat;


/**
 * @author Titov Mykhaylo (titov)
 */
public class MessageRepositoryIT extends AbstractRepositoryIT {

    @Resource(name = "messageRepository")
    private MessageRepository messageRepository;

    @Resource(name = "communityRepository")
    private CommunityRepository communityRepository;

    @Test
    public void testCount() {
        long count = messageRepository.count();
        assertTrue(count > 0);
    }

    @Test
    public void testFindOne() {
        Message message = messageRepository.findOne(1);
        assertNotNull(message);
    }

    @Test
    public void findMaxPosition() {
        Community community = communityRepository.findByName("Now Music");
        Integer position = messageRepository.findMaxPosition(community, MessageType.NEWS, 1315686788000L);
        assertNotNull(position);
    }

    @Test
    public void shouldNotReturnBanners(){
        //given
        Community community = communityRepository.findByRewriteUrlParameter("o2");
        long publishTimeMillis = 1315686788000L;
        messageRepository.save(new Message().withPublishTimeMillis(publishTimeMillis).withTitle("title").withMessageType(LIMITED_BANNER).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));

        //when
        List<Message> messages = messageRepository.findWithoutBannersByCommunityAndPublishTimeMillisAfterOrderByPositionAsc(community, publishTimeMillis);

        //then
        for (Message actualMessage : messages) {
            assertThat(actualMessage.getMessageType(), not(isIn(MessageType.getBannerTypes())));
        }
    }

    @Test
    public void shouldReturnBanners(){
        //given
        Community community = communityRepository.findByRewriteUrlParameter("o2");
        long publishTimeMillis = 1315686788000L;
        Message message = messageRepository.save(new Message().withPublishTimeMillis(publishTimeMillis).withTitle("title").withMessageType(LIMITED_BANNER).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));

        //when
        List<Message> messages = messageRepository.findByCommunityAndPublishTimeMillisAfterOrderByPositionAsc(community, publishTimeMillis);

        //then
        MessageType messageType = null;
        for (Message actualMessage : messages) {
            if(actualMessage.getId().equals(message.getId())) {
                messageType = actualMessage.getMessageType();
                break;
            }
        }
        assertThat(messageType, is(LIMITED_BANNER));
    }
}