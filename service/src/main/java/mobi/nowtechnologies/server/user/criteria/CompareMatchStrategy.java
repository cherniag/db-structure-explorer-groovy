package mobi.nowtechnologies.server.user.criteria;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.valueOf;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class CompareMatchStrategy<T extends Number & Comparable<T>> implements MatchStrategy<T> {
    private CompareOperation<T> compareOperation;
    private ExpectedValueHolder<T> second;

    private CompareMatchStrategy(CompareOperation<T> compareOperation, ExpectedValueHolder<T> second) {
        this.compareOperation = compareOperation;
        this.second = second;
    }

    @Override
    public boolean match(T first) {
        return compareOperation.compare(first, this.second.getValue());
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterThan(ExpectedValueHolder<T> second){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("GreaterThan") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) > 0;
                }
            }, second
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterThan(T second){
        return greaterThan(valueOf(second));
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterOrEqualTo(ExpectedValueHolder<T> second){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("GreaterOrEqualTo") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) >= 0;
                }
            }, second
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterOrEqualTo(T second){
        return greaterOrEqualTo(valueOf(second));
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessThan(ExpectedValueHolder<T> second){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("LessThan") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) < 0;
                }
            }, second
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessThan(T second){
        return lessThan(valueOf(second));
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessOrEqualTo(ExpectedValueHolder<T> second){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("LessOrEqualTo") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) <= 0;
                }
            }, second
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessOrEqualTo(T second){
        return lessOrEqualTo(valueOf(second));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("compareOperation", compareOperation)
                .append(" ", second)
                .toString();
    }

    private abstract static class CompareOperation<T extends Number & Comparable<T>>{

        private final String name;

        protected CompareOperation(String name) {
            this.name = name;
        }

        abstract boolean compare(T first, T second);

        protected int getCompareResult(T first, T second){
            Assert.notNull(first);
            Assert.notNull(second);
            return first.compareTo(second);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
