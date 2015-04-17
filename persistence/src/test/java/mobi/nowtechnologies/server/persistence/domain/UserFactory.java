package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentStatus;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class UserFactory {

    public static Collection<User> getUserCollection() {
        Collection<User> users = new ArrayList<User>(1);

        users.add(createUser(ACTIVATED));
        return users;
    }

    public static List<User> getUserUnmodifableList() {
        Collection<User> users = getUserCollection();

        return Collections.unmodifiableList(new ArrayList<User>(users));
    }


    public static User createUser(ActivationStatus status) {

        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 10);
        userStatus.setName(UserStatusType.SUBSCRIBED.name());

        PaymentStatus paymentStatus = new PaymentStatus();
        paymentStatus.setId(2);

        DeviceType deviceType = new DeviceType();
        deviceType.setName("IOS");
        deviceType.setI((byte) 5);

        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = new UserGroup();
        userGroup.setId(7);
        userGroup.setCommunity(community);

        User testUser = new User();
        testUser.setAddress1("678");
        testUser.setAddress2("");
        testUser.setCanContact(true);
        testUser.setCity("St.Albans");
        testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
        testUser.setCountry(1);
        testUser.setDevice("HTC HERO");
        testUser.setDeviceString("iPhone");
        testUser.setDeviceType(deviceType);
        testUser.setDisplayName("Nigel");
        testUser.setFirstName("Nigel");
        testUser.setIpAddress("217.35.32.182");
        testUser.setLastDeviceLogin(1306902146);
        testUser.setLastName("Rees");
        testUser.setLastPaymentTx(72);
        testUser.setLastWebLogin(1306873638);
        testUser.setMobile("+447770608575");
        testUser.setNextSubPayment(1307219588);
        testUser.setPostcode("412");
        testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
        testUser.setDeviceUID("attg0vs3e98dsddc2a4k9vdkc6");
        testUser.setStatus(userStatus);
        testUser.setSubBalance((byte) 5);
        testUser.setTempToken("NONE");
        testUser.setTitle("Mr");
        testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
        testUser.setUserGroup(userGroup);
        testUser.setUserName("test_getListOfUsersForUpdate@rbt.com");
        testUser.setUserType(UserType.NORMAL);
        testUser.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
        testUser.setPin("pin");
        testUser.setPaymentStatus(paymentStatus.getId());
        testUser.setProvider(O2);
        testUser.setContract(PAYG);
        testUser.setSegment(CONSUMER);
        testUser.setActivationStatus(status);
        return testUser;
    }

    public static User createUser(PaymentDetails currentPaymentDetails, BigDecimal amountOfMoneyToUserNotification) {
        User user = createUser(ACTIVATED);
        user.setCurrentPaymentDetails(currentPaymentDetails);
        user.setAmountOfMoneyToUserNotification(amountOfMoneyToUserNotification);

        return user;
    }

    public static User createUser(PaymentDetails currentPaymentDetails, BigDecimal amountOfMoneyToUserNotification, UserGroup userGroup) {
        User user = createUser(ACTIVATED);
        user.setCurrentPaymentDetails(currentPaymentDetails);
        user.setAmountOfMoneyToUserNotification(amountOfMoneyToUserNotification);
        user.setUserGroup(userGroup);

        return user;
    }

    public static User userWithDefaultNotNullFields() {
        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 11);
        userStatus.setName(UserStatusType.LIMITED.name());

        User user = new User();
        user.setDisplayName("");
        user.setTitle("");
        user.setFirstName("");
        user.setLastName("");
        user.setUserName("");
        user.setSubBalance((byte) 0);
        user.setToken("");
        user.setStatus(userStatus);
        user.setDeviceType(DeviceTypeCache.getAndroidDeviceType());
        user.setDevice("");
        user.setUserGroup(UserGroupFactory.createUserGroup());
        user.setUserType(UserType.DEV);
        user.setLastDeviceLogin(0);
        user.setLastWebLogin(0);
        user.setNextSubPayment(0);
        user.setLastPaymentTx(0);
        user.setAddress1("");
        user.setAddress2("");
        user.setCity("");
        user.setPostcode("");
        user.setCountry(1);
        user.setMobile("");
        user.setCode("");
        user.setSessionID("");
        user.setIpAddress("");
        user.setTempToken("");
        user.setDeviceString("");
        user.setCanContact(false);
        user.setOperator(1);
        user.setPin("");
        user.setPaymentStatus(1);
        user.setNumPsmsRetries(0);
        user.setAmountOfMoneyToUserNotification(BigDecimal.ONE);
        user.setLastSuccesfullPaymentSmsSendingTimestampMillis(Long.MAX_VALUE);
        user.setTariff(_3G);
        user.setVideoFreeTrialHasBeenActivated(false);
        return user;
    }

    public static User userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED() {
        return userWithDefaultNotNullFields().withSubBalance((byte) 0).withLastDeviceLogin(1).withActivationStatus(ACTIVATED);
    }
}
