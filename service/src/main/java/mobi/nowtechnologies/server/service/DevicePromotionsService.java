/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.repository.NotPromotedDeviceRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotedDeviceRepository;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 */
public class DevicePromotionsService {

    private static final String DEFAULT_PROMO_PHONE_MSG_CODE = "promoted.device.phones";
    private static final String DEFAULT_PROMO_PHONE_OTAC_MSG_CODE = "promoted.otac.only.device.phones";

    private NotPromotedDeviceRepository notPromotedDeviceRepository;
    private PromotedDeviceRepository promotedDeviceRepository;

    private CommunityResourceBundleMessageSource messageSource;

    public void init() throws Exception {
        if (notPromotedDeviceRepository == null) {
            throw new Exception("The not promoted Device Repository is null");
        }
        if (promotedDeviceRepository == null) {
            throw new Exception("The promoted Device Repository is null");
        }
    }

    public boolean existsInPromotedList(Community community, String deviceUID) {
        return null != promotedDeviceRepository.findByDeviceUIDAndCommunity(deviceUID, community);
    }

    public boolean existsInNotPromotedList(Community community, String deviceUID) {
        return null != notPromotedDeviceRepository.findByDeviceUIDAndCommunity(deviceUID, community);
    }

    public boolean isPromotedDeviceModel(Community community, String deviceModel) {
        if (null != deviceModel) {
            String promotedDeviceModels = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "promoted.device.models", null, null);
            return promotedDeviceModels.contains(deviceModel);
        }
        return false;
    }

    /**
     * Checks if a phone number is 'otac promoted'. This is used to avoid otac checks with o2 but to have all other checks in place (as user information). Used by test devices, where we don't want
     * otac validation but we want user details
     *
     * @param community
     * @param phoneNumber
     * @return
     */
    public boolean isOtacPromotedDevicePhone(Community community, String phoneNumber) {
        return isPromotedDevicePhone(community, phoneNumber, null, DEFAULT_PROMO_PHONE_OTAC_MSG_CODE);
    }

    public boolean isPromotedDevicePhone(Community community, String phoneNumber, String promoCode) {
        boolean promotedDevicePhone = isPromotedDevicePhone(community, phoneNumber, promoCode, DEFAULT_PROMO_PHONE_MSG_CODE);

        logger().info("is promoted device('{}')={}", phoneNumber, promotedDevicePhone);

        return promotedDevicePhone;
    }

    public boolean isPromotedDevicePhone(Community community, String phoneNumber, String promoCode, String propertyName) {
        if (!isEmpty(phoneNumber)) {

            String[] msgCodes = new String[3];
            msgCodes[0] = propertyName;
            msgCodes[1] = community.getRewriteUrlParameter().toLowerCase() + "." + msgCodes[0];

            if (promoCode != null) {
                msgCodes[2] = community.getRewriteUrlParameter().toLowerCase() + "." + promoCode + "." + msgCodes[0];
                msgCodes[0] = msgCodes[1] = null;
            }

            String promotedDevicePhones = "";

            for (int i = msgCodes.length - 1; i >= 0; i--) {
                if (msgCodes[i] != null) {
                    String msg = messageSource.getMessage(community.getRewriteUrlParameter(), msgCodes[i], null, "", null);
                    if (StringUtils.isNotEmpty(msg)) {
                        promotedDevicePhones = msg;
                        break;
                    }
                }
            }

            return promotedDevicePhones.contains(phoneNumber);
        }
        return false;
    }

    public void setNotPromotedDeviceRepository(NotPromotedDeviceRepository notPromotedDeviceRepository) {
        this.notPromotedDeviceRepository = notPromotedDeviceRepository;
    }

    public void setPromotedDeviceRepository(PromotedDeviceRepository promotedDeviceRepository) {
        this.promotedDeviceRepository = promotedDeviceRepository;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
