package mobi.nowtechnologies.server.persistence.repository;

import javax.annotation.Resource;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * The class <code>MessageRepositoryTest</code> contains tests for the class <code>{@link MessageRepository}</code>.
 *
 * @generatedBy CodePro at 16.05.12 11:10
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(defaultRollback = true)
public class MessageRepositoryTest {
	
	@Resource(name = "messageRepository")
	private MessageRepository messageRepository;
	
	@Test
	@Ignore
	public void testCount() {
		long count = messageRepository.count();
		System.out.println(count);
	}
	
	@Test
	@Ignore
	public void testFindOne() {
		Message message = messageRepository.findOne(1);
		Assert.assertNotNull(message);
	}
	
	@Test
	@Ignore
	public void findMaxPosition(){
		Community community = CommunityDao.getCommunity("Now Music");
		Integer position = messageRepository.findMaxPosition(community, MessageType.NEWS, 0L);
		Assert.assertNotNull(position);
	}
	
	
}