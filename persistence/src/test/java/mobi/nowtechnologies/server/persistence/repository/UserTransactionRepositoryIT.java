package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.device.domain.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.UserTransaction;
import mobi.nowtechnologies.server.persistence.domain.UserTransactionType;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.annotation.Resource;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;


public class UserTransactionRepositoryIT extends AbstractRepositoryIT {

    @Resource
    UserGroupRepository userGroupRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    UserTransactionRepository userTransactionRepository;
    @Resource
    UserStatusRepository userStatusRepository;

    @Test
    public void testSave() throws Exception {
        final String communityRewriteUrl = "mtv1";
        final String promoCode = "some.promo.code";
        final long start = System.currentTimeMillis();
        final long end = start + 2000L;

        User user = createUser(communityRewriteUrl);
        UserTransaction userTransaction = getUserTransaction(promoCode, start, end, user);

        userTransactionRepository.save(userTransaction);

        List<UserTransaction> userTransactions = userTransactionRepository.findByUser(user);
        assertEquals(1, userTransactions.size());
        assertEquals(user, userTransactions.get(0).getUser());
        assertEquals(promoCode, userTransactions.get(0).getPromoCode());
        assertEquals(start, userTransactions.get(0).getStartTimestamp());
        assertEquals(end, userTransactions.get(0).getEndTimestamp());
        assertEquals(UserTransactionType.PROMOTION_BY_PROMO_CODE, userTransactions.get(0).getTransactionType());
    }

    @Test
    public void testDeleteByUser() throws Exception {
        final String communityRewriteUrl = "mtv1";
        final String promoCode = "some.promo.code";
        final long start = System.currentTimeMillis();
        final long end = start + 2000L;

        User user = createUser(communityRewriteUrl);
        UserTransaction userTransaction = getUserTransaction(promoCode, start, end, user);
        userTransactionRepository.save(userTransaction);

        userTransactionRepository.deleteByUser(user);

        List<UserTransaction> userTransactions = userTransactionRepository.findByUser(user);
        assertEquals(0, userTransactions.size());
    }

    private UserTransaction getUserTransaction(String promoCode, long start, long end, User user) {
        UserTransaction userTransaction = new UserTransaction();
        userTransaction.setUser(user);
        userTransaction.setPromoCode(promoCode);
        userTransaction.setStartTimestamp(start);
        userTransaction.setEndTimestamp(end);
        userTransaction.setTransactionType(UserTransactionType.PROMOTION_BY_PROMO_CODE);
        return userTransaction;
    }


    private User createUser(String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(Utils.getRandomUUID());
        user.setUserName(Utils.getRandomUUID());
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name()));
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);
        return user;
    }
}