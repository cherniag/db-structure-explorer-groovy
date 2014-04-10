package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class AndMatcher<T> implements Matcher<T> {

    private Iterable<Matcher> matchers;

    public AndMatcher(Iterable<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean match(T value){
        for (Matcher matcher : matchers) {
            if(!matcher.match(value)){
                return false;
            }
        }
        return true;
    }
}
