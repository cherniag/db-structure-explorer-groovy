package mobi.nowtechnologies.server.persistence.domain;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static junit.framework.Assert.assertTrue;

public class NZSubscriberInfoTest{

    @Test
    public void testUpdateTimestamp() throws Exception {
        NZSubscriberInfo subscriberInfo = new NZSubscriberInfo("6412343215");

        Date updateTimestamp = setAndReturnUpdateTimestampInThePast(subscriberInfo);
        subscriberInfo.setPayIndicator("Prepay");
        checkUpdateTimestampHasBeenRefreshed(subscriberInfo, updateTimestamp);

        updateTimestamp = setAndReturnUpdateTimestampInThePast(subscriberInfo);
        subscriberInfo.setProviderName("Vodafone");
        checkUpdateTimestampHasBeenRefreshed(subscriberInfo, updateTimestamp);

        updateTimestamp = setAndReturnUpdateTimestampInThePast(subscriberInfo);
        subscriberInfo.setBillingAccountNumber("12345");
        checkUpdateTimestampHasBeenRefreshed(subscriberInfo, updateTimestamp);

        updateTimestamp = setAndReturnUpdateTimestampInThePast(subscriberInfo);
        subscriberInfo.setBillingAccountName("C-234-14");
        checkUpdateTimestampHasBeenRefreshed(subscriberInfo, updateTimestamp);
    }

    private Date setAndReturnUpdateTimestampInThePast(NZSubscriberInfo subscriberInfo) {
        Date updateTimestamp = DateUtils.addSeconds(new Date(), -2);
        ReflectionTestUtils.setField(subscriberInfo, "updateTimestamp", updateTimestamp);
        return updateTimestamp;
    }

    private void checkUpdateTimestampHasBeenRefreshed(NZSubscriberInfo subscriberInfo, Date oldUpdateTimestamp) {
        Date updateTimestamp = (Date)ReflectionTestUtils.getField(subscriberInfo, "updateTimestamp");
        assertTrue(updateTimestamp.after(oldUpdateTimestamp));
    }

}