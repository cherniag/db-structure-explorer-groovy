/**
 * 
 */
package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.NotPromotedDeviceRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotedDeviceRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class DeviceService {
	private final static String DEFAULT_PROMO_PHONE_MSG_CODE = "promoted.device.phones";
	private final static String DEFAULT_PROMO_PHONE_OTAC_MSG_CODE = "promoted.otac.only.device.phones"; 
	
	private NotPromotedDeviceRepository notPromotedDeviceRepository;
	private PromotedDeviceRepository promotedDeviceRepository;
	private CommunityResourceBundleMessageSource messageSource;
	
	public void init() throws Exception {
		if (notPromotedDeviceRepository == null)
			throw new Exception("The not promoted Device Repository is null");
		if (promotedDeviceRepository == null)
			throw new Exception("The promoted Device Repository is null");
	}
	
	public Map<String, Object> setDevice(int userId, String deviceType,
			String deviceUID) {
		if (null == deviceType)
			throw new ServiceException("The parameter deviceType is null");
		if (null == deviceUID)
			throw new ServiceException("The parameter deviceUID is null");
		return DeviceTypeService.setDevice(userId, deviceType, deviceUID);
	}
	
	public boolean existsInPromotedList(Community community, String deviceUID) {
		return (null!=promotedDeviceRepository.findByDeviceUIDAndCommunity(deviceUID, community))?true:false;
	}
	
	public boolean existsInNotPromotedList(Community community, String deviceUID) {
		return (null!=notPromotedDeviceRepository.findByDeviceUIDAndCommunity(deviceUID, community))?true:false;
	}
	
	public boolean isPromotedDeviceModel(Community community, String deviceModel) {
		if (null!=deviceModel) {
			String promotedDeviceModels = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "promoted.device.models", null, null);
			return (promotedDeviceModels.indexOf(deviceModel)!=-1)?true:false;
		}
		return false;
	}
	
	/**
	 * Checks if a phone number is 'otac promoted'. This is used to avoid otac checks with o2 but to have all other checks
	 * in place (as user information). Used by test devices, where we don't want otac validation but we want user details 
	 * 
	 * @param community
	 * @param phoneNumber
	 * @param promoCode
	 * @return
	 */
	public boolean isOtacPromotedDevicePhone(Community community, String phoneNumber, String promoCode) {
		return isPromotedDevicePhone(community, phoneNumber, promoCode, DEFAULT_PROMO_PHONE_OTAC_MSG_CODE);
	}
	
	public boolean isPromotedDevicePhone(Community community, String phoneNumber, String promoCode) {
		return isPromotedDevicePhone(community, phoneNumber, promoCode, DEFAULT_PROMO_PHONE_MSG_CODE);
	}
	
	public boolean isPromotedDevicePhone(Community community, String phoneNumber, String promoCode, String propertyName) {
		if (!isEmpty(phoneNumber)) {
			
			String[] msgCodes = new String[3];
			msgCodes[0] = propertyName;
			msgCodes[1] = community.getRewriteUrlParameter().toLowerCase()+"."+msgCodes[0];
			
			if(promoCode != null){
				msgCodes[2] = promoCode != null ? community.getRewriteUrlParameter().toLowerCase()+"."+promoCode+"."+msgCodes[0] : null;
				msgCodes[0] = msgCodes[1] = null;
			}
			
			String promotedDevicePhones = "";
			
			for (int i = msgCodes.length-1; i >= 0; i--) {
				if(msgCodes[i] != null){				
					String msg = messageSource.getMessage(community.getRewriteUrlParameter(), msgCodes[i], null, "", null);
					if (StringUtils.isNotEmpty(msg)){						
						promotedDevicePhones = msg;
						break;
					}
				}
			}
			
			return promotedDevicePhones.contains(phoneNumber);
		}
		return false;
	}
	
	public void setNotPromotedDeviceRepository(
			NotPromotedDeviceRepository notPromotedDeviceRepository) {
		this.notPromotedDeviceRepository = notPromotedDeviceRepository;
	}

	public void setPromotedDeviceRepository(
			PromotedDeviceRepository promotedDeviceRepository) {
		this.promotedDeviceRepository = promotedDeviceRepository;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}