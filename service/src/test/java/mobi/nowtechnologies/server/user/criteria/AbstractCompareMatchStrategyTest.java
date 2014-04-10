package mobi.nowtechnologies.server.user.criteria;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class AbstractCompareMatchStrategyTest {
    protected String description;
    protected Number first;
    private CompareMatchStrategy matchStrategy;
    protected Number second;
    protected boolean matchResult;
    protected Class<?> exceptionClass;

    public AbstractCompareMatchStrategyTest(String description, Number first, Number second, boolean matchResult, Class<?> exceptionClass, CompareMatchStrategy matchStrategy) {
        this.matchResult = matchResult;
        this.exceptionClass = exceptionClass;
        this.first = first;
        this.matchStrategy = matchStrategy;
        this.description = description + " [" + first + "] [" + second + "]";
        this.second = second;
    }

    @Test
    public void testMatching() throws Exception {
        try {
            assertThat(description, matchStrategy.match(first, second), is(matchResult));
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
