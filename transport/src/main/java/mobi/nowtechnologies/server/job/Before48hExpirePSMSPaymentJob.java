package mobi.nowtechnologies.server.job;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

public class Before48hExpirePSMSPaymentJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(Before48hExpirePSMSPaymentJob.class);
	
	private String availableCommunities = "o2";
	private String availableProviders = "o2";
	private SegmentType availableSegments = SegmentType.CONSUMER;
	private Contract availableContracts = Contract.PAYG;

	private O2ClientService o2ClientService;

	private UserRepository userRepository;
	
	private CommunityResourceBundleMessageSource messageSource;
    final private String messageCode = "job.before48.psms.consumer";

    public void execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Before 48h Expire PSMS Payment job...");
			
			List<User> users = userRepository.findBefore48hExpireUsers(getEpochSeconds(), availableProviders, availableSegments, availableContracts);
			LOGGER.info("Before 48h Expire PSMS Payment [{}] users for handling", users.size());
			for (User user : users) {
				try {
					MDC.put(LogUtils.LOG_USER_NAME, user.getUserName());
					MDC.put(LogUtils.LOG_USER_ID, user.getId());

					String msg = messageSource.getMessage(availableCommunities, messageCode, null, null);

					o2ClientService.sendFreeSms(user.getMobile(), msg);
					
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				} finally {
					MDC.remove(LogUtils.LOG_USER_NAME);
					MDC.remove(LogUtils.LOG_USER_ID);
				}
			}
			
			LOGGER.info("[DONE] Before 48h Expire PSMS Payment job");
		} catch (Exception e) {
			LOGGER.error("Error while Before 48h Expire PSMS Payment job. {}", e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	public void setO2ClientService(O2ClientService o2ClientService) {
		this.o2ClientService = o2ClientService;
	}

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}