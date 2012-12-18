package mobi.nowtechnologies.server.shared;

import static org.mockito.Mockito.spy;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

@SuppressWarnings("rawtypes")
public class MockitoSpyFactoryBean implements FactoryBean, InitializingBean {
	private Object realObject;

	@Override
	public void afterPropertiesSet() throws Exception {
		realObject = getTargetObject(realObject);
	}

	protected Object getTargetObject(Object proxy) throws Exception {
		if (AopUtils.isJdkDynamicProxy(proxy) || AopUtils.isCglibProxy(proxy)) {
			return ((Advised) proxy).getTargetSource().getTarget();
		} else {
			return proxy;
		}
	}

	public void setRealObject(Object realObject) throws InstantiationException, IllegalAccessException {
		this.realObject = realObject;
	}

	@Override
	public Object getObject() throws Exception {
		return spy(realObject);
	}

	@Override
	public Class getObjectType() {
		return realObject.getClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
