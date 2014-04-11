package mobi.nowtechnologies.server.user.criteria;

import static mobi.nowtechnologies.server.user.criteria.ExpectedValueHolder.valueOf;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ExactMatchStrategy<T> implements MatchStrategy<T> {

    private ExpectedValueHolder<T> second;

    public ExactMatchStrategy(ExpectedValueHolder<T> second) {
        this.second = second;
    }

    @Override
    public boolean match(T first) {
        return first == this.second.getValue() || first!=null && first.equals(this.second.getValue());
    }

    public static <T> ExactMatchStrategy<T> equalTo(ExpectedValueHolder<T> second){
        return new ExactMatchStrategy<T>(second);
    }

    public static <T> ExactMatchStrategy<T> equalTo(T second){
        return new ExactMatchStrategy<T>(valueOf(second));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
