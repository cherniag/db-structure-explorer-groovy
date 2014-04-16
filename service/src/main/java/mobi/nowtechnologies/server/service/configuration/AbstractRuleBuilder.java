package mobi.nowtechnologies.server.service.configuration;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.user.criteria.Matcher;
import org.springframework.util.Assert;

public abstract class AbstractRuleBuilder<T, V>  implements RuleBuilder<T,V> {

    private V result;
    private Matcher<User> userMatcher;

    final public AbstractRuleBuilder<T,V> match(Matcher<User> matcher){
        Assert.isNull(this.userMatcher);
        this.userMatcher = matcher;
        return this;
    };

    final public void result(V result){
        Assert.isNull(this.result);
        this.result = result;
    };

    protected V getResult() {
        Assert.notNull(result);
        return result;
    }

    protected Matcher<User> getUserMatcher() {
        Assert.notNull(userMatcher);
        return userMatcher;
    }
}
