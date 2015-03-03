package cucumber.custom;

import cucumber.runtime.java.ObjectFactory;

import org.springframework.beans.factory.BeanFactory;

public class SpringObjectFactory implements ObjectFactory {

    private final BeanFactory context;

    public SpringObjectFactory(BeanFactory context) {
        this.context = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void addClass(Class<?> glueClass) {

    }

    @Override
    public <T> T getInstance(Class<T> glueClass) {
        return context.getBean(glueClass);
    }
}
