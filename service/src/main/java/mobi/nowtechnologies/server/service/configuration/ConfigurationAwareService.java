package mobi.nowtechnologies.server.service.configuration;

import mobi.nowtechnologies.server.user.rules.RuleServiceSupport;
import mobi.nowtechnologies.server.user.rules.TriggerType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ConfigurationAwareService<T extends TriggerType, Value> implements InitializingBean{

    private Configuration<T, Value,? extends RuleBuilder<?, Value> > configuration;
    private RuleServiceSupport<T> ruleServiceSupport;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(configuration, "Configuration should be set.");
        ruleServiceSupport = configuration.get();
        Assert.notNull(ruleServiceSupport, "Configuration should be not null.");
    }

    public RuleServiceSupport<T> getRuleServiceSupport() {
        return ruleServiceSupport;
    }

    public Configuration<T, Value,?  extends  RuleBuilder<?, Value> > getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration<T, Value, ? extends RuleBuilder<?, Value>> configuration) {
        this.configuration = configuration;
    }

    //TODO: get rid of this, introduced for test purpose
    protected void setRuleServiceSupport(RuleServiceSupport ruleServiceSupport) {
        this.ruleServiceSupport = ruleServiceSupport;
    }

}
