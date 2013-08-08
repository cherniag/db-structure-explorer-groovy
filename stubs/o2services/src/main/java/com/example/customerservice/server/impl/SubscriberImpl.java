package com.example.customerservice.server.impl;

import uk.co.o2.soa.coredata_1.PaymentCategoryType;
import uk.co.o2.soa.coredata_1.SegmentType;
import uk.co.o2.soa.subscriberdata_2.SubscriberProfileType;

public class SubscriberImpl {

	public SubscriberProfileType getSubscriberProfileInternal(String subscriberID) {
		System.err.println("getSubscriber profile "+subscriberID);
		SubscriberProfileType s=new SubscriberProfileType();
		s.setOperator("o2");
		s.setSegment(SegmentType.CONSUMER);
        s.setPaymentCategory(PaymentCategoryType.POSTPAY);
		return s;
	}


}
