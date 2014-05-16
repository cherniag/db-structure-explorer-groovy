package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Created by oar on 4/28/2014.
 */
@NoRepositoryBean
public  interface BaseSocialRepository<T extends SocialInfo> extends JpaRepository<T, Long> {
    T findByUser(User user);

    @Modifying
    @Query(value="delete  from #{#entityName} entity where entity.user=?1")
    int deleteByUser(User user);
}
