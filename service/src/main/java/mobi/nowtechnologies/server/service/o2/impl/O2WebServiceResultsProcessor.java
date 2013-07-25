package mobi.nowtechnologies.server.service.o2.impl;

import java.util.Arrays;
import java.util.Collection;

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

import com.google.common.base.Preconditions;

/** process results of O2 web service calls */
public class O2WebServiceResultsProcessor {
	private final Logger LOGGER = LoggerFactory
			.getLogger(O2WebServiceResultsProcessor.class);

	private static final String BOLTON_4G = "4G Bolt On";

	private static final Collection<String> DIRECT_CHANNEL_PARTNERS = Arrays
			.asList(new String[] { "agent", "consumer", "LBM2_in", "LBM2_out",
					"retail_shop", "upgrade_consumer", "upgrade_csa" });

	private static final Collection<String> KNOWN_INDIRECT_CHANNEL_PARTNERS = Arrays
			.asList(new String[] { "CPW", "esme", "Mass_Distribution",
					"PHONES4U", "TESCO" });

	/**
	 * @return true if PostPayTarif.getContract returns 4G related
	 *         Tariff/ProductClassification
	 */
	public boolean isPostPayContract4G(GetContractResponse r) {
		Preconditions.checkNotNull(r);
		Preconditions.checkNotNull(r.getCurrentContract());
		Preconditions.checkNotNull(r.getCurrentContract().getTariff());

		String productClassification = r.getCurrentContract().getTariff()
				.getProductClassification();
		if (productClassification == null) {
			productClassification = "";
		}

		return productClassification.toUpperCase().contains("4G");
	}

	/**
	 * @return new subscriber data object with populated
	 *         contract/segment/provider
	 */
	public O2SubscriberData getSubscriberData(
			GetSubscriberProfileResponse subscriberProfile) {
		O2SubscriberData res = new O2SubscriberData();
		res.setBusinessOrConsumerSegment(subscriberProfile
				.getSubscriberProfile().getSegment() == SegmentType.CORPORATE);
		res.setContractPostPayOrPrePay(subscriberProfile.getSubscriberProfile()
				.getPaymentCategory() == PaymentCategoryType.POSTPAY);
		res.setProviderO2("O2".equalsIgnoreCase(subscriberProfile
				.getSubscriberProfile().getOperator()));
		return res;
	}

	/**
	 * @return true if post pay bolton(subscription) is present in customer
	 *         boltons(subscriptions)
	 */
	public boolean isPostPay4GBoltonPresent(GetCurrentBoltonsResponse boltons) {
		boolean bolton4GFound = false;
		for (ProductType productType : boltons.getMyCurrentBoltons()
				.getBolton()) {
			if (productType.getProductClassification() != null
					&& (productType.getProductClassification()
							.contains(BOLTON_4G))) {
				bolton4GFound = true;
			}
		}
		return bolton4GFound;
	}

	/** updates 4G and direct flags for prepay */
	public void populatePrepay4G(GetTariff1Response prepayTariff,
			O2SubscriberData data) {

		LOGGER.info("populate prepay4G tariffId: "
				+ prepayTariff.getCurrentTariff().getTariffDetail()
						.getTariffId() + " AllowanceStatusExternal="
				+ prepayTariff.getCurrentTariff().getAllowanceStatusExternal());

		if (is4GTariffId(prepayTariff.getCurrentTariff().getTariffDetail()
				.getTariffId().intValue())) {

			data.setTariff4G(true);

			if ("0".equals(prepayTariff.getCurrentTariff()
					.getAllowanceStatusExternal())) {
				data.setDirectOrIndirect4GChannel(true);
			}
		}

	}

	private static boolean is4GTariffId(int tariffId) {
		return tariffId == 43 || tariffId == 44 || tariffId == 45;
	}

	/** @return true if last order's partner is direct partner */
	public boolean isPostpayDirectPartner(GetOrderList2Response orderList,
			String phone) {
		boolean direct = false;
		if ((orderList.getOrder() != null) || (orderList.getOrder().size() > 0)) {

			Order2SummaryType order = orderList.getOrder().get(
					orderList.getOrder().size() - 1);

			String partner = order.getPartner();
			if (partner == null) {
				partner = "";
			}

			direct = DIRECT_CHANNEL_PARTNERS.contains(partner);
			if (!direct) {
				boolean knownIndirectChannelPartner = KNOWN_INDIRECT_CHANNEL_PARTNERS
						.contains(partner);
				if (!knownIndirectChannelPartner) {
					LOGGER.warn("partner is not listed as direct or indirect "
							+ partner + " phone:" + phone);
				}
			}
		}
		return direct;
	}

}
