package mobi.nowtechnologies.server.user.criteria;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.greaterOrEqualTo;

import java.util.Collection;

import com.google.common.collect.Lists;

import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.runners.Parameterized.*;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */
@RunWith(Parameterized.class)
public class GreaterOrEqualToCompareMatchStrategyTest<T extends Number & Comparable<T>> extends AbstractCompareMatchStrategyTest<T> {

    public GreaterOrEqualToCompareMatchStrategyTest(String description, T first, CompareMatchStrategy<T> second, boolean matchResult, Class<?> exceptionClass) {
        super(description, first, second, matchResult, exceptionClass);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(new Object[][] {{"First param should be greater than second for byte", (byte) 127, greaterOrEqualTo(
                                      (byte) 20), true, null}, {"First param should not be greater than second for byte", (byte) 10, greaterOrEqualTo(
                                      (byte) 100), false, null}, {"First param should be equal to second for byte", (byte) 10, greaterOrEqualTo(
                                      (byte) 10), true, null}, {"First param should be greater than second for byte", Byte.MAX_VALUE, greaterOrEqualTo(
                                      Byte.MIN_VALUE), true, null}, {"First param should be greater than second for byte", Byte.MAX_VALUE, greaterOrEqualTo(
                                      (byte) 0), true, null}, {"First param should not be greater than second for byte", Byte.MIN_VALUE, greaterOrEqualTo(
                                      Byte.MAX_VALUE), false, null}, {"First param should not be greater than second for byte", Byte.MIN_VALUE, greaterOrEqualTo((byte) 0), false, null},
                                      //short
                                      {"First param should be greater than second for short", (short) 100, greaterOrEqualTo(
                                          (short) 20), true, null}, {"First param should not be greater than second for short", (short) 5, greaterOrEqualTo(
                                      (short) 15), false, null}, {"First param should be equal to second for short", (short) 5, greaterOrEqualTo(
                                      (short) 5), true, null}, {"First param should be greater than second for short", Short.MAX_VALUE, greaterOrEqualTo(
                                      Short.MIN_VALUE), true, null}, {"First param should be greater than second for short", Short.MAX_VALUE, greaterOrEqualTo(
                                      (short) 0), true, null}, {"First param should not be greater than second for short", Short.MIN_VALUE, greaterOrEqualTo(
                                      Short.MAX_VALUE), false, null}, {"First param should not be greater than second for short", Short.MIN_VALUE, greaterOrEqualTo((short) 0), false, null},
                                      //int
                                      {"First param should be greater than second for int", 10, greaterOrEqualTo(
                                          5), true, null}, {"First param should not be greater than second for int", 30, greaterOrEqualTo(
                                      240), false, null}, {"First param should be equal to second for int", 55, greaterOrEqualTo(
                                      55), true, null}, {"First param should be equal to second for int", 0, greaterOrEqualTo(
                                      0), true, null}, {"First param should be equal to second for int", -55, greaterOrEqualTo(
                                      -55), true, null}, {"First param should be equal to second for int", -5, greaterOrEqualTo(
                                      -5), true, null}, {"First param should be greater than second for int", -1, greaterOrEqualTo(
                                      -5), true, null}, {"First param should be greater than second for int", 0, greaterOrEqualTo(
                                      -5), true, null}, {"First param should be greater than second for int", 2, greaterOrEqualTo(
                                      0), true, null}, {"First param should be greater than second for int", Integer.MAX_VALUE, greaterOrEqualTo(
                                      Integer.MIN_VALUE), true, null}, {"First param should be greater than second for int", Integer.MAX_VALUE, greaterOrEqualTo(
                                      0), true, null}, {"First param should not be greater than second for int", Integer.MIN_VALUE, greaterOrEqualTo(
                                      Integer.MAX_VALUE), false, null}, {"First param should not be greater than second for int", Integer.MIN_VALUE, greaterOrEqualTo(0), false, null},
                                      //long
                                      {"First param should be greater than second for long", 1000L, greaterOrEqualTo(
                                          999L), true, null}, {"First param should not be greater than second for long", 1000L, greaterOrEqualTo(
                                      50000L), false, null}, {"First param should be equal to second for long", 1000L, greaterOrEqualTo(
                                      1000L), true, null}, {"First param should be greater than second for long", Long.MAX_VALUE, greaterOrEqualTo(
                                      Long.MIN_VALUE), true, null}, {"First param should be greater than second for long", Long.MAX_VALUE, greaterOrEqualTo(
                                      0L), true, null}, {"First param should not be greater than second for long", Long.MIN_VALUE, greaterOrEqualTo(
                                      Long.MAX_VALUE), false, null}, {"First param should not be greater than second for long", Long.MIN_VALUE, greaterOrEqualTo(0L), false, null},
                                      //float
                                      {"First param should be greater than second for float", 7.62F, greaterOrEqualTo(
                                          5.45F), true, null}, {"First param should not be greater than second for float", 5.45F, greaterOrEqualTo(
                                      12.7F), false, null}, {"First param should not be greater than second for float", Float.MIN_VALUE, greaterOrEqualTo(
                                      Float.MAX_VALUE), false, null}, {"First param should be greater than second for float", Float.MAX_VALUE, greaterOrEqualTo(
                                      Float.MIN_VALUE), true, null}, {"First param should be greater than second for float", Float.MAX_VALUE, greaterOrEqualTo(0F), true, null},
                                      //double
                                      {"First param should be greater than second for double", 5.56D, greaterOrEqualTo(
                                          5.45D), true, null}, {"First param should not be greater than second for double", 7.62D, greaterOrEqualTo(
                                      5.45D), true, null}, {"First param should not be greater than second for double", Double.MIN_VALUE, greaterOrEqualTo(
                                      Double.MAX_VALUE), false, null}, {"First param should be greater than second for double", Double.MAX_VALUE, greaterOrEqualTo(
                                      Double.MIN_VALUE), true, null}, {"First param should be greater than second for double", Double.MAX_VALUE, greaterOrEqualTo(0D), true, null},
                                      //null
                                      {"Null can not be compared", null, greaterOrEqualTo(5.45D), true, IllegalArgumentException.class}, {"Null can not be compared", 5.45D, greaterOrEqualTo(
                                      (Double) null), true, IllegalArgumentException.class}, {"Null can not be compared", null, greaterOrEqualTo(
                                      (Double) null), true, IllegalArgumentException.class},});
    }

}

