package mobi.nowtechnologies.server.service.pincode;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Anton Zemliankin
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager")
public class PinCodeSmsServiceIT {

    @Resource
    PinCodeSmsService pinCodeService;

    @Resource
    Map<String, SMSGatewayService> smsProviders;

    SmsResponseData lastSmsMessage;

    boolean inited;

    @Test
    public void testSmsService() throws Exception {
        User user = new User();
        UserGroup userGroup = new UserGroup();
        Community community = new Community();
        community.setRewriteUrlParameter("vf_nz");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);

        pinCodeService.sendPinCode(user, "+4490284872", "1234");

        assertEquals("vf_nz", lastSmsMessage.getProvider());
        assertEquals("+4490284872", lastSmsMessage.getNumbers());
        assertTrue(lastSmsMessage.getMessage().contains("1234"));
    }

    @Before
    public void initMockGateways() {
        if (!inited) {
            smsProviders.clear();
            smsProviders.put("vf_nz", getMockSmsGateway("vf_nz"));
            smsProviders.put("DEFAULT", getMockSmsGateway("DEFAULT"));
            inited = true;
        }
    }

    @After
    public void tearDown() {
        lastSmsMessage = null;
    }

    private SMSGatewayService getMockSmsGateway(final String providerName) {
        return new SMSGatewayService() {
            @Override
            public SMSResponse send(String numbers, String message, String title) {
                lastSmsMessage = new SmsResponseData(message, message, numbers, providerName);
                return new SMSResponse() {
                    @Override
                    public String getMessage() {
                        return null;
                    }

                    @Override
                    public boolean isSuccessful() {
                        return true;
                    }
                };
            }
        };
    }

    private class SmsResponseData {
        private String message;
        private String title;
        private String numbers;
        private String provider;

        public SmsResponseData(String message, String title, String numbers, String provider) {
            this.message = message;
            this.title = title;
            this.numbers = numbers;
            this.provider = provider;
        }

        public String getMessage() {
            return message;
        }

        public String getTitle() {
            return title;
        }

        public String getNumbers() {
            return numbers;
        }

        public String getProvider() {
            return provider;
        }
    }

}
