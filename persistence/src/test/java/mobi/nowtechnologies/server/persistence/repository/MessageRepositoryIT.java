package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


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


}