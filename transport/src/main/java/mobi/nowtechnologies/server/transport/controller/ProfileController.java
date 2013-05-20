package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.util.ServerMessage;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class ProfileController {
	protected final Logger PROFILE_LOGGER = LoggerFactory.getLogger("PROFILE_LOGGER");
	
	public void logProfileData(String deviceUIDFromRequest, String communityFromRequest, UserDeviceRegDetailsDto userDeviceRegDetailsDto, String PHONEFromRequest, User user, Exception exception) {
		String result = "success";
		String errorMessage = null;
		if (exception != null) {
			result = "fail";
			errorMessage = exception.getMessage();
			if (errorMessage == null) {
				if (exception instanceof ServiceException) {
					ServiceException serviceException = (ServiceException) exception;

					ServerMessage serverMessage = serviceException.getServerMessage();
					String errorCodeForMessageLocalization = serviceException.getErrorCodeForMessageLocalization();
					if (serverMessage != null) {
						String localizedMessage = ServerMessage.getMessage(ServerMessage.EN, serverMessage.getErrorCode(), serverMessage.getParameters());
						errorMessage = localizedMessage;
					} else {
						errorMessage = errorCodeForMessageLocalization;
					}

				}
			}
		}

		Integer newUserId = null;
		String newUserName = null;
		String newDeviceUID = null;
		String newDeviceModel = null;
		String newDeviceType = null;
		String newMobile = null;
		String newCommunityRewriteUri = null;
		if (user != null) {
			newUserId = user.getId();
			newUserName = user.getUserName();
			newDeviceUID = user.getDeviceUID();
			newDeviceModel = user.getDeviceModel();
			newMobile = user.getMobile();
			UserGroup userGroup = user.getUserGroup();
			if (userGroup!=null){
				final Community community = userGroup.getCommunity();
				if (community!=null){
					newCommunityRewriteUri = community.getRewriteUrlParameter();
				}
			}
			final DeviceType userDeviceType = user.getDeviceType();
			if (userDeviceType != null) {
				newDeviceType = userDeviceType.getName();
			}
		}

		Long startTimeMillis = LogUtils.getStartTimeMillis();
		Long executionTimeMillis = null;
		if (startTimeMillis != null) {
			final long epochMillis = Utils.getEpochMillis();
			executionTimeMillis = epochMillis - startTimeMillis;
		}

		String deviceModelFromRequest = null;
		String deviceTypeFromRequest = null;
		if (userDeviceRegDetailsDto!=null){
			deviceModelFromRequest = userDeviceRegDetailsDto.getDeviceModel();
			deviceTypeFromRequest = userDeviceRegDetailsDto.getDeviceType();
			deviceUIDFromRequest = userDeviceRegDetailsDto.getDeviceUID();
			if (communityFromRequest==null){
				communityFromRequest = userDeviceRegDetailsDto.getCommunityName();
			}
		}
		
		PROFILE_LOGGER.info("communityFromRequest={}; deviceModelFromRequest={}; deviceTypeFromRequest={}; deviceUIDFromRequest={}; PHONEFromRequest={}; newUserId={}; newUserName={}; newCommunityRewriteUri={}; newMobile={}; newDeviceUID={}; newDeviceModel={}; newDeviceType={}; result={}; executionTimeMillis={}; errorMessage={}",
				new Object[] {communityFromRequest, deviceModelFromRequest, deviceTypeFromRequest, deviceUIDFromRequest, PHONEFromRequest, newUserId, newUserName, newCommunityRewriteUri, newMobile, newDeviceUID, newDeviceModel, newDeviceType, result,
						executionTimeMillis, errorMessage });
	}

}
