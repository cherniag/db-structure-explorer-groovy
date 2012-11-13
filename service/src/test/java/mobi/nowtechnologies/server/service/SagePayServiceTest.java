package mobi.nowtechnologies.server.service;

import static mobi.nowtechnologies.server.shared.Utils.getBigRandomInt;

import java.math.BigDecimal;

import junit.framework.TestCase;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.Payment;
import mobi.nowtechnologies.server.persistence.domain.User;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * SagePayServiceTest
 *
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Ignore
public class SagePayServiceTest extends TestCase {
	private SagePayService service;

	@Override
	public void setUp()
		throws Exception {
		new ClassPathXmlApplicationContext(
				new String[] {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" });
	}
	
	@Test
	public void testSagePayScenario() throws InterruptedException {
		UserRegInfo userRegInfo = new UserRegInfo();
		userRegInfo.setCardHolderFirstName("John");
		userRegInfo.setCardHolderLastName("Smith");
		userRegInfo.setCardNumber("4929000000006");
		userRegInfo.setCardExpirationMonth(1);
		userRegInfo.setCardExpirationYear(2012);
		userRegInfo.setCardStartMonth(1);
		userRegInfo.setCardStartYear(11);
		userRegInfo.setCardType(UserRegInfo.CardType.VISA);
		userRegInfo.setCardBillingPostCode("412");
		userRegInfo.setCardBillingAddress("88");
		userRegInfo.setCardBillingCity("London");
		userRegInfo.setCardBillingCountry("GB");
		userRegInfo.setCardCv2("123");
		userRegInfo.setCardIssueNumber("");
		userRegInfo.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);

		Payment payment1 = service.makeDeferredPayment(
				userRegInfo, new BigDecimal(5.0), (byte)4, "GBP", "deferred", "DFRD" + getBigRandomInt());
		User user = new User();
		user.setId(1);
		user.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
		service.release(user, payment1, new BigDecimal(5.0),(byte)4, "GBP", "release", payment1.getInternalTxCode());
		for (int i = 0; i < 2; i++) {
			service.repeat(user, payment1, new BigDecimal(5.0),(byte)4,  "GBP", "repeat", "REPT" + getBigRandomInt());
		}
	}
	
}