package mobi.nowtechnologies.server.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletResponseMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;

/**
 * EntityController
 * 
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
public class WebConfigPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private ConfigurableBeanFactory beanFactory;

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    	 if (bean instanceof RequestMappingHandlerAdapter) {
    		 RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
             
    		 initRequestMappingHandlerAdapter(adapter);
         } else if(bean instanceof AnnotationMethodHandlerAdapter){
        	 AnnotationMethodHandlerAdapter adapter = (AnnotationMethodHandlerAdapter) bean;
             
    		 initAnnotationMethodHandlerAdapter(adapter);
         }
         return bean;
    }

    private void initAnnotationMethodHandlerAdapter(AnnotationMethodHandlerAdapter adapter) {
    	List<WebArgumentResolver> resolvers = new ArrayList<WebArgumentResolver>();
    	
    	resolvers.add(new PageableArgumentResolver());
		
    	adapter.setCustomArgumentResolvers(resolvers.toArray(new WebArgumentResolver[resolvers.size()]));
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
    private void initRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter) {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new ServletModelAttributeMethodProcessor(false));
		resolvers.add(new HttpRequestResponseBodyMethodProcessor(adapter.getMessageConverters()));
		resolvers.add(new RequestPartMethodArgumentResolver(adapter.getMessageConverters()));
		resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new RequestHeaderMapMethodArgumentResolver());
		resolvers.add(new ServletCookieValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));

		// Type-based argument resolution
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		resolvers.add(new HttpEntityMethodProcessor(adapter.getMessageConverters()));
		resolvers.add(new RedirectAttributesMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());
		resolvers.add(new MapMethodProcessor());
		resolvers.add(new ErrorsMethodArgumentResolver());
		resolvers.add(new SessionStatusMethodArgumentResolver());
		resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
		resolvers.add(new ServletModelAttributeMethodProcessor(true));

		adapter.setArgumentResolvers(resolvers);
	}
    
    public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Return the owning factory of this bean instance, or {@code null}. 
	 */
	protected ConfigurableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}
}