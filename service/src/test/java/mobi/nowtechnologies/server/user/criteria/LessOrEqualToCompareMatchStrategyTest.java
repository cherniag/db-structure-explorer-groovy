package mobi.nowtechnologies.server.user.criteria;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.lessOrEqualTo;

import java.util.Collection;

import com.google.common.collect.Lists;

import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.runners.Parameterized.*;

/*
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */


@RunWith(Parameterized.class)
public class LessOrEqualToCompareMatchStrategyTest<T extends Number & Comparable<T>> extends AbstractCompareMatchStrategyTest<T> {

    public LessOrEqualToCompareMatchStrategyTest(String description, T first, CompareMatchStrategy<T> second, boolean matchResult, Class<?> exceptionClass) {
        super(description, first, second, matchResult, exceptionClass);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(new Object[][] {
            //byte
            {"First param should be less than second for byte", (byte) 1, lessOrEqualTo((byte) 20), true, null}, {"First param should not be less than second for byte", (byte) 10, lessOrEqualTo(
            (byte) 2), false, null}, {"First param should be equal to second for byte", (byte) 10, lessOrEqualTo(
            (byte) 10), true, null}, {"First param should be less than second for byte", Byte.MIN_VALUE, lessOrEqualTo(
            Byte.MAX_VALUE), true, null}, {"First param should be less than second for byte", Byte.MIN_VALUE, lessOrEqualTo(
            (byte) 0), true, null}, {"First param should not be less than second for byte", Byte.MAX_VALUE, lessOrEqualTo(
            Byte.MIN_VALUE), false, null}, {"First param should not be less than second for byte", Byte.MAX_VALUE, lessOrEqualTo((byte) 0), false, null},
            //short
            {"First param should be less than second for short", (short) 5, lessOrEqualTo((short) 20), true, null}, {"First param should not be less than second for short", (short) 25, lessOrEqualTo(
            (short) 15), false, null}, {"First param should be equal to second for short", (short) 5, lessOrEqualTo(
            (short) 5), true, null}, {"First param should be less than second for short", Short.MIN_VALUE, lessOrEqualTo(
            Short.MAX_VALUE), true, null}, {"First param should be less than second for short", Short.MIN_VALUE, lessOrEqualTo(
            (short) 0), true, null}, {"First param should not be less than second for short", Short.MAX_VALUE, lessOrEqualTo(
            Short.MIN_VALUE), false, null}, {"First param should not be less than second for short", Short.MAX_VALUE, lessOrEqualTo((short) 0), false, null},
            //int
            {"First param should be less than second for int", 10, lessOrEqualTo(15), true, null}, {"First param should not be less than second for int", 330, lessOrEqualTo(
            240), false, null}, {"First param should be equal to second for int", 55, lessOrEqualTo(55), true, null}, {"First param should be equal to second for int", 0, lessOrEqualTo(
            0), true, null}, {"First param should be equal to second for int", -55, lessOrEqualTo(-55), true, null}, {"First param should be less than second for int", -51, lessOrEqualTo(
            -5), true, null}, {"First param should be less than second for int", -10, lessOrEqualTo(0), true, null}, {"First param should be less than second for int", 0, lessOrEqualTo(
            10), true, null}, {"First param should be less than second for int", Integer.MIN_VALUE, lessOrEqualTo(
            Integer.MAX_VALUE), true, null}, {"First param should be less than second for int", Integer.MIN_VALUE, lessOrEqualTo(
            0), true, null}, {"First param should not be less than second for int", Integer.MAX_VALUE, lessOrEqualTo(
            Integer.MIN_VALUE), false, null}, {"First param should not be less than second for int", Integer.MAX_VALUE, lessOrEqualTo(0), false, null},
            //long
            {"First param should be less than second for long", 1000L, lessOrEqualTo(2999L), true, null}, {"First param should not be less than second for long", 1000L, lessOrEqualTo(
            50L), false, null}, {"First param should be equal to second for long", 1000L, lessOrEqualTo(
            1000L), true, null}, {"First param should be less than second for long", Long.MIN_VALUE, lessOrEqualTo(
            Long.MAX_VALUE), true, null}, {"First param should be less than second for long", Long.MIN_VALUE, lessOrEqualTo(
            0L), true, null}, {"First param should not be less than second for long", Long.MAX_VALUE, lessOrEqualTo(
            Long.MIN_VALUE), false, null}, {"First param should not be less than second for long", Long.MAX_VALUE, lessOrEqualTo(0L), false, null},
            //float
            {"First param should be less than second for float", 7.62F, lessOrEqualTo(55.45F), true, null}, {"First param should not be less than second for float", 95.45F, lessOrEqualTo(
            12.7F), false, null}, {"First param should not be less than second for float", Float.MAX_VALUE, lessOrEqualTo(
            Float.MIN_VALUE), false, null}, {"First param should be less than second for float", Float.MIN_VALUE, lessOrEqualTo(
            Float.MAX_VALUE), true, null}, {"First param should be less than second for float", 0F, lessOrEqualTo(Float.MAX_VALUE), true, null},
            //double
            {"First param should be less than second for double", 5.45D, lessOrEqualTo(5.56D), true, null}, {"First param should not be less than second for double", 7.62D, lessOrEqualTo(
            85.45D), true, null}, {"First param should not be less than second for double", Double.MAX_VALUE, lessOrEqualTo(
            Double.MIN_VALUE), false, null}, {"First param should be less than second for double", Double.MIN_VALUE, lessOrEqualTo(
            Double.MAX_VALUE), true, null}, {"First param should be less than second for double", 0D, lessOrEqualTo(Double.MAX_VALUE), true, null},
            //null
            {"Null can not be compared", null, lessOrEqualTo(5.45D), true, IllegalArgumentException.class}, {"Null can not be compared", 5.45D, lessOrEqualTo(
            (Double) null), true, IllegalArgumentException.class}, {"Null can not be compared", null, lessOrEqualTo((Double) null), true, IllegalArgumentException.class},});
    }

}

