package mobi.nowtechnologies.server.service;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ActivationEmailServiceTest {

    public static final String EMAIL = "a@gmail.com";

    private ActivationEmailService activationEmailService;

    @Mock
    private ActivationEmailRepository activationEmailRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommunityResourceBundleMessageSourceImpl messageSource;

    @Mock
    private MailService mailService;

    @Test
    public void testActivate() {
        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setEmail(EMAIL);
        activationEmail.setDeviceUID("htc");
        String token = ActivationEmail.generateToken(EMAIL, "htc");
        activationEmail.setToken(token);
        when(activationEmailRepository.findOne(1l)).thenReturn(activationEmail);

        activationEmailService.activate(1l, EMAIL, token);

        verify(activationEmailRepository).save(argThat(new ArgumentMatcher<ActivationEmail>() {
            @Override
            public boolean matches(Object argument) {
                return argument != null && ((ActivationEmail) argument).isActivated();
            }
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateEmailWringToken() {
        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setEmail(EMAIL);
        activationEmail.setDeviceUID("htc");
        activationEmail.setToken("ttt");
        String token = ActivationEmail.generateToken(EMAIL, "htc");
        when(activationEmailRepository.findOne(1l)).thenReturn(activationEmail);

        activationEmailService.activate(1l, EMAIL, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateEmailActivated() {
        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setEmail(EMAIL);
        activationEmail.setDeviceUID("htc");
        activationEmail.setActivated(true);
        String token = ActivationEmail.generateToken(EMAIL, "htc");
        activationEmail.setToken(token);
        when(activationEmailRepository.findOne(1l)).thenReturn(activationEmail);

        activationEmailService.activate(1l, EMAIL, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateEmailWrongEmail() {
        ActivationEmail activationEmail = new ActivationEmail();
        activationEmail.setEmail("ttt@gmail.com");
        activationEmail.setDeviceUID("htc");
        String token = ActivationEmail.generateToken(EMAIL, "htc");
        activationEmail.setToken(token);
        when(activationEmailRepository.findOne(1l)).thenReturn(activationEmail);

        activationEmailService.activate(1l, EMAIL, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testActivateEmailWrongId() {
        when(activationEmailRepository.findOne(1l)).thenReturn(null);

        String token = ActivationEmail.generateToken(EMAIL, "htc");

        activationEmailService.activate(1l, EMAIL, token);
    }

    @Test
    public void testSendEmail() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        when(userService.findByNameAndCommunity("htc", Community.O2_COMMUNITY_REWRITE_URL)).thenReturn(user);
        when(activationEmailRepository.save(any(ActivationEmail.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ActivationEmail activationEmail = (ActivationEmail) invocation.getArguments()[0];
                activationEmail.setId(1l);
                return activationEmail;
            }
        });

        when(messageSource.getMessage(eq(Community.O2_COMMUNITY_REWRITE_URL), anyString(),
                isNull(Object[].class), isNull(Locale.class))).thenCallRealMethod();

        activationEmailService.sendEmail(EMAIL, "htc", "htc", Community.O2_COMMUNITY_REWRITE_URL);

        verify(mailService).sendMail(anyString(), any(String[].class), anyString(), anyString(), anyMap());
    }

    @Test(expected = ValidationException.class)
    public void testSendEmailNotValid() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        when(userService.findByNameAndCommunity("htc", Community.O2_COMMUNITY_REWRITE_URL)).thenReturn(user);
        when(activationEmailRepository.save(any(ActivationEmail.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ActivationEmail activationEmail = (ActivationEmail) invocation.getArguments()[0];
                activationEmail.setId(1l);
                return activationEmail;
            }
        });

        when(messageSource.getMessage(eq(Community.O2_COMMUNITY_REWRITE_URL), anyString(),
                isNull(Object[].class), isNull(Locale.class))).thenCallRealMethod();

        activationEmailService.sendEmail("ttt", "htc", "htc", Community.O2_COMMUNITY_REWRITE_URL);

        verify(mailService).sendMail(anyString(), any(String[].class), anyString(), anyString(), anyMap());
    }

    @Before
    public void setUp() {
        activationEmailService = new ActivationEmailServiceImpl();
        ReflectionTestUtils.setField(activationEmailService, "activationEmailRepository", activationEmailRepository);
        ReflectionTestUtils.setField(activationEmailService, "userService", userService);
        ReflectionTestUtils.setField(activationEmailService, "messageSource", messageSource);
        ReflectionTestUtils.setField(activationEmailService, "mailService", mailService);
    }
}
