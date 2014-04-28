package mobi.nowtechnologies.server.persistence.repository.social;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by oar on 4/28/2014.
 */
public  interface BaseSocialRepository<T extends SocialInfo> extends JpaRepository<T, Long> {
    T findByEmail(String email);

    T findForUser(User user);

    int deleteForUser(User user);
}
