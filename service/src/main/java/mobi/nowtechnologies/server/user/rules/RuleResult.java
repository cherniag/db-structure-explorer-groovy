package mobi.nowtechnologies.server.user.rules;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class RuleResult<T> {

    private final boolean isSuccessful;

    private final T result;

    public RuleResult(boolean isSuccessful, T result) {
        this.isSuccessful = isSuccessful;
        this.result = result;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public T getResult() {
        return result;
    }
}
