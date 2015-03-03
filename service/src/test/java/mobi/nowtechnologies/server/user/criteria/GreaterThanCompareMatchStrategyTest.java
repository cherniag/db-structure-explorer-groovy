package mobi.nowtechnologies.server.user.criteria;

import static mobi.nowtechnologies.server.user.criteria.CompareMatchStrategy.greaterThan;

import java.util.Collection;

import com.google.common.collect.Lists;

import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.runners.Parameterized.*;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */
@RunWith(Parameterized.class)
public class GreaterThanCompareMatchStrategyTest<T extends Number & Comparable<T>> extends AbstractCompareMatchStrategyTest<T> {

    public GreaterThanCompareMatchStrategyTest(String description, T first, CompareMatchStrategy<T> second, boolean matchResult, Class<?> exceptionClass) {
        super(description, first, second, matchResult, exceptionClass);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(new Object[][] {
                                      //byte
                                      {"First param should be greater than second for byte", (byte) 127, greaterThan(
                                          (byte) 20), true, null}, {"First param should not be greater than second for byte", (byte) 10, greaterThan(
                                      (byte) 100), false, null}, {"First param should not be equal to second for byte", (byte) 10, greaterThan(
                                      (byte) 10), false, null}, {"First param should be greater than second for byte", Byte.MAX_VALUE, greaterThan(
                                      Byte.MIN_VALUE), true, null}, {"First param should be greater than second for byte", Byte.MAX_VALUE, greaterThan(
                                      (byte) 0), true, null}, {"First param should not be greater than second for byte", Byte.MIN_VALUE, greaterThan(
                                      Byte.MAX_VALUE), false, null}, {"First param should not be greater than second for byte", Byte.MIN_VALUE, greaterThan((byte) 0), false, null},
                                      //short
                                      {"First param should be greater than second for short", (short) 100, greaterThan(
                                          (short) 20), true, null}, {"First param should not be greater than second for short", (short) 5, greaterThan(
                                      (short) 15), false, null}, {"First param should not be equal to second for short", (short) 5, greaterThan(
                                      (short) 5), false, null}, {"First param should be greater than second for short", Short.MAX_VALUE, greaterThan(
                                      Short.MIN_VALUE), true, null}, {"First param should be greater than second for short", Short.MAX_VALUE, greaterThan(
                                      (short) 0), true, null}, {"First param should not be greater than second for short", Short.MIN_VALUE, greaterThan(
                                      Short.MAX_VALUE), false, null}, {"First param should not be greater than second for short", Short.MIN_VALUE, greaterThan((short) 0), false, null},
                                      //int
                                      {"First param should be greater than second for int", 10, greaterThan(5), true, null}, {"First param should not be greater than second for int", 30, greaterThan(
                                      240), false, null}, {"First param should not be equal to second for int", 55, greaterThan(
                                      55), false, null}, {"First param should not be equal to second for int", 0, greaterThan(
                                      0), false, null}, {"First param should not be equal to second for int", -55, greaterThan(
                                      -55), false, null}, {"First param should be greater than second for int", -1, greaterThan(
                                      -5), true, null}, {"First param should be greater than second for int", 0, greaterThan(
                                      -5), true, null}, {"First param should be greater than second for int", 2, greaterThan(
                                      0), true, null}, {"First param should be greater than second for int", Integer.MAX_VALUE, greaterThan(
                                      Integer.MIN_VALUE), true, null}, {"First param should be greater than second for int", Integer.MAX_VALUE, greaterThan(
                                      0), true, null}, {"First param should not be greater than second for int", Integer.MIN_VALUE, greaterThan(
                                      Integer.MAX_VALUE), false, null}, {"First param should not be greater than second for int", Integer.MIN_VALUE, greaterThan(0), false, null},
                                      //long
                                      {"First param should be greater than second for long", 1000L, greaterThan(
                                          999L), true, null}, {"First param should not be greater than second for long", 1000L, greaterThan(
                                      50000L), false, null}, {"First param should not be equal to second for long", 1000L, greaterThan(
                                      1000L), false, null}, {"First param should be greater than second for long", Long.MAX_VALUE, greaterThan(
                                      Long.MIN_VALUE), true, null}, {"First param should be greater than second for long", Long.MAX_VALUE, greaterThan(
                                      0L), true, null}, {"First param should not be greater than second for long", Long.MIN_VALUE, greaterThan(
                                      Long.MAX_VALUE), false, null}, {"First param should not be greater than second for long", Long.MIN_VALUE, greaterThan(0L), false, null},
                                      //float
                                      {"First param should be greater than second for float", 7.62F, greaterThan(
                                          5.45F), true, null}, {"First param should not be greater than second for float", 5.45F, greaterThan(
                                      12.7F), false, null}, {"First param should not be greater than second for float", Float.MIN_VALUE, greaterThan(
                                      Float.MAX_VALUE), false, null}, {"First param should be greater than second for float", Float.MAX_VALUE, greaterThan(
                                      Float.MIN_VALUE), true, null}, {"First param should be greater than second for float", Float.MAX_VALUE, greaterThan(0F), true, null},
                                      //double
                                      {"First param should be greater than second for double", 5.56D, greaterThan(
                                          5.45D), true, null}, {"First param should not be greater than second for double", 7.62D, greaterThan(
                                      5.45D), true, null}, {"First param should not be greater than second for double", Double.MIN_VALUE, greaterThan(
                                      Double.MAX_VALUE), false, null}, {"First param should be greater than second for double", Double.MAX_VALUE, greaterThan(
                                      Double.MIN_VALUE), true, null}, {"First param should be greater than second for double", Double.MAX_VALUE, greaterThan(0D), true, null},
                                      //null
                                      {"Null can not be compared", null, greaterThan(5.45D), true, IllegalArgumentException.class}, {"Null can not be compared", 5.45D, greaterThan(
                                      (Double) null), true, IllegalArgumentException.class}, {"Null can not be compared", null, greaterThan((Double) null), true, IllegalArgumentException.class},});
    }

}

