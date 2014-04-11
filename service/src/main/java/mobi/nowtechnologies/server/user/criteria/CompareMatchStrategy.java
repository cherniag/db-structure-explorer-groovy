package mobi.nowtechnologies.server.user.criteria;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class CompareMatchStrategy<T extends Number & Comparable<T>> implements MatchStrategy<T> {
    private CompareOperation<T> compareOperation;

    public CompareMatchStrategy(CompareOperation<T> compareOperation) {
        this.compareOperation = compareOperation;
    }

    @Override
    public boolean match(T first, T second) {
        return compareOperation.compare(first, second);
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterThan(){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("GreaterThan") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) > 0;
                }
            }
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> greaterOrEqualTo(){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("GreaterOrEqualTo") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) >= 0;
                }
            }
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessThan(){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("LessThan") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) < 0;
                }
            }
        );
    }

    public static <T extends Number & Comparable<T>> CompareMatchStrategy<T> lessOrEqualTo(){
        return new CompareMatchStrategy<T>(new CompareOperation<T>("LessOrEqualTo") {
                @Override
                public boolean compare(T first, T second) {
                    return getCompareResult(first, second) <= 0;
                }
            }
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("compareOperation", compareOperation)
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
