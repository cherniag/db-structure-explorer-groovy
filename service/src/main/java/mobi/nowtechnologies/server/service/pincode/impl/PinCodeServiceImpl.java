package mobi.nowtechnologies.server.service.pincode.impl;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import org.apache.commons.lang.RandomStringUtils;
import javax.annotation.Resource;

/**
 * @author Anton Zemliankin
 */

public class PinCodeServiceImpl implements PinCodeService {

    @Resource
    PinCodeRepository pinCodeRepository;

    private int maxAttempts;
    private int expirationSeconds;
    private int limitSeconds;
    private int limitCount;

    @Override
    public PinCode generatePinCode(int userId, int digitsCount) throws PinCodeException.MaxPinCodesReached {
        int selectFromTime = DateTimeUtils.getEpochSeconds() - limitSeconds;
        Integer allUserPinCodesCount = pinCodeRepository.countByUserIdAndCreationTimeGreaterThan(userId, selectFromTime);

        if(allUserPinCodesCount != null && allUserPinCodesCount >= limitCount){
            throw new PinCodeException.MaxPinCodesReached(String.format("Max count(%s) of pin codes for user per period(%s seconds) has been reached.", limitCount, limitSeconds));
        }

        PinCode pinCode = new PinCode();
        pinCode.setUserId(userId);
        pinCode.setCode(RandomStringUtils.random(digitsCount, false, true));
        pinCode.setAttempts(0);
        pinCode.setCreationTime(DateTimeUtils.getEpochSeconds());
        pinCode.setEntered(false);

        return pinCodeRepository.save(pinCode);
    }

    @Override
    public boolean checkPinCode(int userId, String pinCodeStr) throws PinCodeException.NotFound, PinCodeException.MaxAttemptsReached {
        int selectFromTime = DateTimeUtils.getEpochSeconds() - expirationSeconds;
        PinCode userLatestPinCode = pinCodeRepository.findTopByUserIdAndEnteredFalseAndCreationTimeGreaterThanOrderByCreationTimeDesc(userId, selectFromTime);

        if(userLatestPinCode == null){
            throw new PinCodeException.NotFound("Pin code not found or has been expired.");
        }

        if(userLatestPinCode.getAttempts() >= maxAttempts){
            throw new PinCodeException.MaxAttemptsReached(String.format("Max count(%s) of attempts has been reached.", maxAttempts));
        }

        boolean checkResult = userLatestPinCode.getCode().equals(pinCodeStr);
        userLatestPinCode.setAttempts(userLatestPinCode.getAttempts() + 1);
        userLatestPinCode.setEntered(checkResult);
        pinCodeRepository.save(userLatestPinCode);

        return checkResult;
    }


    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setExpirationSeconds(int expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public void setLimitSeconds(int limitSeconds) {
        this.limitSeconds = limitSeconds;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }
}
