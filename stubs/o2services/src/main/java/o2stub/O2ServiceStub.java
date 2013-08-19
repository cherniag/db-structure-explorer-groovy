package o2stub;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.o2.soa.coredata_1.PaymentCategoryType;
import uk.co.o2.soa.coredata_1.SegmentType;
import uk.co.o2.soa.managepostpayboltonsdata_2.MyCurrentBoltonsType;
import uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;
import uk.co.o2.soa.subscriberdata_2.SubscriberProfileType;

public class O2ServiceStub {
	private static final Logger LOGGER = LoggerFactory.getLogger(O2ServiceStub.class);

	public SubscriberProfileType getSubscriberProfileInternal(String subscriberID) {
		SubsData d = PhoneNumberManager.getInstance().getData(subscriberID);
		LOGGER.info("getSubscriber data " + subscriberID + " " + d);

		SubscriberProfileType s = new SubscriberProfileType();
		s.setOperator(d.isO2() ? "o2" : "non-o2");
		s.setSegment(d.isBusiness() ? SegmentType.CORPORATE : SegmentType.CONSUMER);
		s.setPaymentCategory(d.isPayAsYouGo() ? PaymentCategoryType.PREPAY : PaymentCategoryType.POSTPAY);
		return s;
	}

	public ServiceContractType getManagePostpayContract(String phoneNumber) {
		SubsData d = PhoneNumberManager.getInstance().getData(phoneNumber);
		LOGGER.info(" getManagePostpayContract:  " + phoneNumber + " " + d);

		ServiceContractType serviceContract = new ServiceContractType();
		serviceContract.setTariff(new ProductType());
		serviceContract.getTariff().setProductClassification(d.isTariff4G() ? "4GproductClassification" : "3Gclasn");

		return serviceContract;
	}

	public MyCurrentBoltonsType getCurrentBoltonsPostPay(Holder<String> msisdn) {
		MyCurrentBoltonsType res = new MyCurrentBoltonsType();
		//res.getBolton().add
		return res;
	}

}
