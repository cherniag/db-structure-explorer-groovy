package mobi.nowtechnologies.server.job;

import javapns.Push;
import javapns.devices.Device;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.UserIPhoneDetailsService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class NotificationJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationJob.class);

	protected ChartDetailService chartDetailService;
	protected UserIPhoneDetailsService userIPhoneDetailsService;
	protected Community community;
	protected int numberOfThreads;
	protected Resource keystore;
	protected String password;
	protected boolean production;
	protected int userIPhoneDetailsListFetchSize;

	private Pageable pageable; 
	
	public void setChartDetailService(ChartDetailService chartDetailService) {
		this.chartDetailService = chartDetailService;
	}

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
	
	public void setUserIPhoneDetailsListFetchSize(int userIPhoneDetailsListFetchSize) {
		if (userIPhoneDetailsListFetchSize<=0)
			throw new IllegalArgumentException("The userIPhoneDetailsListFetchSize must not be less than or equal to zero!");
		
		this.userIPhoneDetailsListFetchSize = userIPhoneDetailsListFetchSize;
		
		pageable = new PageRequest(0, userIPhoneDetailsListFetchSize);
	}

	public void setCommunityName(String communityName) {
		if (communityName == null)
			throw new NullPointerException("The parameter communityName is null");
		LOGGER.debug("input parameters communityName: [{}]", communityName);

		this.community = CommunityDao.getCommunity(communityName);
		if (community == null)
			throw new NullPointerException("The parameter community is null");
	}

	public List<PushedNotification> execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Notification job starting...");
			
			final long epochMillis = Utils.getEpochMillis();
			Long nearestLatestPublishTimeMillis = chartDetailService.findNearestLatestPublishTimeMillis(community, epochMillis);
			
			List<UserIPhoneDetails> userIPhoneDetailsList = userIPhoneDetailsService.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable);

			Map<String, UserIPhoneDetails> userIPhoneDetailsMap = new LinkedHashMap<String, UserIPhoneDetails>();
			List<PayloadPerDevice> payloadDevicePairs = new Vector<PayloadPerDevice>();
			for (UserIPhoneDetails userIPhoneDetails : userIPhoneDetailsList) {
				try {
					PushNotificationPayload pushNotificationPayload = PushNotificationPayload.complex();
					pushNotificationPayload.addBadge(1);
					String deviceToken = userIPhoneDetails.getToken();
					PayloadPerDevice payloadPerDevice = new PayloadPerDevice(pushNotificationPayload, deviceToken);
					payloadDevicePairs.add(payloadPerDevice);
					userIPhoneDetailsMap.put(deviceToken, userIPhoneDetails);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

			List<PushedNotification> pushedNotifications = Collections.<PushedNotification> emptyList();
			if (!payloadDevicePairs.isEmpty()) {
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
									.markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis);
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
