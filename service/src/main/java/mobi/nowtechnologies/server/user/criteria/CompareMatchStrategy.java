package mobi.nowtechnologies.server.user.criteria;

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
        return new CompareMatchStrategy(new CompareOperation() {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) > 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy greaterOrEqualTo(){
        return new CompareMatchStrategy(new CompareOperation() {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) >= 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy lessThan(){
        return new CompareMatchStrategy(new CompareOperation() {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) < 0 : raiseException(first, second);
                }
            }
        );
    }

    public static CompareMatchStrategy lessOrEqualTo(){
        return new CompareMatchStrategy(new CompareOperation() {
                @Override
                public boolean compare(Number first, Number second) {
                    return argsCanBeCompared(first, second) ? ((Comparable)first).compareTo(second) <= 0 : raiseException(first, second);
                }
            }
        );
    }

    private abstract static class CompareOperation{

        abstract boolean compare(Number first, Number second);

        protected boolean argsCanBeCompared(Number first, Number second){
            return first instanceof Comparable && second instanceof Comparable;
        }

        protected boolean raiseException(Number first, Number second){
            throw new IllegalArgumentException("Arguments are not comparable: [" + first + "] [" + second + "]");
        }
    }
}
