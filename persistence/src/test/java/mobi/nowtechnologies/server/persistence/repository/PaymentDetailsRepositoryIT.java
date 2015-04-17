package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.rules.*;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.*;

import static org.hamcrest.CoreMatchers.is;

public class PaymentDetailsRepositoryIT extends AbstractRepositoryIT {

    @Rule
    public ExpectedException exception = none();
    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;
    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;
    @Resource(name = "userRepository")
    private UserRepository userRepository;

    private PayPalPaymentDetails getPaymentDetails(String billingAgreement) {
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
        paymentDetails.setBillingAgreementTxId(billingAgreement);
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        paymentDetails.withMadeAttempts(0);
        paymentDetails.setRetriesOnError(3);
        paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
        paymentDetails.setActivated(false);
        return paymentDetails;
    }

    @Test
    public void savePaymentDetailsWithChangesToUser() {

        User user = createUser();
        userRepository.save(user);

        user.setCity("Lugansk");
        PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
        paymentDetails.setOwner(user);

        paymentDetailsRepository.save(paymentDetails);


        assertNotNull(paymentDetails.getI());
        assertEquals("Lugansk", user.getCity());
    }

    private User createUser() {
        User user = new User();
        user.setUserName("hello@user.com");
        user.setCity("Kiev");
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        return user;
    }

    /**
     * Adding new payment details to user should disable old one and add a new one with activated equals to true
     */
    @Test
    @Transactional
    public void addingNewPaymentDetailsAndToserWithExistingPaymentDetails() {
        User user = createUser();
        userRepository.save(user);

        PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
        paymentDetails.setActivated(true);
        paymentDetails.setOwner(user);
        paymentDetailsRepository.save(paymentDetails);

        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.save(user);

        assertEquals(1, user.getPaymentDetailsList().size());

        PayPalPaymentDetails newPaymentDetails = getPaymentDetails("1111-2345-2345-23452-2345");
        newPaymentDetails.setActivated(true);
        user.getCurrentPaymentDetails().setActivated(false);
        newPaymentDetails.setOwner(user);
        newPaymentDetails = (PayPalPaymentDetails) paymentDetailsRepository.save(newPaymentDetails);

        assertEquals(2, user.getPaymentDetailsList().size());
        for (PaymentDetails pd : user.getPaymentDetailsList()) {
            if ("2345-2345-2345-23452-2345".equals(((PayPalPaymentDetails) pd).getBillingAgreementTxId())) {
                assertEquals(false, pd.isActivated());
            } else {
                assertEquals(true, pd.isActivated());
            }
        }
    }

    @Test
    public void shouldFindFailurePaymentPaymentDetailsWithNoNotification() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails1 = new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null);
        paymentDetails1.setLastPaymentStatus(ERROR);
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetails1);

        //when
        List<PaymentDetails> paymentDetailsList =
            paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(1));
        assertThat(paymentDetailsList.get(0).getI(), is(paymentDetails.getI()));
    }

    @Test
    public void shouldNotFindFailurePaymentDetailsWithStatusNone() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails1 = new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null);
        paymentDetails1.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetails1);

        //when
        List<PaymentDetails> paymentDetailsList =
            paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotification() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(2).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList =
            paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecauseTheyDoesNotExist() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails = paymentDetailsRepository
            .save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(Long.MAX_VALUE));

        //when
        List<PaymentDetails> paymentDetailsList =
            paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecauseWrongCommunity() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(false).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(null, new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }

    @Test
    public void shouldNotFindFailurePaymentPaymentDetailsWithNoNotificationBecausePaymentDetailsIsActive() {
        //given
        UserGroup o2UserGroup = userGroupRepository.findByCommunityRewriteUrl("o2");
        User user = userRepository.save(UserFactory.createUser(ActivationStatus.ACTIVATED).withUserGroup(o2UserGroup));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(new O2PSMSPaymentDetails().withOwner(user).withActivated(true).withRetriesOnError(1).withMadeRetries(1).withLastFailedPaymentNotificationMillis(null));

        //when
        List<PaymentDetails> paymentDetailsList =
            paymentDetailsRepository.findFailedPaymentWithNoNotificationPaymentDetails(o2UserGroup.getCommunity().getRewriteUrlParameter(), new PageRequest(0, Integer.MAX_VALUE));

        //then
        assertNotNull(paymentDetailsList);
        assertThat(paymentDetailsList.size(), is(0));
    }


    @Test
    public void savePaymentDetailsWithOwnerInInvalidStatus() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Unexpected activation status [ENTERED_NUMBER]. Payment details' owner should be in ACTIVATED activation status");
        User user = createUser();
        user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
        userRepository.save(user);

        user.setCity("Lugansk");
        PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
        paymentDetails.setOwner(user);

        paymentDetailsRepository.save(paymentDetails);
    }

}