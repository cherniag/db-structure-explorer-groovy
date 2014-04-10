package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.runners.Parameterized.Parameters;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
@RunWith(Parameterized.class)
public class CompareMatchStrategyGreaterThanTest {

    private CompareMatchStrategy compareMatchStrategy = CompareMatchStrategy.greaterThan();

    private String description;
    private Number first;
    private Number second;
    private boolean matchResult;
    private Class<?> exceptionClass;

    public CompareMatchStrategyGreaterThanTest(String description, Number first, Number second, boolean matchResult, Class<?> exceptionClass) {
        this.description = description + " [" + first + "] [" + second + "]";
        this.first = first;
        this.second = second;
        this.matchResult = matchResult;
        this.exceptionClass = exceptionClass;
    }

    @Test
    public void testMatching() throws Exception {
        try {
            assertThat(description, compareMatchStrategy.match(first, second), is(matchResult));
        } catch (Exception e) {
            if (exceptionClass == null || !e.getClass().isAssignableFrom(exceptionClass)) {
                fail("Expected exception " + exceptionClass + " is null or has wrong type. Actual type is " + e.getClass());
            }
        }

    }

    @Parameters
    public static Collection<Object[]> data() {
        return Lists.newArrayList(
                new Object[][]{
                        {"First param should be greater than second for byte", (byte) 127, (byte) 20, true, null},
                        {"First param should not be greater than second for byte", (byte) 10, (byte) 100, false, null},
                        {"First param should be greater than second for short", (short) 100, (short) 20, true, null},
                        {"First param should not be greater than second for short", (short) 5, (short) 15, false, null},
                        {"First param should be greater than second for int", 10, 5, true, null},
                        {"First param should not be greater than second for int", 30, 240, false, null},
                        {"First param should be greater than second for int", -1, -5, true, null},
                        {"First param should be greater than second for int", Integer.MAX_VALUE, Integer.MIN_VALUE, true, null},
                        {"First param should be greater than second for long", 1000L, 999L, true, null},
                        {"First param should be greater than second for float", 7.62F, 5.45F, true, null},
                        {"First param should be greater than second for double", 5.56D, 5.45D, true, null},
                        {"Null can not be compared", null, 5.45D, true, IllegalArgumentException.class},
                        {"Null can not be compared", 5.45D, null, true, IllegalArgumentException.class},
                        {"Null can not be compared", null, null, true, IllegalArgumentException.class},
                }
        );
    }

}

