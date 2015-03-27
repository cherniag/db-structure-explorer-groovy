package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ActivationStatusException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;

import org.apache.commons.lang3.ArrayUtils;

public class UserActivationStatusService {
    public void checkActivationStatus(User user, ActivationStatus... availableActivationStatuses) {
        final ActivationStatus activationStatus = user.getActivationStatus();

        if (ArrayUtils.isNotEmpty(availableActivationStatuses)) {
            if (!ArrayUtils.contains(availableActivationStatuses, activationStatus)) {
                throw new ActivationStatusException(activationStatus, availableActivationStatuses[0]);
            }
        }

        String message = null;
        String messageCode = null;
        if (activationStatus == REGISTERED) {
            if (!user.isTempUserName()) {
                message = "User activation status [REGISTERED] is invalid. User must have temp userName";
                messageCode = "error.604.activation.status.REGISTERED.invalid.userName";
            } else if (user.hasAllDetails()) {
                message = "User activation status [REGISTERED] is invalid. User can't have all details";
                messageCode = "error.604.activation.status.REGISTERED.invalid.userDetails";
            } else if (!user.isLimited()) {
                message = "User activation status [REGISTERED] is invalid. User must have limit status";
                messageCode = "error.604.activation.status.REGISTERED.invalid.status";
            } else if (user.hasPhoneNumber()) {
                message = "User activation status [REGISTERED] is invalid. User can't have phoneNumber";
                messageCode = "error.604.activation.status.REGISTERED.invalid.phoneNumber";
            }
        } else if (activationStatus == ENTERED_NUMBER) {
            if (!user.isTempUserName()) {
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have temp userName";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.userName";
            } else if (!user.isLimited()) {
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have limit status";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.status";
            } else if (!user.hasPhoneNumber()) {
                message = "User activation status [ENTERED_NUMBER] is invalid. User must have phoneNumber";
                messageCode = "error.604.activation.status.ENTERED_NUMBER.invalid.phoneNumber";
            }
        } else if (activationStatus == ACTIVATED) {
            if (!user.hasAllDetails()) {
                message = "User activation status [ACTIVATED] is invalid. User must have all user details";
                messageCode = "error.604.activation.status.ACTIVATED.invalid.userDetails";
            } else if (!user.isActivatedUserName()) {
                message = "User activation status [ACTIVATED] is invalid. User must have activated userName";
                messageCode = "error.604.activation.status.ACTIVATED.invalid.userName";
            }
        }

        if (message != null) {
            throw new ActivationStatusException(message, messageCode);
        }
    }
}
