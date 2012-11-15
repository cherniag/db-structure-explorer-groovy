package mobi.nowtechnologies.server.web.view;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class CommunityViewPreparer implements ViewPreparer {

	private static final String MOBILE_PATH_PARAM = "mobilePath";
	private static final String VIEW_PATH_ACCORDING_TO_DEVICE = "viewPathAccordingToDevice";
	private static final String ASSETS_PATH_ACCORDING_TO_COMMUNITY = "assetsPathAccordingToCommunity";
	private static final String VIEWS_PATH = "/WEB-INF/views/";
	private static final String MOBILE_FOLDER_NAME = "mobile";
	private static final String WWW_FOLDER_NAME = "www";
	private static final String MOBILE_PATH = VIEWS_PATH + MOBILE_FOLDER_NAME;
	private static final String WEB_PATH = VIEWS_PATH + WWW_FOLDER_NAME;
	private static final String ASSETS_FOLDER_NAME = "assets";

	private static final Map<Boolean, String> deviceMap;
	private static final String IS_MOBILE_REQUEST = "isMobileRequest";

	static {
		Map<Boolean, String> map = new HashMap<Boolean, String>();
		map.put(Boolean.TRUE, MOBILE_PATH);
		map.put(Boolean.FALSE, WEB_PATH);
		deviceMap = Collections.unmodifiableMap(map);
	}
		
	@Override
	public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {

		Object[] objects = tilesContext.getRequestObjects();

		Boolean isMobile = Boolean.FALSE;
		for (Object object : objects) {
			if (object instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) object;
				Device device = DeviceUtils.getCurrentDevice(httpServletRequest);
				if (device != null)
					isMobile = device.isMobile();
				String path = deviceMap.get(isMobile);
				String deviceFolderName;
				if (isMobile)
					deviceFolderName = MOBILE_FOLDER_NAME;
				else
					deviceFolderName = WWW_FOLDER_NAME;
				Map<String, Object> requestScopeMap = tilesContext.getRequestScope();
				requestScopeMap.put(MOBILE_PATH_PARAM, path);
				requestScopeMap.put(VIEW_PATH_ACCORDING_TO_DEVICE, path);
				requestScopeMap.put(IS_MOBILE_REQUEST, isMobile);
				String communityName = WebUtils.getCookie(httpServletRequest, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME).getValue();
				requestScopeMap.put(ASSETS_PATH_ACCORDING_TO_COMMUNITY, ASSETS_FOLDER_NAME + "/" + deviceFolderName + "/" + communityName + "/");
				
				requestScopeMap.put("community", communityName);
				requestScopeMap.put("samsungCommunity", "samsung");
				break;
			}
		}

	}

}