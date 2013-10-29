package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.dao.PaymentStatusDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.DeviceUserDataService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/service-test.xml", "classpath:META-INF/soap.xml",
		"classpath:META-INF/dao-test.xml", "/META-INF/shared.xml", "classpath:transport-servlet-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EntityControllerIT {

	@Resource(name = "transport.EntityController")
	EntityController entityController;

	@Resource(name = "service.UserService")
	UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
	DeviceUserDataService deviceUserDataService;

    public void givenO2ClientWhoHasSavedPhoneAndPin_whenACC_CHECK_thenActivationIs_ACTIVATED()throws Exception{
        //given
        String userName = "test@test.com";
        AccCheckController controller = prepareMockController();
        updateUserActivationStatus(userName, ActivationStatus.ACTIVATED);

        //when
        ModelAndView mav = controller.accountCheckForO2Client(
                null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", null, null);
        
        assertEquals("ACTIVATED", getActivation(mav));
    }

    @Test
    @Ignore
    public void givenO2ClientWhoHasNotSavedPhone_whenACC_CHECK_thenActivationIs_REGISTERED() throws Exception{
        //given
        String userName = "test@test.com";
        AccCheckController controller = prepareMockController();
        updateUserActivationStatus(userName, ActivationStatus.REGISTERED);

        //when
        ModelAndView mav = controller.accountCheckForO2Client(
                null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", null, "Now Music");

        //then
        assertEquals("REGISTERED", getActivation(mav));
    }

    @Test
    @Ignore
    public void givenO2ClientWhoHasSavedPhone_whenACC_CHECK_thenActivationIs_ENTERED_NUMBER()throws Exception{
        //given
        String userName = "test@test.com";
        AccCheckController controller = prepareMockController();
        updateUserActivationStatus(userName, ActivationStatus.ENTERED_NUMBER);

        //when
        ModelAndView mav = controller.accountCheckForO2Client(
                null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", null, "Now Music");

        //then
        assertEquals("ENTERED_NUMBER", getActivation(mav));
    }


    private String getActivation(ModelAndView mav) {
        AccountCheckDto accountCheckDTO = AccCheckController.getAccountCheckDtoFrom(mav);
        return accountCheckDTO.activation.toString();
    }

    private void updateUserActivationStatus(String userName, ActivationStatus status) {
        User user = userService.findByName(userName);
        user.setActivationStatus(status);
        userRepository.save(user);
    }

    @Test
    @Ignore
    public void verifyThatTwoDifferentXtifyTokensWhenReceivedWithTheSameUserAndCommunityAndDeviceWillUpdated()throws Exception{
    	AccCheckController controller = prepareMockController();
        controller.accountCheckWithXtifyToken(
                null, null, "Now Music", null, "test@test.com", null, null, null, "deviceUID", null, null, "1234", null);
        controller.accountCheckWithXtifyToken(
                null, null, "Now Music", null, "test@test.com", null, null, null, "deviceUID", null, null, "5678", null);

        verify(controller);
        DeviceUserData data = deviceUserDataService.getByXtifyToken("5678");
        assertNotNull(data);
        assertEquals("deviceUID", data.getDeviceUid());

        data = deviceUserDataService.getByXtifyToken("1234");
        assertNull(data);
    }

    @Test
    @Ignore
    public void verifyThatXtifyTokenWillNotDuplicateWithTheSameUserAndCommunityUrl() throws Exception  {
    	AccCheckController controller = prepareMockController();
        controller.accountCheckWithXtifyToken(null,
                null, "Now Music", null, "test@test.com", null, null, null, "deviceUID", null, null, "1234", null);
        controller.accountCheckWithXtifyToken(null,
                null, "Now Music", null, "test@test.com", null, null, null, "deviceUID", null, null, "1234", null);

        verify(controller);
        DeviceUserData data = deviceUserDataService.getByXtifyToken("1234");
        assertNotNull(data);
        assertEquals("deviceUID", data.getDeviceUid());
    }

    @Test
    @Ignore
    public void verifyThatXtifyTokenCanBeSavedThroughRestApi() throws Exception {

    	AccCheckController controller = prepareMockController();
        controller.accountCheckWithXtifyToken(null,
                null, "Now Music", null, "test@test.com", null, null, null, "deviceUID", null, null, "1234", null);

        verify(controller);
        DeviceUserData data = deviceUserDataService.getByXtifyToken("1234");
        assertNotNull(data);
        assertEquals("deviceUID", data.getDeviceUid());
    }

	@Test
	@Ignore
	public void testRegisterUser_ValidParamsCardWithoutIssueNumberTag() throws Exception {
		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>"
				+ "<appVersion>CNBETA</appVersion>" + "<apiVersion>V1.2</apiVersion>" + "<deviceType>BLACKBERRY</deviceType>"
				+ "<countryFullName>Great Britain</countryFullName>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>"
				+ "<email>q@q.com</email>" + "<displayName>33</displayName>" + "<postCode>null</postCode>" + "<paymentType>creditCard</paymentType>"
				+ "<storedToken>493c467551c6da6c0e8538a6a55572e0</storedToken>" + "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>" + "<cardCv2>123</cardCv2>"
				+ "<cardHolderFirstName>John</cardHolderFirstName>" + "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>" + "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>" + "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>VISA</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

	}

	@Test
    @Ignore
	public void testRegisterUser_ValidParamsCardIssueNumberTagIsEmpty() {
		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>"
				+ "<appVersion>CNBETA</appVersion>" + "<apiVersion>V1.2</apiVersion>" + "<deviceType>BLACKBERRY</deviceType>"
				+ "<deviceString>Device 1</deviceString>" + "<countryFullName>Great Britain</countryFullName>" + "<phoneNumber>00447580381128</phoneNumber>"
				+ "<operator>1</operator>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>" + "<email>i@i.com</email>"
				+ "<communityName>Metal Hammer</communityName>" + "<displayName>33</displayName>" + "<postCode>null</postCode>" + "<paymentType>"
				+ UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>" + "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>"
				+ "<cardBillingAddress>88</cardBillingAddress>" + "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>"
				+ "<cardCv2>123</cardCv2>" + "<cardIssueNumber></cardIssueNumber>" + "<cardHolderFirstName>John</cardHolderFirstName>"
				+ "<cardHolderLastName>Smith</cardHolderLastName>" + "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>"
				+ "<cardStartYear>2011</cardStartYear>" + "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>"
				+ "<cardNumber>4929000000006</cardNumber>" + "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

	}

    private AccCheckController prepareMockController() throws NoSuchMethodException {
    	AccCheckController controller = createMock(AccCheckController.class,
        		AccCheckController.class.getMethod("accountCheck", HttpServletRequest.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class,
                        String.class));
        controller.setDeviceUserDataService(deviceUserDataService);
        controller.setUserService(userService);
        replay(controller);
        return controller;
    }

}