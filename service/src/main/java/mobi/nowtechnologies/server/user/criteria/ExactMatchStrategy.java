package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
public class ExactMatchStrategy<T> implements MatchStrategy<T> {

    @Override
    public boolean match(T first, T second) {
        return first == second || first!=null && first.equals(second);
    }

    public static <T> ExactMatchStrategy<T> equalTo(){
        return new ExactMatchStrategy<T>();
    }
}
