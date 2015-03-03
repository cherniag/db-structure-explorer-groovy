package mobi.nowtechnologies.server.user.criteria;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.lessThan;

import java.util.Collection;

import com.google.common.collect.Lists;

import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.runners.Parameterized.*;


/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */

@RunWith(Parameterized.class)
public class LessThanCompareMatchStrategyTest<T extends Number & Comparable<T>> extends AbstractCompareMatchStrategyTest<T> {

    public LessThanCompareMatchStrategyTest(String description, T first, CompareMatchStrategy<T> second, boolean matchResult, Class<?> exceptionClass) {
        super(description, first, second, matchResult, exceptionClass);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(new Object[][] {
            //byte
            {"First param should be less than second for byte", (byte) 1, lessThan((byte) 20), true, null}, {"First param should not be less than second for byte", (byte) 10, lessThan(
            (byte) 2), false, null}, {"First param should not be equal to second for byte", (byte) 10, lessThan(
            (byte) 10), false, null}, {"First param should be less than second for byte", Byte.MIN_VALUE, lessThan(
            Byte.MAX_VALUE), true, null}, {"First param should be less than second for byte", Byte.MIN_VALUE, lessThan(
            (byte) 0), true, null}, {"First param should not be less than second for byte", Byte.MAX_VALUE, lessThan(
            Byte.MIN_VALUE), false, null}, {"First param should not be less than second for byte", Byte.MAX_VALUE, lessThan((byte) 0), false, null},
            //short
            {"First param should be less than second for short", (short) 5, lessThan((short) 20), true, null}, {"First param should not be less than second for short", (short) 25, lessThan(
            (short) 15), false, null}, {"First param should not be equal to second for short", (short) 5, lessThan(
            (short) 5), false, null}, {"First param should be less than second for short", Short.MIN_VALUE, lessThan(
            Short.MAX_VALUE), true, null}, {"First param should be less than second for short", Short.MIN_VALUE, lessThan(
            (short) 0), true, null}, {"First param should not be less than second for short", Short.MAX_VALUE, lessThan(
            Short.MIN_VALUE), false, null}, {"First param should not be less than second for short", Short.MAX_VALUE, lessThan((short) 0), false, null},
            //int
            {"First param should be less than second for int", 10, lessThan(15), true, null}, {"First param should not be less than second for int", 330, lessThan(
            240), false, null}, {"First param should not be equal to second for int", 55, lessThan(55), false, null}, {"First param should not be equal to second for int", 0, lessThan(
            0), false, null}, {"First param should not be equal to second for int", -55, lessThan(-55), false, null}, {"First param should be less than second for int", -51, lessThan(
            -5), true, null}, {"First param should be less than second for int", -10, lessThan(0), true, null}, {"First param should be less than second for int", 0, lessThan(
            10), true, null}, {"First param should be less than second for int", Integer.MIN_VALUE, lessThan(
            Integer.MAX_VALUE), true, null}, {"First param should be less than second for int", Integer.MIN_VALUE, lessThan(
            0), true, null}, {"First param should not be less than second for int", Integer.MAX_VALUE, lessThan(
            Integer.MIN_VALUE), false, null}, {"First param should not be less than second for int", Integer.MAX_VALUE, lessThan(0), false, null},
            //long
            {"First param should be less than second for long", 1000L, lessThan(2999L), true, null}, {"First param should not be less than second for long", 1000L, lessThan(
            50L), false, null}, {"First param should not be equal to second for long", 1000L, lessThan(
            1000L), false, null}, {"First param should be less than second for long", Long.MIN_VALUE, lessThan(
            Long.MAX_VALUE), true, null}, {"First param should be less than second for long", Long.MIN_VALUE, lessThan(
            0L), true, null}, {"First param should not be less than second for long", Long.MAX_VALUE, lessThan(
            Long.MIN_VALUE), false, null}, {"First param should not be less than second for long", Long.MAX_VALUE, lessThan(0L), false, null},
            //float
            {"First param should be less than second for float", 7.62F, lessThan(55.45F), true, null}, {"First param should not be less than second for float", 95.45F, lessThan(
            12.7F), false, null}, {"First param should not be less than second for float", Float.MAX_VALUE, lessThan(
            Float.MIN_VALUE), false, null}, {"First param should be less than second for float", Float.MIN_VALUE, lessThan(
            Float.MAX_VALUE), true, null}, {"First param should be less than second for float", 0F, lessThan(Float.MAX_VALUE), true, null},
            //double
            {"First param should be less than second for double", 5.45D, lessThan(5.56D), true, null}, {"First param should not be less than second for double", 7.62D, lessThan(
            85.45D), true, null}, {"First param should not be less than second for double", Double.MAX_VALUE, lessThan(
            Double.MIN_VALUE), false, null}, {"First param should be less than second for double", Double.MIN_VALUE, lessThan(
            Double.MAX_VALUE), true, null}, {"First param should be less than second for double", 0D, lessThan(Double.MAX_VALUE), true, null},
            //null
            {"Null can not be compared", null, lessThan(5.45D), true, IllegalArgumentException.class}, {"Null can not be compared", 5.45D, lessThan(
            (Double) null), true, IllegalArgumentException.class}, {"Null can not be compared", null, lessThan((Double) null), true, IllegalArgumentException.class},});
    }

}


