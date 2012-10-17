package mobi.nowtechnologies.server.shared.context;

import javax.servlet.ServletContext;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourceLoader;

public class CommunityResourceBundleThemeSource extends ResourceBundleThemeSource implements ServletContextAware {
	
	private MessageSource parent;
	private String themePath;
	private ServletContext servletContext;
	private int cacheSeconds = -1;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setParentMessageSource(MessageSource messageSource) {
        parent = messageSource;
    }
	
	public void setThemePath(String themePath) {
		this.themePath = themePath;
	}
	
	public void setCacheSeconds(int cacheSeconds) {
		this.cacheSeconds = cacheSeconds;
	}

	@Override
    protected MessageSource createMessageSource(String basename) {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setResourceLoader(new ServletContextResourceLoader(servletContext));
    	messageSource.setBasenames(new String[]{themePath+basename});
    	messageSource.setCacheSeconds(cacheSeconds);
        messageSource.setParentMessageSource(parent);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}