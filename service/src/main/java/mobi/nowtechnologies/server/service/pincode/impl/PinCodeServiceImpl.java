package mobi.nowtechnologies.server.service.pincode.impl;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Anton Zemliankin
 */

public class PinCodeServiceImpl implements PinCodeService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    PinCodeRepository pinCodeRepository;

    private int maxAttempts;
    private int expirationSeconds;
    private int limitSeconds;
    private int limitCount;

    @Override
    public PinCode generate(User user, int digitsCount) throws PinCodeException.MaxPinCodesReached {
        log.debug("Generating new {}-digit pin code for user {}", digitsCount, user.getId());

        Date selectFromDate = new Date(System.currentTimeMillis() - limitSeconds * 1000);
        int allUserPinCodesCount = pinCodeRepository.countUserPinCodes(user.getId(), selectFromDate);

        if (allUserPinCodesCount >= limitCount) {
            throw new PinCodeException.MaxPinCodesReached(String.format("Max count(%s) of pin codes for user per period(%s seconds) has been reached.", limitCount, limitSeconds));
        }

        PinCode pinCode = new PinCode(user.getId(), generateValue(digitsCount));

        log.debug("Generated pin code {} for user {}", pinCode.getCode(), user.getId());
        return pinCodeRepository.save(pinCode);
    }

    @Override
    public boolean check(User user, String pinCodeStr) throws PinCodeException.NotFound, PinCodeException.MaxAttemptsReached {
        log.debug("Checking pin code {} for user {}", pinCodeStr, user.getId());

        Date selectFromTime = new Date(System.currentTimeMillis() - expirationSeconds * 1000);
        PinCode userLatestPinCode = pinCodeRepository.findPinCodeByUserAndCreationTime(user.getId(), selectFromTime);

        if (userLatestPinCode == null) {
            throw new PinCodeException.NotFound("Pin code not found or has been expired.");
        }

        if (userLatestPinCode.getAttempts() >= maxAttempts) {
            throw new PinCodeException.MaxAttemptsReached(String.format("Max count(%s) of attempts has been reached.", maxAttempts));
        }

        boolean checkResult = userLatestPinCode.getCode().equals(pinCodeStr);
        userLatestPinCode.incAttempts();
        userLatestPinCode.setEntered(checkResult);
        pinCodeRepository.save(userLatestPinCode);

        log.debug("Pin code {} for user {} is {}", pinCodeStr, user.getId(), checkResult ? "valid" : "not valid");
        return checkResult;
    }

    private String generateValue(int digitsCount) {
        return RandomStringUtils.random(digitsCount, false, true);
    }

    @PostConstruct
    public void checkConfiguration() {
        Assert.notNull(maxAttempts, "maxAttempts should not be null");
        Assert.notNull(expirationSeconds, "expirationSeconds should not be null");
        Assert.notNull(limitSeconds, "limitSeconds should not be null");
        Assert.notNull(limitCount, "limitCount should not be null");
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
