package mobi.nowtechnologies.server.persistence.repository;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentDetailsRepositoryIT {
	
	@Resource(name = "paymentDetailsRepository")
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    @Resource(name = "userRepository")
    private UserRepository userRepository;
		
	private PayPalPaymentDetails getPaymentDetails(String billingAgreement) {
		PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
		paymentDetails.setBillingAgreementTxId(billingAgreement); 
		paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
		paymentDetails.setMadeRetries(0);
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
		paymentDetails.setActivated(false);
		return paymentDetails;
	}
	
	@Test
	public void savePaymentDetailsWithChangesToUser() {
		
		User user = new User();
			user.setUserName("hello@user.com");
			user.setCity("Kiev");
		entityDao.saveEntity(user);
		
		user.setCity("Lugansk");
		PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
		paymentDetails.setOwner(user);
		
		paymentDetailsRepository.save(paymentDetails);
		
		
		assertNotNull(paymentDetails.getI());
		assertEquals("Lugansk", user.getCity());
	}
	
	/**
	 * Adding new payment details to user should disable old one and add a new one with activated equals to true
	 */
	@Test
	@Transactional
	public void addingNewPaymentDetailsAndToserWithExistingPaymentDetails() {
		User user = new User();
		user.setUserName("hello@user.com");
		user.setCity("Kiev");
			entityDao.saveEntity(user);
			
		PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
			paymentDetails.setActivated(true);
			paymentDetails.setOwner(user);
			paymentDetailsRepository.save(paymentDetails);
			
		user.setCurrentPaymentDetails(paymentDetails);
			entityDao.saveEntity(user);
		
		assertEquals(1, user.getPaymentDetailsList().size());
			
		PayPalPaymentDetails newPaymentDetails = getPaymentDetails("1111-2345-2345-23452-2345");
		newPaymentDetails.setActivated(true);
		user.getCurrentPaymentDetails().setActivated(false);
		newPaymentDetails.setOwner(user);
		newPaymentDetails = (PayPalPaymentDetails) paymentDetailsRepository.save(newPaymentDetails);
			
		assertEquals(2, user.getPaymentDetailsList().size());
		for (PaymentDetails pd : user.getPaymentDetailsList()) {
			if ("2345-2345-2345-23452-2345".equals(((PayPalPaymentDetails)pd).getBillingAgreementTxId())) {
				assertEquals(false, pd.isActivated());
			} else {
				assertEquals(true, pd.isActivated());
			}
		}
	}

    @Test
    public void shouldFindFailurePaymentPaymentDetailsWithNoNotification(){
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser().withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(1));
        assertThat(paymentDetailsList.get(0).getI(), is(paymentDetails.getI()));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotification(){
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser().withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(2).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecauseTheyDoesNotExist(){
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser().withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(Long.MAX_VALUE));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecauseWrongCommunity(){
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser().withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(null, new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecausePaymentDetailsIsActive(){
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser().withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(true).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }
}