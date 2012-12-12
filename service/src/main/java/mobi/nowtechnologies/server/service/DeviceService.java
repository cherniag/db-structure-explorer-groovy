/**
 * 
 */
package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceSet;
import mobi.nowtechnologies.server.persistence.repository.NotPromotedDeviceRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotedDeviceRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * 
 */
public class DeviceService {
	
	private NotPromotedDeviceRepository notPromotedDeviceRepository;
	private PromotedDeviceRepository promotedDeviceRepository;
	private CommunityResourceBundleMessageSource messageSource;
	
	public void init() throws Exception {
		if (notPromotedDeviceRepository == null)
			throw new Exception("The not promoted Device Repository is null");
		if (promotedDeviceRepository == null)
			throw new Exception("The promoted Device Repository is null");
	}
	
	public DeviceSet setDevice(int userId, String deviceType,
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