package mobi.nowtechnologies.server.user.criteria;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class CompareMatchStrategyGreaterThanTest {

    private CompareMatchStrategy compareMatchStrategy = CompareMatchStrategy.greaterThan();

    @Test
    public void testFirstGreaterThanSecondForByte() throws Exception {
        byte first = 127;
        byte second = 20;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForShort() throws Exception {
        short first = 1000;
        short second = 50;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForInt() throws Exception {
        int first = 10;
        int second = 5;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForIntNegative() throws Exception {
        int first = -1;
        int second = -10;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForIntMix() throws Exception {
        int first = 0;
        int second = -10;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForIntBorder() throws Exception {
        int first = Integer.MAX_VALUE;
        int second = Integer.MIN_VALUE;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForLong() throws Exception {
        long first = 10L;
        long second = 5L;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testSecondGreaterThanFirstForLong() throws Exception {
        long first = 5L;
        long second = 10L;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testFirstGreaterThanSecondForFloat() throws Exception {
        float first = 7.62F;
        float second = 5.45F;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testSecondGreaterThanFirstForFloat() throws Exception {
        float first = 5.45F;
        float second = 7.62F;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(false));
    }

    @Test
    public void testFirstGreaterThanSecondForDouble() throws Exception {
        double first = 5.56D;
        double second = 5.45D;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(true));
    }

    @Test
    public void testSecondGreaterThanFirstForDouble() throws Exception {
        double first = 5.45D;
        double second = 5.56D;
        boolean match = compareMatchStrategy.match(first, second);
        assertThat(match, is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFirstGreaterThanSecondForNull() throws Exception {
        double second = 5.45D;
        boolean match = compareMatchStrategy.match(null, second);
        assertThat(match, is(true));
    }




    /*@Test
    public void testGreaterOrEqualTo() throws Exception {
        compareMatchStrategy= CompareMatchStrategy.greaterOrEqualTo();
        boolean match = compareMatchStrategy.match(10, 5);
        assertThat(match, is(true));
    }

    @Test
    public void testLessThan() throws Exception {
        compareMatchStrategy= CompareMatchStrategy.lessThan();
        boolean match = compareMatchStrategy.match(5, 10);
        assertThat(match, is(true));
    }

    @Test
    public void testLessOrEqualTo() throws Exception {
        compareMatchStrategy= CompareMatchStrategy.lessOrEqualTo();
        boolean match = compareMatchStrategy.match(5, 10);
        assertThat(match, is(true));
    }*/
}
