package mobi.nowtechnologies.server.persistence.repository;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FBUserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by oar on 2/10/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FBUserInfoRepositoryIT {
    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource
    private FBUserInfoRepository fbUserInfoRepository;

    @Resource(name = "persistence.DataSource")
    private DataSource dataSource;

    @Test
    public void testMapping(){
        FBUserInfo fbUserInfo = new FBUserInfo();
        fbUserInfo.setUser(findUser());
        fbUserInfo.setFirstName("AA");
        fbUserInfo.setFacebookId("ID");
        fbUserInfo.setUserName("userName");
        fbUserInfo.setEmail("AA@ukr.net");
        fbUserInfoRepository.save(fbUserInfo);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        assertEquals(1, template.queryForInt("select count(*) from tb_abstractSocialInfo"));
        assertEquals(1, template.queryForInt("select count(*) from tb_fbUserInfo"));
    }

    private User findUser() {
        String phoneNumber = "+64279000456";
        List<User> list = userRepository.findByMobile(phoneNumber);
        return Iterables.getFirst(list, null);
    }

}