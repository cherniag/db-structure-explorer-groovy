package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * CreateUserTest
 * 
 * @author Maksym Chernolevskyi (maksym)
 */
@Ignore
public class CreateUserTest {
	private static final int MAX_USERS = 1000000;
	private static final String PASSWORD = "12345678";
	private static final String TIMESTAMP = "Tue Jul 12 15:22:19 GMT+00:00 2011";
	private static final String BASIC_USERNAME = "user4@cn.com";
	private static final Integer BASIC_ID = 131;
	
	//7e772ca3f286a056c05a726a78e492de - 
	//token for user "bcherry@bcherry.com" and timestamp
	//"Tue Jul 12 13:57:22 GMT+00:00 2011"
	
	private static EntityDao entityDao;
	
	@Ignore
	@Test
	public void testCreateUsers() throws Exception {
		User user = entityDao.findById(User.class, BASIC_ID);
		for (int i = 0; i < MAX_USERS; i++) {
			String username = BASIC_USERNAME +  "_" + i;
			String token = Utils.createStoredToken(username, PASSWORD);
			createUser(user, username, token);
			writeToCcvFile(username, Utils.createTimestampToken(token, TIMESTAMP));
		}
	}

	private void createUser(User user, String username, String token) {
		user.setId(0);
		user.setUserName(username);
		user.setToken(token);
		entityDao.saveEntity(user);
	}

	private void writeToCcvFile(String username, String timestampToken) {
		try {
			RandomAccessFile file = new RandomAccessFile("out.csv", "rw");
			file.seek(file.length());
			file.writeBytes(String.format("%s;%s\r\n", username, timestampToken));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "/META-INF/dao-test.xml" });
		entityDao = (EntityDao) appContext.getBean("persistence.EntityDao");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		// Add additional tear down code here
	}
}