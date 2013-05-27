package mobi.nowtechnologies.server.transport.controller;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDTO;
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
                null, null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", null);
        
        assertEquals("ACTIVATED", getActivation(mav));
    }

    @Test
    public void givenO2ClientWhoHasNotSavedPhone_whenACC_CHECK_thenActivationIs_REGISTERED() throws Exception{
        //given
        String userName = "test@test.com";
        AccCheckController controller = prepareMockController();
        updateUserActivationStatus(userName, ActivationStatus.REGISTERED);

        //when
        ModelAndView mav = controller.accountCheckForO2Client(
                null, null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", "Now Music");

        //then
        assertEquals("REGISTERED", getActivation(mav));
    }

    @Test
    public void givenO2ClientWhoHasSavedPhone_whenACC_CHECK_thenActivationIs_ENTERED_NUMBER()throws Exception{
        //given
        String userName = "test@test.com";
        AccCheckController controller = prepareMockController();
        updateUserActivationStatus(userName, ActivationStatus.ENTERED_NUMBER);

        //when
        ModelAndView mav = controller.accountCheckForO2Client(
                null, null, "Now Music", null, userName, null, null, null, "deviceUID", null, null, "1234", "Now Music", "Now Music");

        //then
        assertEquals("ENTERED_NUMBER", getActivation(mav));
    }


    private String getActivation(ModelAndView mav) {
        AccountCheckDTO accountCheckDTO = AccCheckController.getAccountCheckDtoFrom(mav);
        return accountCheckDTO.getActivation().toString();
    }

    private void updateUserActivationStatus(String userName, ActivationStatus status) {
        User user = userService.findByName(userName);
        user.setActivationStatus(status);
        userRepository.save(user);
    }

    @Test
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
    @Transactional
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
	@Rollback(value = false)
    @Ignore
	public void testRegisterUserCheckPinAccountCheckSuccess() {
		String timestamp = "1";
		String userToken = "1a4d0298535c54cbab054eccaca4c793";
		String userName = "zzz@z.com";
		String apiVersion = "V1.2";
		String communityName = "Metal Hammer";
		String appVersion = "CNBETA";
		String deviceType = "ANDROID";
		String displayName = "Nigel";
		String deviceString = "Device 1";
		String phoneNumber = "00447580381128";
		String operator = "1";

		String aBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<userRegInfo>" + "<address>33333</address>" + "<appVersion>"
				+ appVersion + "</appVersion>" + "<apiVersion>" + apiVersion + "</apiVersion>" + "<deviceType>" + deviceType + "</deviceType>"
				+ "<deviceString>" + deviceString + "</deviceString>" + "<countryFullName>Great Britain</countryFullName>" + "<phoneNumber>" + phoneNumber
				+ "</phoneNumber>" + "<operator>" + operator + "</operator>" + "<city>33</city>" + "<firstName>33</firstName>" + "<lastName>33</lastName>"
				+ "<email>" + userName + "</email>" + "<communityName>" + communityName + "</communityName>" + "<displayName>" + displayName + "</displayName>"
				+ "<postCode>null</postCode>" + "<paymentType>" + UserRegInfo.PaymentType.PREMIUM_USER + "</paymentType>"
				+ "<storedToken>51c7bb77ae9859e18118b014188f34b1</storedToken>" + "<cardBillingAddress>88</cardBillingAddress>"
				+ "<cardBillingCity>London</cardBillingCity>" + "<cardBillingCountry>GB</cardBillingCountry>" + "<cardCv2>123</cardCv2>"
				+ "<cardIssueNumber></cardIssueNumber>" + "<cardHolderFirstName>John</cardHolderFirstName>" + "<cardHolderLastName>Smith</cardHolderLastName>"
				+ "<cardBillingPostCode>412</cardBillingPostCode>" + "<cardStartMonth>1</cardStartMonth>" + "<cardStartYear>2011</cardStartYear>"
				+ "<cardExpirationMonth>1</cardExpirationMonth>" + "<cardExpirationYear>2012</cardExpirationYear>" + "<cardNumber>4929000000006</cardNumber>"
				+ "<cardType>" + UserRegInfo.CardType.VISA + "</cardType>" + "</userRegInfo>";
		MockHttpServletResponse aHttpServletResponse = new MockHttpServletResponse();
		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
		httpServletRequest.setRemoteAddr("2.24.0.1");

		entityController.registerUser(aBody, aHttpServletResponse, httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

		aHttpServletResponse = new MockHttpServletResponse();
		httpServletRequest = new MockHttpServletRequest();
		User user = userService.findByName(userName);
		entityController.checkPin(appVersion, communityName, apiVersion, userName, userToken, timestamp, user.getPin(), aHttpServletResponse,
				httpServletRequest);
		assertEquals(aHttpServletResponse.getStatus(), 200);

		ModelAndView modelAndView = null;
		//ModelAndView modelAndView = entityController.accountCheck(null, appVersion, communityName, apiVersion, userName, userToken, timestamp, null, null, null, null, null);
		assertNotNull(modelAndView);
		Map<String, Object> modelMap = modelAndView.getModel();
		assertNotNull(modelMap);
		assertTrue(modelMap.entrySet().size() == 1);

		Object object = modelMap.get(Response.class.toString());
		assertTrue(object instanceof Response);
		Response response = (Response) object;
		assertNotNull(response);
		Object[] objects = response.getObject();
		assertTrue(objects.length == 1);

		assertTrue(objects[0] instanceof AccountCheckDTO);
		AccountCheckDTO receivedAccountCheck = (AccountCheckDTO) objects[0];

		AccountCheckDTO accountCheck = new AccountCheckDTO();
		accountCheck.setChartItems((byte) 25);
		accountCheck.setChartTimestamp(1313172060);
		accountCheck.setDeviceType(deviceType);
		accountCheck.setDeviceUID(deviceString);
		accountCheck.setDisplayName(displayName);
		accountCheck.setDrmType(null);
		accountCheck.setDrmValue((byte) 0);
		accountCheck.setNewsTimestamp(1312560155);
		accountCheck.setNewsItems((byte) 10);
		accountCheck.setStatus(UserStatus.EULA.toString());
		accountCheck.setSubBalance((byte) 0);
		accountCheck.setPhoneNumber(phoneNumber);
		accountCheck.setOperator(Integer.valueOf(operator));
		// accountCheck.setPaymentStatus("AWAITING_PSMS");

		assertEquals(accountCheck.getChartItems(), receivedAccountCheck.getChartItems());
		assertEquals(accountCheck.getChartTimestamp(), receivedAccountCheck.getChartTimestamp());
		assertEquals(accountCheck.getDeviceType(), receivedAccountCheck.getDeviceType());
		assertEquals(accountCheck.getDeviceUID(), receivedAccountCheck.getDeviceUID());
		assertEquals(accountCheck.getDisplayName(), receivedAccountCheck.getDisplayName());
		assertEquals(accountCheck.getDrmType(), receivedAccountCheck.getDrmType());
		assertEquals(accountCheck.getDrmValue(), receivedAccountCheck.getDrmValue());
		assertEquals(accountCheck.getNewsTimestamp(), receivedAccountCheck.getNewsTimestamp());
		assertEquals(accountCheck.getNewsItems(), receivedAccountCheck.getNewsItems());
		assertEquals(accountCheck.getStatus(), receivedAccountCheck.getStatus());
		assertEquals(accountCheck.getSubBalance(), receivedAccountCheck.getSubBalance());
		assertEquals(accountCheck.getPhoneNumber(), receivedAccountCheck.getPhoneNumber());
		assertEquals(accountCheck.getOperator(), receivedAccountCheck.getOperator());
		// assertEquals(accountCheck.getPaymentStatus(),receivedAccountCheck.getPaymentStatus());
		String receivedPaymentStatus = receivedAccountCheck.getPaymentStatus();
		assertTrue(receivedPaymentStatus.equals(PaymentStatusDao.getAWAITING_PSMS().getName())
				|| receivedPaymentStatus.equals(PaymentStatusDao.getOK().getName()));
	}

	@Test
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
//        expect(controller.accountCheck((HttpServletRequest) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject(),
//                (String) anyObject()
//        )).andReturn(modelAndViewWithAccountCheckDto()).anyTimes();
        replay(controller);
        return controller;
    }

    private ModelAndView modelAndViewWithAccountCheckDto() {
        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        Object[] objects = {accountCheckDTO};
        return new ModelAndView("view", Response.class.toString(), new Response(objects));
    }

}