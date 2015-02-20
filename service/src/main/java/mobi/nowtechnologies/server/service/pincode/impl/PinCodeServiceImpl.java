package mobi.nowtechnologies.server.service.pincode.impl;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PinCodeRepository;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * @author Anton Zemliankin
 */

public class PinCodeServiceImpl implements PinCodeService, InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    PinCodeRepository pinCodeRepository;

    private int maxAttempts;
    private int expirationSeconds;
    private int limitSeconds;
    private int limitCount;

    @Override
    public PinCode generate(User user, int digitsCount) throws PinCodeException.MaxPinCodesReached {
        log.info("Generating new {}-digit pin code for user {}", digitsCount, user.getId());

        Date selectFromDate = DateUtils.addSeconds(new Date(), -limitSeconds);

        int allUserPinCodesCount = pinCodeRepository.countUserPinCodes(user.getId(), selectFromDate);

        if (allUserPinCodesCount >= limitCount) {
            throw new PinCodeException.MaxPinCodesReached(String.format("Max count(%s) of pin codes for user per period(%s seconds) has been reached.", limitCount, limitSeconds));
        }

        PinCode pinCode = new PinCode(user.getId(), generateValue(digitsCount));

        log.info("Generated pin code {} for user {}", pinCode.getCode(), user.getId());
        return pinCodeRepository.save(pinCode);
    }

    @Override
    public boolean check(User user, String pinCodeStr) throws PinCodeException.NotFound, PinCodeException.MaxAttemptsReached {
        log.info("Checking pin code {} for user {}", pinCodeStr, user.getId());

        Date selectFromDate = DateUtils.addSeconds(new Date(), -expirationSeconds);

        List<PinCode> userLatestPinCodes = pinCodeRepository.findPinCodesByUserAndCreationTime(user.getId(), selectFromDate);

        if (CollectionUtils.isEmpty(userLatestPinCodes)) {
            throw new PinCodeException.NotFound("Pin code not found or has been expired.");
        }

        PinCode userLatestPinCode = userLatestPinCodes.get(0);
        if (userLatestPinCode.getAttempts() >= maxAttempts) {
            throw new PinCodeException.MaxAttemptsReached(String.format("Max count(%s) of attempts has been reached.", maxAttempts));
        }

        boolean checkResult = userLatestPinCode.getCode().equals(pinCodeStr);
        userLatestPinCode.incAttempts();
        userLatestPinCode.setEntered(checkResult);
        pinCodeRepository.save(userLatestPinCode);

        log.info("Pin code {} for user {} is {}", pinCodeStr, user.getId(), checkResult ? "valid" : "not valid");
        return checkResult;
    }

    private String generateValue(int digitsCount) {
        return RandomStringUtils.random(digitsCount, false, true);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(pinCodeRepository, "pinCodeRepository should not be null");
        Assert.notNull(maxAttempts, "maxAttempts should not be null");
        Assert.notNull(expirationSeconds, "expirationSeconds should not be null");
        Assert.notNull(limitSeconds, "limitSeconds should not be null");
        Assert.notNull(limitCount, "limitCount should not be null");
    }

    public void setPinCodeRepository(PinCodeRepository pinCodeRepository) {
        this.pinCodeRepository = pinCodeRepository;
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
