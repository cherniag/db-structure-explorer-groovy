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

    @Override
    public String toString() {
        return new StringBuilder()
                .append("equalTo(")
                .append(second)
                .append(")")
        .toString();
    }
}
