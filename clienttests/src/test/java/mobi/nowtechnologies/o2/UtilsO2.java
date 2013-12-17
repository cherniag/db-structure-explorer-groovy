package mobi.nowtechnologies.o2;

import java.io.File;
import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserLog;
import mobi.nowtechnologies.server.persistence.domain.enums.UserLogType;
import mobi.nowtechnologies.server.persistence.repository.UserLogRepository;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2ServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2TariffServiceImpl;
import mobi.nowtechnologies.server.service.o2.impl.O2WebServiceMessageHandler;
import mobi.nowtechnologies.server.service.o2.impl.WebServiceGateway;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;


/**
 *
 * Nothing fancy - just creates the objects we are using to access O2 services
 *
 */
public class UtilsO2 {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtilsO2.class);

	public static O2ServiceImpl createO2ServiceImpl(O2Config config) throws Exception {

		O2ServiceImpl res = new O2ServiceImpl();
		res.setO2TariffService(createO2TariffImpl(config));
		return res;
	}

	public static O2TariffServiceImpl createO2TariffImpl(O2Config config) throws Exception {
		O2TariffServiceImpl impl = new O2TariffServiceImpl();

		WebServiceGateway webServiceGateway = createWSGateway(O2Config.QA, getJaxBPackagesForTariff());

		impl.setWebServiceGateway(webServiceGateway);
		impl.setManagePostpayTariffEndpoint(config.getServerUrl() + "ManagePostpayTariff_2_0");
		impl.setManagePrepayTariffEndpoint(config.getServerUrl() + "ManagePrepayTariff_2_0");
		impl.setManagePostpayBoltonEndpoint(config.getServerUrl() + "ManagePostpayBoltons_2_0");
		impl.setSubscriberEndpoint(config.getServerUrl() + "Subscriber_2_0");
		impl.setManageOrderEndpoint(config.getServerUrl() + "ManageOrder_2_0");

		return impl;
	}

	public static String[] getJaxBPackagesForTariff() {
		return new String[] { "uk.co.o2.soa.managepostpaytariffdata_2", "uk.co.o2.soa.manageprepaytariffdata_2",
				"uk.co.o2.soa.managepostpayboltonsdata_2", "uk.co.o2.soa.manageorderdata_2",
				"uk.co.o2.soa.subscriberdata_2" };

	}

	public static O2ProviderServiceImpl createO2ClientService(O2Config config) throws Exception {

		LOGGER.info("o2ProxyUrl: " + config.getProxyUrl() + " ServerUrl=" + config.getServerUrl());
		//ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "db2mine-application.xml" });

		//      SessionFactory sessionFactory = context.getBean(SessionFactory.class);
		//
		//      HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
		//
		//      GenericService genericService = context.getBean(GenericService.class);

		WebServiceGateway webServiceGateway = createWSGateway(config, "uk.co.o2.soa.chargecustomerdata",
				"uk.co.o2.soa.subscriberdata");

		O2ProviderServiceImpl impl = new O2ProviderServiceImpl();
		impl.setWebServiceGateway(webServiceGateway);

		impl.setServerO2Url(config.getProxyUrl());
		impl.setPromotedServerO2Url(config.getProxyUrl());
		impl.setRedeemPromotedServerO2Url(config.getProxyUrl());

		impl.setRestTemplate(new RestTemplate());

		impl.setLimitValidatePhoneNumber(1000);

		impl.setChargeCustomerEndpoint(config.getServerUrl() + "ChargeCustomer_1_0");
		impl.setSubscriberEndpoint(config.getServerUrl() + "Subscriber_2_0");
		impl.setSendMessageEndpoint(config.getServerUrl() + "SendMessage_1_1");

		impl.setDeviceService(getDeviceService());
		impl.setCommunityService(getCommunityService());
		impl.setUserLogRepository(getUserLogRepository());
		
		impl.setUserService(getUserService());
		impl.setO2Service( null );
		
		return impl;
	}
	
	public static WebServiceGateway createWSGateway(O2Config o2Config,String... jaxbPackagees) throws Exception {
		WebServiceGateway webServiceGateway = new WebServiceGateway();

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths(jaxbPackagees);

		webServiceGateway.setMarshaller(marshaller);
		webServiceGateway.setUnmarshaller(marshaller);

		O2WebServiceMessageHandler webServiceMessageHandler = new O2WebServiceMessageHandler();
		webServiceMessageHandler.setSoaConsumerTransactionID("0000111122223333:musicqubed.test");
		webServiceMessageHandler.setUsername("musicQubed_1001");

		webServiceMessageHandler.setPassword(o2Config.getTokenPassword());
		webServiceGateway.setDefaultWebServiceMessageHandler(webServiceMessageHandler);

		File keyStoreFile = new File(o2Config.getKeystoreFile());//resource.toURI());
		Assert.assertTrue("Keystore file not found " + keyStoreFile.getAbsolutePath(), keyStoreFile.exists());

		webServiceGateway.setKeystoreLocation(new FileSystemResource(keyStoreFile));
		webServiceGateway.setKeystorePassword(o2Config.getKeystorePassword());

		SaajSoapMessageFactory factory = createMessageFactory();
		webServiceGateway.setMessageFactory(factory);
		return webServiceGateway;
	}

	private static SaajSoapMessageFactory MESSAGE_FACTORY_INSTANCE = null;

	private static SaajSoapMessageFactory createMessageFactory() throws Exception {
		if (MESSAGE_FACTORY_INSTANCE == null) {
			SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory(
					javax.xml.soap.MessageFactory.newInstance());
			messageFactory.setSoapVersion(SoapVersion.SOAP_11);
			messageFactory.createWebServiceMessage();

			MESSAGE_FACTORY_INSTANCE = messageFactory;
		}

		return MESSAGE_FACTORY_INSTANCE;
	}

	private static DeviceService getDeviceService() {
		return new DeviceService() {
			@Override
			public boolean isPromotedDevicePhone(Community community, String phoneNumber, String promoCode) {
				return false;
			}

		};
	}

	private static CommunityService getCommunityService() {
		return new CommunityService() {

			public List<Community> list() {
				throw new RuntimeException();

			}

			public Community getCommunityByUrl(String communityUrl) {
				throw new RuntimeException(communityUrl);
			}

			public Community getCommunityByName(String communityName) {

				return null;
			}
		};
	}

	private static UserService getUserService() {
		return new UserService(){
			public boolean isPromotedDevice(String phoneNumber, Community community) {
				return false;
			}
		};
	}
	
	private static UserLogRepository getUserLogRepository() {
		return new UserLogRepository() {

			public UserLog findOne(Integer id) {
				return null;
			}

			public Iterable<UserLog> findAll(Iterable<Integer> ids) {
				return null;
			}

			public boolean exists(Integer id) {
				return false;
			}

			public void deleteAll() {
			}

			public void delete(Iterable<? extends UserLog> entities) {
			}

			public void delete(UserLog entity) {
			}

			public void delete(Integer id) {
			}

			public long count() {
				return 0;
			}

			public Page<UserLog> findAll(Pageable pageable) {
				return null;
			}

			public UserLog saveAndFlush(UserLog entity) {
				return null;
			}

			public void flush() {
			}

			public List<UserLog> findAll(Sort sort) {
				return null;
			}

			public List<UserLog> findAll() {
				return null;
			}

			public void deleteInBatch(Iterable<UserLog> entities) {
			}

			public void deleteAllInBatch() {
			}

			public <S extends UserLog> S save(S entity) {

				return null;
			}

			@Query("select userLog from UserLog userLog  where userLog.user.id = ?1 and userLog.type = ?2 group by userLog.user  having min(userLog.last_update) = userLog.last_update")
			public UserLog findByUser(int id, UserLogType userLogType) {

				return null;
			}

			@Query("select userLog from UserLog userLog  where userLog.phoneNumber = ?1 and userLog.type = ?2 group by userLog.phoneNumber  having min(userLog.last_update) = userLog.last_update")
			public UserLog findByPhoneNumber(String phoneNumber, UserLogType userLogType) {

				return null;
			}

			@Query("select count(userLog) from UserLog userLog  where userLog.phoneNumber = ?1 and abs(userLog.last_update/86400000 - ?3) < 1 and userLog.type = ?2")
			public Long countByPhoneNumberAndDay(String phoneNumber, UserLogType userLogType, long dayOfDate) {
				return new Long(0);
			}

			public <S extends UserLog> List<S> save(Iterable<S> entities) {

				return null;
			}

		};

	}
}