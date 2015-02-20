package mobi.nowtechnologies.applicationtests.features.context;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChartBehaviorCaseTransformerTest {
    ChartBehaviorCaseTransformer transformer = new ChartBehaviorCaseTransformer();

    @Test
    public void testTransform() throws Exception {
        Date now = new Date();
        Date freeTrialDate = DateUtils.addDays(now, 3);

        ChartBehaviorCase aCase;

        aCase = transformer.transform("----(Rm)--------(NOW)-----(Re)-------");
        assertEquals(2, aCase.getReferralPeriod(now, null).getAmount());

        aCase = transformer.transform("----(Rm)--------(NOW)----------------");
        assertFalse(aCase.hasReferralPeriod());

        aCase = transformer.transform("----(Rm)-----(NOW)-------(FT)--(Re)--");
        assertTrue(7 <= aCase.getReferralPeriod(now, freeTrialDate).getAmount());

        aCase = transformer.transform("----(Rm)-----(NOW)-------(FT)--------");
        assertFalse(aCase.hasReferralPeriod());

        aCase = transformer.transform("----(Rm)-----(NOW)-(Re)--(FT)--------");
        assertTrue(3 <= aCase.getReferralPeriod(now, freeTrialDate).getAmount());
    }


}