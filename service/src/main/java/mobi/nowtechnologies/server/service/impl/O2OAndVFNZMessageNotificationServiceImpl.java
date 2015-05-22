package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.MessageNotificationService;
import mobi.nowtechnologies.server.shared.ObjectUtils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @author Titov Mykhaylo (titov) on 02.03.2015.
public class O2OAndVFNZMessageNotificationServiceImpl implements MessageNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(O2OAndVFNZMessageNotificationServiceImpl.class);

    @Resource(name = "serviceMessageSource")
    private CommunityResourceBundleMessageSource messageSource;

    @Override
    public String getMessage(User user, String msgCodeBase, String[] msgArgs) {
        Community community = user.getCommunity();

        LOGGER.debug("input parameters user, community, msgCodeBase, msgArgs: [{}], [{}], [{}], [{}]", user, community, msgCodeBase, msgArgs);

        if (msgCodeBase == null) {
            throw new NullPointerException("The parameter msgCodeBase is null");
        }

        String msg = null;

        String[] codes = new String[25];

        final String providerKey = ObjectUtils.isNull(user.getProvider()) ? null : user.getProvider().getKey();
        final SegmentType segment = user.getSegment();
        final Contract contract = user.getContract();
        final DeviceType deviceType = user.getDeviceType();
        final PaymentDetails paymentDetails = user.getCurrentPaymentDetails();
        String deviceTypeName = null;

        if (deviceType != null) {
            deviceTypeName = deviceType.getName();
        }

        codes[0] = msgCodeBase;
        codes[1] = getCode(codes, 0, providerKey, true);
        codes[2] = getCode(codes, 1, segment, true);
        codes[3] = getCode(codes, 2, contract, true);
        codes[4] = getCode(codes, 3, deviceTypeName, true);
        codes[5] = getCode(codes, 4, Tariff._4G.equals(user.getTariff()) ? "VIDEO" : null, true);
        codes[6] = getCode(codes, 5, paymentDetails != null ? paymentDetails.getPaymentType() : null, true);

        if (paymentDetails != null) {
            PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
            String prefix = "before";
            final String preProviderKey = ObjectUtils.isNull(paymentPolicy.getProvider()) ? null : paymentPolicy.getProvider().getKey();
            final SegmentType preSegment = paymentPolicy.getSegment();
            final Contract preContract = paymentPolicy.getContract();
            final String providerSuffix = prefix + "." + preProviderKey;
            final String segmentSuffix = providerSuffix + "." + preSegment;
            final String contractSuffix = segmentSuffix + "." + preContract;
            for (int i = 1; i <= 6; i++) {
                if (!StringUtils.equals(preProviderKey, providerKey)) {
                    codes[1 * 6 + i] = getCode(codes, i, providerSuffix, false);
                }
                if (segment != preSegment) {
                    codes[2 * 6 + i] = getCode(codes, i, segmentSuffix, false);
                }
                if (contract != preContract) {
                    codes[3 * 6 + i] = getCode(codes, i, contractSuffix, false);
                }
            }
        }

        for (int i = codes.length - 1; i >= 0; i--) {
            if (codes[i] != null) {
                msg = messageSource.getMessage(community.getRewriteUrlParameter(), codes[i], msgArgs, "", null);
                if (StringUtils.isNotEmpty(msg)) {
                    break;
                }
            }
        }

        LOGGER.debug("Output parameter msg=[{}]", msg);
        return msg;
    }

    private String getCode(String[] codes, int i, Object value, boolean recursive) {
        LOGGER.debug("input parameters codes, i, value: [{}], [{}], [{}]", codes, i, value);

        if (codes == null) {
            throw new NullPointerException("The parameter codes is null");
        }
        if (codes.length == 0) {
            throw new IllegalArgumentException("The parameter codes of array type has 0 size");
        }
        if (codes[0] == null) {
            throw new IllegalArgumentException("The parameter codes of array type has null value as first element");
        }
        if (i >= codes.length) {
            throw new IllegalArgumentException("The parameter i>=codes.length. i=" + i);
        }
        if (i < 0) {
            throw new IllegalArgumentException("The parameter i less than 0. i=" + i);
        }

        String code = null;

        if (value != null) {
            final String prefix = codes[i];
            if (prefix != null) {
                if (i == 0) {
                    code = prefix + ".for." + value;
                } else {
                    code = prefix + "." + value;
                }
            } else if (recursive) {
                code = getCode(codes, i - 1, value, recursive);
            }
        }
        LOGGER.debug("Output parameter code=[{}]", code);
        return code;
    }
}
