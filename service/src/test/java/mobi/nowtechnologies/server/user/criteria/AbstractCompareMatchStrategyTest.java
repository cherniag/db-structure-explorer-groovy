package mobi.nowtechnologies.server.user.criteria;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */
public abstract class AbstractCompareMatchStrategyTest<T extends Number & Comparable<T>> {

    protected String description;
    protected T first;
    protected boolean matchResult;
    protected Class<?> exceptionClass;
    private CompareMatchStrategy<T> matchStrategy;

    public AbstractCompareMatchStrategyTest(String description, T first, CompareMatchStrategy<T> second, boolean matchResult, Class<?> exceptionClass) {
        this.matchResult = matchResult;
        this.exceptionClass = exceptionClass;
        this.first = first;
        this.matchStrategy = second;
        this.description = description + " [" + first + "] [" + second + "]";
    }

    @Test
    public void testMatching() throws Exception {
        try {
            assertThat(description, matchStrategy.match(first), is(matchResult));
        } catch (Exception e) {
            if (exceptionClass == null) {
                fail("Unexpected exception " + e.getClass());
            }
            if (!exceptionClass.isAssignableFrom(e.getClass())) {
                fail("Expected exception " + exceptionClass + " has wrong type. Actual type is " + e.getClass());
            }
        }

    }
}
