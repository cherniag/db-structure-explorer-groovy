package mobi.nowtechnologies.server.persistence.domain.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.NewsDetail;
import mobi.nowtechnologies.server.persistence.domain.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The class <code>UserStateFilterTest</code> contains tests for the class <code>{@link UserStateFilter}</code>.
 *
 * @generatedBy CodePro at 01.02.12 17:50
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DeviceTypeDao.class)
public class UserHandsetFilterTest {
	
	private static DeviceType iOS_DeviceType;
	private static DeviceType noneDeviceType;
	private static DeviceType androidDeviceType;
	private static DeviceType j2meDeviceType;
	private static DeviceType blackberryDeviceType;
	private static DeviceType symbianDeviceType;
	
	/**
	 * Run the UserHandsetFilter() constructor test.
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testUserHandsetFilter_Constructor_Success()
		throws Exception {
		UserHandsetFilter result = new UserHandsetFilter();
		assertNotNull(result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsANDROIDandUserHandsetIsANDROID_Success()
		throws Exception {
		
		UserHandsetFilter userHandsetFilter = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(androidDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.ANDROID);

		boolean result = userHandsetFilter.doFilter(user, newsDetail);

		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsNONEandUserHandsetIsANDROID_Failure()
		throws Exception {
		
		UserHandsetFilter userHandsetFilter = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(noneDeviceType);
		
		NewsDetail param = new NewsDetail();
		param.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.ANDROID);

		boolean result = userHandsetFilter.doFilter(user, param);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsIOSandUserHandsetIsANDROID_Failure()
		throws Exception {
		
		UserHandsetFilter userHandsetFilter = new UserHandsetFilter();
		
		User user = new User();
		user.setDeviceType(iOS_DeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.ANDROID);

		boolean result = userHandsetFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsBLACKBERRYandUserHandsetIsANDROID_Failure()
		throws Exception {
		UserHandsetFilter userHandsetFilter = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(blackberryDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.ANDROID);

		boolean result = userHandsetFilter.doFilter(user, newsDetail);

		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsBLACKBERRYandUserHandsetIsBB_Success()
		throws Exception {
		
		UserHandsetFilter fixture = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(blackberryDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.BLACKBERRY);

		boolean result = fixture.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsIOSandUserHandsetIsIOS_Success()
		throws Exception {
		
		UserHandsetFilter fixture = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(iOS_DeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.IOS);

		boolean result = fixture.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsJ2MEandUserHandsetIsJ2ME_Success()
		throws Exception {
		UserHandsetFilter fixture = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(j2meDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.J2ME);

		boolean result = fixture.doFilter(user, newsDetail);
		
		assertEquals(true, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsBLACKBERRYandUserHandsetIsJ2ME_Failure()
		throws Exception {
		UserHandsetFilter fixture = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(blackberryDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.J2ME);

		boolean result = fixture.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}

	/**
	 * Run the boolean doFilter(User,Object) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 02.02.12 18:04
	 */
	@Test
	public void testDoFilter_WhenUserDeviceTypeIsANDROIDandUserHandsetIsJ2ME_Failure()
		throws Exception {
		UserHandsetFilter fixture = new UserHandsetFilter();
		
		User user = new User();
		
		user.setDeviceType(androidDeviceType);
		
		NewsDetail newsDetail = new NewsDetail();
		newsDetail.setUserHandset(mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset.J2ME);

		boolean result = fixture.doFilter(user, newsDetail);
		
		assertEquals(false, result);
	}

	@Before
	public void setUp(){
		iOS_DeviceType = new DeviceType();
		noneDeviceType  = new DeviceType();
		androidDeviceType  = new DeviceType();
		j2meDeviceType  = new DeviceType();
		blackberryDeviceType  = new DeviceType();
		symbianDeviceType  = new DeviceType();
		
		iOS_DeviceType.setName(DeviceTypeDao.IOS);
		noneDeviceType.setName(DeviceTypeDao.NONE);
		androidDeviceType.setName(DeviceTypeDao.ANDROID);
		j2meDeviceType.setName(DeviceTypeDao.J2ME);
		blackberryDeviceType.setName(DeviceTypeDao.BLACKBERRY);
		symbianDeviceType.setName(DeviceTypeDao.SYMBIAN);
		
		PowerMockito.mockStatic(DeviceTypeDao.class);
		
		PowerMockito.when(DeviceTypeDao.getIOSDeviceType()).thenReturn(iOS_DeviceType);
		PowerMockito.when(DeviceTypeDao.getNoneDeviceType()).thenReturn(noneDeviceType);
		PowerMockito.when(DeviceTypeDao.getAndroidDeviceType()).thenReturn(androidDeviceType);
		PowerMockito.when(DeviceTypeDao.getJ2meDeviceType()).thenReturn(j2meDeviceType);
		PowerMockito.when(DeviceTypeDao.getBlackberryDeviceType()).thenReturn(blackberryDeviceType);
		PowerMockito.when(DeviceTypeDao.getSymbianDeviceType()).thenReturn(symbianDeviceType);	
	}
}