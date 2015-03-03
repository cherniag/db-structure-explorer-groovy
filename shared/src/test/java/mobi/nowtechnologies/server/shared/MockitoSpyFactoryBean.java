package mobi.nowtechnologies.server.shared;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
public class MockitoSpyFactoryBean implements FactoryBean, InitializingBean {

    private Object realObject;
    private Object realObjectSpy;

    @Override
    public void afterPropertiesSet() throws Exception {
        realObject = getTargetObject(realObject);
        realObjectSpy = spy(realObject);
    }

    protected Object getTargetObject(Object proxy) throws Exception {
        if (AopUtils.isJdkDynamicProxy(proxy) || AopUtils.isCglibProxy(proxy)) {
            return ((Advised) proxy).getTargetSource().getTarget();
        }
        else {
            return proxy;
        }
    }

    public void setRealObject(Object realObject) throws InstantiationException, IllegalAccessException {
        this.realObject = realObject;
    }

    @Override
    public Object getObject() throws Exception {
        return realObjectSpy;
    }

    @Override
    public Class getObjectType() {
        return realObject != null ?
               realObject.getClass() :
               Object.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
