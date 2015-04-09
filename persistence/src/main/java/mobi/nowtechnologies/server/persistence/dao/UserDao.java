package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.util.List;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * UserDao
 *
 * @author Maksym Chernolevskyi (maksym)
 * @deprecated should be replaced on {@link mobi.nowtechnologies.server.persistence.repository.UserRepository}
 */
@Deprecated
public class UserDao extends JpaDaoSupport {

    public Promotion getActivePromotion(UserGroup userGroup) {
        List<?> list = getJpaTemplate().find("select o from " + Promotion.class.getSimpleName() +
                                             " o where (o.numUsers < o.maxUsers or o.maxUsers=0) and o.startDate < ?1 " +
                                             "and o.endDate > ?1 and o.isActive = 1 and o.userGroup = ?2 and o.type = ?3", getEpochSeconds(), userGroup, Promotion.ADD_SUBBALANCE_PROMOTION);
        return list == null || list.size() == 0 ?
               null :
               (Promotion) list.get(0);
    }

}
