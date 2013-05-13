package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/**
 * s
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class UserLogRepositoryIT {
	
	@Resource(name = "userLogRepository")
	private UserLogRepository userLogRepository;
	
	@Test
	public void testCountByPhoneNumberAndDay_TwoSameDayAndOnePrevDayAndOneNextDay() {
		String phoneNumber = "+447870111111";
		long dayOfDate = (System.currentTimeMillis()-(4*60*60*1000))/Utils.DAY_MILLISECONDS;
		String description = "validate_phonenumber";
		UserLogType userLogType = UserLogType.VALIDATE_PHONE_NUMBER;
		
		UserLog userLog1 = new UserLog(phoneNumber, UserLogStatus.O2_FAIL, userLogType, description);
		UserLog userLog2 = new UserLog(phoneNumber, UserLogStatus.SUCCESS, userLogType, description);		
		UserLog userLog3 = new UserLog(phoneNumber, UserLogStatus.SUCCESS, userLogType, description);		
		userLog3.setLastUpdateMillis(userLog3.getLastUpdateMillis()-48*60*60*1000);
		UserLog userLog4 = new UserLog(phoneNumber, UserLogStatus.SUCCESS, userLogType, description);		
		userLog4.setLastUpdateMillis(userLog4.getLastUpdateMillis()+48*60*60*1000);
		
		userLogRepository.save(userLog1);
		userLogRepository.save(userLog2);
		userLogRepository.save(userLog3);
		userLogRepository.save(userLog4);
		
		long count = userLogRepository.countByPhoneNumberAndDay(phoneNumber, userLogType, dayOfDate);
		
		assertEquals(2, count);
	}	
}