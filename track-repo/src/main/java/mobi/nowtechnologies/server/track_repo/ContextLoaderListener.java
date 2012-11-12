package mobi.nowtechnologies.server.track_repo;

import javax.servlet.ServletContextEvent;

import mobi.nowtechnologies.server.track_repo.utils.ZipUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener{
	private static final Logger LOGGER = LoggerFactory.getLogger(ContextLoaderListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		ApplicationProperties properties = getCurrentWebApplicationContext().getBean(ApplicationProperties.class);
		Resource binZip = properties.getBinZip();
		if (binZip != null && binZip.exists())
		{
			try {
				ZipUtils zipUtils = new ZipUtils();
				zipUtils.unzip(binZip.getFile().getAbsolutePath(), true);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
}
