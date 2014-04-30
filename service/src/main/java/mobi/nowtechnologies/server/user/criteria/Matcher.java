package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public interface Matcher<T> {
    boolean match(T value);
}
