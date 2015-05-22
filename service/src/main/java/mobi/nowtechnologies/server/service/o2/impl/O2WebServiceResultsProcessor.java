package mobi.nowtechnologies.server.service.o2.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.o2.soa.coredata_1.PaymentCategoryType;
import uk.co.o2.soa.coredata_1.SegmentType;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.manageorderdata_2.Order2SummaryType;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse;
import uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;
import uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse;

/** process results of O2 web service calls */
public class O2WebServiceResultsProcessor {

    private static final String BOLTON_4G = "4G Bolt On";
    private static final Collection<String> DIRECT_CHANNEL_PARTNERS = Arrays.asList(new String[] {"agent", "consumer", "LBM2_in", "LBM2_out", "retail_shop", "upgrade_consumer", "upgrade_csa"});
    private static final Collection<String> KNOWN_INDIRECT_CHANNEL_PARTNERS = Arrays.asList(new String[] {"CPW", "esme", "Mass_Distribution", "PHONES4U", "TESCO"});
    private static final Collection<String> SUBSCRIBER_O2_CHANNELS = Arrays.asList(new String[] {"ISP", "OFFLINE", "ONLINE"});
    private final Logger LOGGER = LoggerFactory.getLogger(O2WebServiceResultsProcessor.class);
    private Set<Integer> tarrif4GCodes = Sets.newHashSet(43, 44, 45, 46, 47, 48, 52);

    public boolean is4GTariffId(int tariffId) {
        return tarrif4GCodes.contains(tariffId);
    }

    /**
     * @return true if PostPayTarif.getContract returns 4G related Tariff/ProductClassification
     */
    public boolean isPostPayContract4G(GetContractResponse r) {
        Preconditions.checkNotNull(r);
        Preconditions.checkNotNull(r.getCurrentContract());
        Preconditions.checkNotNull(r.getCurrentContract().getTariff());

        String productClassification = r.getCurrentContract().getTariff().getProductClassification();
        if (productClassification == null) {
            productClassification = "";
        }

        return productClassification.toUpperCase().contains("4G");
    }

    /**
     * @return new subscriber data object with populated contract/segment/provider
     */
    public O2SubscriberData getSubscriberData(GetSubscriberProfileResponse subscriberProfile) {
        O2SubscriberData res = new O2SubscriberData();

        boolean consumerUser = subscriberProfile.getSubscriberProfile().getSegment() == SegmentType.CONSUMER;
        boolean businessUser = !consumerUser;

        res.setBusinessOrConsumerSegment(businessUser);
        res.setContractPostPayOrPrePay(subscriberProfile.getSubscriberProfile().getPaymentCategory() == PaymentCategoryType.POSTPAY);

        boolean o2ProviderFlag = "O2".equalsIgnoreCase(subscriberProfile.getSubscriberProfile().getOperator());
        res.setProviderO2(o2ProviderFlag && isChannelO2(subscriberProfile.getSubscriberProfile().getChannel()));

        return res;
    }

    private boolean isChannelO2(String channel) {
        if (channel == null) {
            return false;
        }
        return SUBSCRIBER_O2_CHANNELS.contains(channel.toUpperCase());
    }

    /**
     * @return true if post pay bolton(subscription) is present in customer boltons(subscriptions)
     */
    public boolean isPostPay4GBoltonPresent(GetCurrentBoltonsResponse boltons) {
        boolean bolton4GFound = false;
        for (ProductType productType : boltons.getMyCurrentBoltons().getBolton()) {
            if (productType.getProductClassification() != null && (productType.getProductClassification().contains(BOLTON_4G))) {
                bolton4GFound = true;
            }
        }
        return bolton4GFound;
    }

    /** updates 4G and direct flags for prepay */
    public void populatePrepay4G(GetTariff1Response prepayTariff, O2SubscriberData data) {

        LOGGER.info("populate prepay4G tariffId: " + prepayTariff.getCurrentTariff().getTariffDetail().getTariffId() + " AllowanceStatusExternal=" +
                    prepayTariff.getCurrentTariff().getAllowanceStatusExternal());

        if (is4GTariffId(prepayTariff.getCurrentTariff().getTariffDetail().getTariffId().intValue())) {

            if ("ACTIVE".equals(prepayTariff.getCurrentTariff().getAllowanceStatusExternal())) {
                data.setTariff4G(true);
            }
            data.setDirectOrIndirect4GChannel(true);
        }

    }

    /** @return true if last order's partner is direct partner */
    public boolean isPostpayDirectPartner(GetOrderList2Response orderList, String phone) {
        boolean direct = false;
        if ((orderList.getOrder() != null) && (orderList.getOrder().size() > 0)) {

            Order2SummaryType order = orderList.getOrder().get(0);

            String partner = order.getPartner();
            if (partner == null) {
                partner = "";
            }

            direct = DIRECT_CHANNEL_PARTNERS.contains(partner);
            if (!direct) {
                boolean knownIndirectChannelPartner = KNOWN_INDIRECT_CHANNEL_PARTNERS.contains(partner);
                if (!knownIndirectChannelPartner) {
                    LOGGER.warn("partner is not listed as direct or indirect " + partner + " phone:" + phone);
                }
            }
        }
        return direct;
    }

}
