package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.runners.Parameterized.Parameters;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
@RunWith(Parameterized.class)
public class LessOrEqualToCompareMatchStrategyTest extends AbstractCompareMatchStrategyTest {

    public LessOrEqualToCompareMatchStrategyTest(String description, Number first, Number second, boolean matchResult, Class<?> exceptionClass) {
        super(description, first, second, matchResult, exceptionClass, CompareMatchStrategy.lessOrEqualTo());
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(
                new Object[][]{
                        //byte
                        {"First param should be less than second for byte", (byte)1, (byte)20, true, null},
                        {"First param should not be less than second for byte", (byte)10, (byte)2, false, null},
                        {"First param should be equal to second for byte", (byte)10, (byte)10, true, null},
                        {"First param should be less than second for byte", Byte.MIN_VALUE, Byte.MAX_VALUE, true, null},
                        {"First param should be less than second for byte", Byte.MIN_VALUE, (byte)0, true, null},
                        {"First param should not be less than second for byte", Byte.MAX_VALUE, Byte.MIN_VALUE, false, null},
                        {"First param should not be less than second for byte", Byte.MAX_VALUE, (byte)0, false, null},
                        //short
                        {"First param should be less than second for short", (short)5, (short)20, true, null},
                        {"First param should not be less than second for short", (short)25, (short)15, false, null},
                        {"First param should be equal to second for short", (short)5, (short)5, true, null},
                        {"First param should be less than second for short", Short.MIN_VALUE, Short.MAX_VALUE, true, null},
                        {"First param should be less than second for short", Short.MIN_VALUE, (short)0, true, null},
                        {"First param should not be less than second for short", Short.MAX_VALUE, Short.MIN_VALUE, false, null},
                        {"First param should not be less than second for short", Short.MAX_VALUE, (short)0, false, null},
                        //int
                        {"First param should be less than second for int", 10, 15, true, null},
                        {"First param should not be less than second for int", 330, 240, false, null},
                        {"First param should be equal to second for int", 55, 55, true, null},
                        {"First param should be equal to second for int", 0, 0, true, null},
                        {"First param should be equal to second for int", -55, -55, true, null},
                        {"First param should be less than second for int", -51, -5, true, null},
                        {"First param should be less than second for int", -10, 0, true, null},
                        {"First param should be less than second for int", 0, 10, true, null},
                        {"First param should be less than second for int", Integer.MIN_VALUE, Integer.MAX_VALUE, true, null},
                        {"First param should be less than second for int", Integer.MIN_VALUE, 0, true, null},
                        {"First param should not be less than second for int", Integer.MAX_VALUE, Integer.MIN_VALUE, false, null},
                        {"First param should not be less than second for int", Integer.MAX_VALUE, 0, false, null},
                        //long
                        {"First param should be less than second for long", 1000L, 2999L, true, null},
                        {"First param should not be less than second for long", 1000L, 50L, false, null},
                        {"First param should be equal to second for long", 1000L, 1000L, true, null},
                        {"First param should be less than second for long", Long.MIN_VALUE, Long.MAX_VALUE, true, null},
                        {"First param should be less than second for long", Long.MIN_VALUE, 0L, true, null},
                        {"First param should not be less than second for long", Long.MAX_VALUE, Long.MIN_VALUE, false, null},
                        {"First param should not be less than second for long", Long.MAX_VALUE, 0L, false, null},
                        //float
                        {"First param should be less than second for float", 7.62F, 55.45F, true, null},
                        {"First param should not be less than second for float", 95.45F, 12.7F, false, null},
                        {"First param should not be less than second for float", Float.MAX_VALUE, Float.MIN_VALUE, false, null},
                        {"First param should be less than second for float", Float.MIN_VALUE, Float.MAX_VALUE, true, null},
                        {"First param should be less than second for float", 0F, Float.MAX_VALUE, true, null},
                        //double
                        {"First param should be less than second for double", 5.45D, 5.56D, true, null},
                        {"First param should not be less than second for double", 7.62D, 85.45D, true, null},
                        {"First param should not be less than second for double", Double.MAX_VALUE, Double.MIN_VALUE, false, null},
                        {"First param should be less than second for double", Double.MIN_VALUE, Double.MAX_VALUE, true, null},
                        {"First param should be less than second for double", 0D, Double.MAX_VALUE, true, null},
                        //null
                        {"Null can not be compared", null, 5.45D, true, IllegalArgumentException.class},
                        {"Null can not be compared", 5.45D, null, true, IllegalArgumentException.class},
                        {"Null can not be compared", null, null, true, IllegalArgumentException.class},
                }
        );
    }

}

