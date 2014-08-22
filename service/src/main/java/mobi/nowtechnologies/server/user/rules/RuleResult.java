package mobi.nowtechnologies.server.user.rules;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class RuleResult<T> {
    public static final RuleResult FAIL_RESULT = new RuleResult(false, null);

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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isSuccessful", isSuccessful)
                .append("result", result)
                .toString();
    }
}
