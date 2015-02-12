package mobi.nowtechnologies.server.web.model;

import mobi.nowtechnologies.server.persistence.domain.Community;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

public class CommunityServiceFactory implements BeanFactoryAware {
    private BeanFactory beanFactory;

    public <T> T find(Community community, Class<T> serviceType) {
        String beanName = getBeanName(community, serviceType);
        try {
            return beanFactory.getBean(beanName, serviceType);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private <T> String getBeanName(Community community, Class<T> serviceType) {
        String rewriteUrlParameter = community.getRewriteUrlParameter();
        return rewriteUrlParameter + "." + serviceType.getSimpleName();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
