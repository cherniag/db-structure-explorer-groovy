package mobi.nowtechnologies.server.job;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javapns.Push;
import javapns.devices.Device;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.service.UserIPhoneDetailsService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class NotificationJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJob.class);

	private UserIPhoneDetailsService userIPhoneDetailsService;
	private Community community;
	private int numberOfThreads;
	private Resource keystore;
	private String password;
	private boolean production;

	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	public void setKeystore(Resource keystore) {
		this.keystore = keystore;
	}

	public void setUserIPhoneDetailsService(UserIPhoneDetailsService userIPhoneDetailsService) {
		this.userIPhoneDetailsService = userIPhoneDetailsService;
	}

	public void setCommunityName(String communityName) {
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		LOGGER.debug("input parameters communityName: [{}]", communityName);

		this.community = CommunityDao.getMapAsNames().get(communityName);
		if (community == null)
			throw new NullPointerException("The parameter community is null");
	}

	// public int prepareToWork() {
	// LOGGER.debug("input parameters : [{}]", new Object[] {});
	// int updatedRowsCount =
	// userIPhoneDetailsService.updateUserIPhoneDetailsForPushNotification(community);
	// LOGGER.debug("Output parameter updatedRowsCount=[{}]", updatedRowsCount);
	// return updatedRowsCount;
	// }

	@SuppressWarnings("unchecked")
	public List<PushedNotification> execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Notification job starting...");
			List<UserIPhoneDetails> userIPhoneDetailsList = userIPhoneDetailsService.getUserIPhoneDetailsListForPushNotification(community);

			Map<String, UserIPhoneDetails> userIPhoneDetailsMap = new LinkedHashMap<String, UserIPhoneDetails>();
			List<PayloadPerDevice> payloadDevicePairs = new Vector<PayloadPerDevice>();
			for (UserIPhoneDetails userIPhoneDetails : userIPhoneDetailsList) {
				try {
					PushNotificationPayload pushNotificationPayload = PushNotificationPayload.complex();
					pushNotificationPayload.addBadge(userIPhoneDetails.getNbUpdates());
					String deviceToken = userIPhoneDetails.getToken();
					PayloadPerDevice payloadPerDevice = new PayloadPerDevice(pushNotificationPayload, deviceToken);
					payloadDevicePairs.add(payloadPerDevice);
					userIPhoneDetailsMap.put(deviceToken, userIPhoneDetails);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

			List<PushedNotification> pushedNotifications = Collections.EMPTY_LIST;
			if (!payloadDevicePairs.isEmpty()){
				try {
					pushedNotifications = Push.payloads(keystore.getInputStream(), password, production,
							numberOfThreads, payloadDevicePairs);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}

				if (pushedNotifications != null) {
					List<PushedNotification> successfulPushedNotifications = PushedNotification
							.findSuccessfulNotifications(pushedNotifications);
					for (PushedNotification pushedNotification : successfulPushedNotifications) {
						try {
							LOGGER.info("PushedNotification [{}]",
									pushedNotification);
							Device device = pushedNotification.getDevice();
							UserIPhoneDetails userIPhoneDetails = userIPhoneDetailsMap
									.get(device.getToken());
							userIPhoneDetailsService
									.markUserIPhoneDetailsAsProcessed(userIPhoneDetails);
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
						}
					}

					List<PushedNotification> failedPushedNotifications = PushedNotification
							.findFailedNotifications(pushedNotifications);
					for (PushedNotification pushedNotification : failedPushedNotifications) {
						Device device = pushedNotification.getDevice();
						String deviceToken = device.getToken();
						UserIPhoneDetails userIPhoneDetails = userIPhoneDetailsMap
								.get(deviceToken);
						LOGGER
								.error(
										"Codn't send notification to user with id [{}] and deviceToken [{}]. PushedNotification is [{}]",
										new Object[] {
												userIPhoneDetails.getUser()
														.getId(), deviceToken,
												pushedNotification });
					}
				}
			}

			LOGGER.debug("Output parameter pushedNotifications=[{}]", pushedNotifications);
			LOGGER.info("[DONE] Notification job finished");
			return pushedNotifications;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
		return null;
	}

}
