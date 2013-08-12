package com.example.customerservice.server.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.o2.soa.coredata_1.PaymentCategoryType;
import uk.co.o2.soa.coredata_1.SegmentType;
import uk.co.o2.soa.subscriberdata_2.SubscriberProfileType;

public class O2ServiceStub {
	private static final Logger LOGGER = LoggerFactory.getLogger(O2ServiceStub.class);

	public SubscriberProfileType getSubscriberProfileInternal(String subscriberID) {
		LOGGER.info("getSubscriber profile " + subscriberID);
		SubscriberProfileType s = new SubscriberProfileType();
		s.setOperator("o2");
		s.setSegment(SegmentType.CORPORATE);
		s.setPaymentCategory(PaymentCategoryType.POSTPAY);
		return s;
	}
	
	
	

}
