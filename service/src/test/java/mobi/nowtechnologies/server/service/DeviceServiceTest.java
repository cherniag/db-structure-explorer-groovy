package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceTest {

	private DeviceService fixture;
	
	@Mock
	private CommunityResourceBundleMessageSourceImpl mockMessageSource;

	@Test
	public void testIsPromotedDevicePhone_PromotedNoComunityNoPromo_Success()
		throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111111, +447870111112";

		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, null);

		assertTrue(result);
	}

	@Test
	public void testIsPromotedDevicePhone_NotPomotedNoComunityNoPromo_Success()
		throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111113, +447870111112";

		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);

		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, null);

		assertFalse(result);
	}
	
	@Test
	public void testIsPromotedDevicePhone_PromotedNoPromo_Success()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111111, +447870111112";
		
		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, null);
		
		assertTrue(result);
		
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(1)).getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
	}
	
	@Test
	public void testIsPromotedDevicePhone_NotPomotedNoPromo_Success()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111113, +447870111112";
		
		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, null);
		
		assertFalse(result);
		
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(1)).getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
	}
	
	@Test
	public void testIsPromotedDevicePhone_Promoted_Success()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111111, +447870111112";
		String promoCode = "staff";
		
		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, promoCode);
		
		assertTrue(result);
		
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(1)).getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
	}
	
	@Test
	public void testIsPromotedDevicePhone_NotPomoted_Success()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "+447870111113, +447870111112";
		String promoCode = "staff";
		
		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, promoCode);
		
		assertFalse(result);
		
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(1)).getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
	}
	
	@Test
	public void testIsPromotedDevicePhone_NotPomotedEmptyPromoPhones_Success()
			throws Exception {
		final Community community = CommunityFactory.createCommunity();		
		community.setRewriteUrlParameter("o2");
		community.setName("o2");;
		String phoneNumber = "+447870111111";
		String promotedPhones = "";
		String promoCode = "staff";
		
		when(mockMessageSource.getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		when(mockMessageSource.getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class))).thenReturn(promotedPhones);
		
		boolean result = fixture.isPromotedDevicePhone(community, phoneNumber, promoCode);
		
		assertFalse(result);
		
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(0)).getMessage(anyString(), eq("o2.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
		verify(mockMessageSource, times(1)).getMessage(anyString(), eq("o2.staff.promoted.device.phones"), any(Object[].class), eq(""), any(Locale.class));
	}

	@Before
	public void setUp()
		throws Exception {
		
		fixture = new DeviceService();
		fixture.setMessageSource(mockMessageSource);
	}
}