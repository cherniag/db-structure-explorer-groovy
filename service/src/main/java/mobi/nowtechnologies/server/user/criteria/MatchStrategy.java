package mobi.nowtechnologies.server.user.criteria;

/**
 * Author: Gennadii Cherniaiev Date: 4/10/2014
 */
public interface MatchStrategy<T> {

    boolean match(T first);
}
