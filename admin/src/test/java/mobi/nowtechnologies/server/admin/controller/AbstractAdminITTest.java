package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

// Created by oar on 2/3/14.
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({@ContextConfiguration("classpath:META-INF/admin-root-test.xml"), @ContextConfiguration("classpath:META-INF/admin-servlet-test.xml")})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@ActiveProfiles("TEST")
public abstract class AbstractAdminITTest {

    final Logger logger = LoggerFactory.getLogger(getClass());

    protected MockMvc mockMvc;

    @Resource
    private WebApplicationContext webApplicationContext;


    @Autowired
    private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;


    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    protected Cookie getCommunityCookie(String communityUrl) {
        Cookie cookie = new Cookie("_chartsnow_community", communityUrl);
        cookie.setMaxAge(365 * 24 * 60 * 60);
        return cookie;
    }

    protected HttpHeaders getHttpHeaders(boolean responseInJson) {
        HttpHeaders headers = new HttpHeaders();
        String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");
        headers.put(nowTechTokenBasedRememberMeServices.getKey(), Lists.newArrayList(rememberMeToken));
        if (responseInJson) {
            headers.put("Accept", Lists.newArrayList("application/json"));
        }
        return headers;
    }
}
