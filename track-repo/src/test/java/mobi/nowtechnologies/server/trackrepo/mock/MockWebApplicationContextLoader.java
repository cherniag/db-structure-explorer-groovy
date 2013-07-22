package mobi.nowtechnologies.server.trackrepo.mock;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Titov Mykhaylo (titov)
 *
 * A Spring {@link org.springframework.test.context.ContextLoader} that establishes a mock Servlet environment and {@link org.springframework.web.context.WebApplicationContext}
 * so that Spring MVC stacks can be tested from within JUnit.
 */
public class MockWebApplicationContextLoader extends AbstractContextLoader {
        /**
         * The configuration defined in the {@link MockWebApplication} annotation.
         */
        private MockWebApplication configuration;
        
        protected BeanDefinitionReader createBeanDefinitionReader(final GenericApplicationContext context) {
            return new XmlBeanDefinitionReader(context);
        }
        
        @Override
        @SuppressWarnings("serial")
        public ApplicationContext loadContext(String... locations) throws Exception {
        	String basePath=configuration.webapp();
        	File path = ResourceUtils.getFile(basePath);
        	if (!path.exists()) throw new FileNotFoundException("base path does not exist; " + basePath);
        	basePath = "file:" + path.getAbsolutePath();
        	
                // Establish the servlet context and config based on the test class's MockWebApplication annotation.
                final MockServletContext mockServletContext = new MockServletContext(basePath, new FileSystemResourceLoader());
                final MockServletConfig servletConfig = new MockServletConfig(mockServletContext, configuration.name());
                
                // Create a WebApplicationContext and initialize it with the xml and servlet configuration.
                //final XmlWebApplicationContext webApplicationContext = new XmlWebApplicationContext();
                final GenericWebApplicationContext genericWebApplicationContext = new GenericWebApplicationContext(mockServletContext);
                mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, genericWebApplicationContext);
        		
        		createBeanDefinitionReader(genericWebApplicationContext).loadBeanDefinitions(locations);
                AnnotationConfigUtils.registerAnnotationConfigProcessors(genericWebApplicationContext);
                
                //webApplicationContext.setServletConfig(servletConfig);
//                webApplicationContext.setConfigLocations(locations);
//                
                // Create a DispatcherServlet that uses the previously established WebApplicationContext.
                final DispatcherServlet dispatcherServlet = new DispatcherServlet() {
                        @Override
                        protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
                                return genericWebApplicationContext;
                        }
                };
              
                // Add the DispatcherServlet (and anything else you want) to the context.
                // Note: this doesn't happen until refresh is called below.
                genericWebApplicationContext.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
                        @Override
                        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                                beanFactory.registerResolvableDependency(DispatcherServlet.class, dispatcherServlet);
                                // Register any other beans here, including a ViewResolver if you are using JSPs.
                        }
                });
                 
                // Have the context notify the servlet every time it is refreshed.
                genericWebApplicationContext.addApplicationListener(new SourceFilteringListener(genericWebApplicationContext, new ApplicationListener<ContextRefreshedEvent>() {
                        @Override
                        public void onApplicationEvent(ContextRefreshedEvent event) {
                                dispatcherServlet.onApplicationEvent(event);
                        }
                }));
                
                // Prepare the context.
                genericWebApplicationContext.refresh();
                genericWebApplicationContext.registerShutdownHook();
                
                // Initialize the servlet.
                dispatcherServlet.setContextConfigLocation("");
                dispatcherServlet.init(servletConfig);
                
                return genericWebApplicationContext;
        }
        
        /**
         * One of these two methods will get called before {@link #loadContext(String...)}.
         * We just use this chance to extract the configuration.
         */
        @Override
        protected String[] generateDefaultLocations(Class<?> clazz) {
                extractConfiguration(clazz);            
                return super.generateDefaultLocations(clazz);
        }
        
        /**
         * One of these two methods will get called before {@link #loadContext(String...)}.
         * We just use this chance to extract the configuration.
         */
        @Override
        protected String[] modifyLocations(Class<?> clazz, String... locations) {
                extractConfiguration(clazz);
                return super.modifyLocations(clazz, locations);
        }
        
        private void extractConfiguration(Class<?> clazz) {
                configuration = AnnotationUtils.findAnnotation(clazz, MockWebApplication.class);
                if (configuration == null)
                        throw new IllegalArgumentException("Test class " + clazz.getName() + " must be annotated @MockWebApplication.");
        }
        
        @Override
        protected String getResourceSuffix() {
                return ".xml";
        }

		@Override
		public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
			return loadContext(mergedConfig.getLocations());
		}

}
