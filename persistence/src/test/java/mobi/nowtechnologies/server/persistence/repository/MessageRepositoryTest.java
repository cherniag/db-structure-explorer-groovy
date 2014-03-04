package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * The class <code>MessageRepositoryTest</code> contains tests for the class <code>{@link MessageRepository}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 * @generatedBy CodePro at 16.05.12 11:10
 */
public class MessageRepositoryTest extends AbstractRepositoryIT {

    @Resource(name = "messageRepository")
    private MessageRepository messageRepository;

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
        Community community = CommunityDao.getCommunity("Now Music");
        Integer position = messageRepository.findMaxPosition(community, MessageType.NEWS, 1315686788000L);
        assertNotNull(position);
    }


}