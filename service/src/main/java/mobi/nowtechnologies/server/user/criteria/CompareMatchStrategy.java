package mobi.nowtechnologies.server.user.criteria;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class CompareMatchStrategy implements MatchStrategy<Number> {
    private CompareOperation compareOperation;

    public CompareMatchStrategy(CompareOperation compareOperation) {
        this.compareOperation = compareOperation;
    }

    @Override
    public boolean match(Number first, Number second) {
        return compareOperation.compare(first, second);
    }

    public static CompareMatchStrategy greaterThan(){
        return new CompareMatchStrategy(new CompareOperation("GreaterThan") {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) > 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy greaterOrEqualTo(){
        return new CompareMatchStrategy(new CompareOperation("GreaterOrEqualTo") {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) >= 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy lessThan(){
        return new CompareMatchStrategy(new CompareOperation("LessThan") {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) < 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy lessOrEqualTo(){
        return new CompareMatchStrategy(new CompareOperation("LessOrEqualTo") {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) <= 0 : raiseException(first, second);
                }
            }
        );
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("compareOperation", compareOperation)
                .toString();
    }

    private abstract static class CompareOperation{

        private final String name;

        protected CompareOperation(String name) {
            this.name = name;
        }

        abstract boolean compare(Number first, Number second);

        protected boolean argsCanBeCompared(Number first, Number second){
            return first instanceof Comparable && second instanceof Comparable;
        }

        protected boolean raiseException(Number first, Number second){
            throw new IllegalArgumentException("Arguments are not comparable: [" + first + "] [" + second + "]");
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
