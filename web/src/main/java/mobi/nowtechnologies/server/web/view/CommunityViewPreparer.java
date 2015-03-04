package mobi.nowtechnologies.server.web.view;

import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.Expression;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparer;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * @author Titov Mykhaylo (titov)
 */
public class CommunityViewPreparer implements ViewPreparer, ServletContextAware {

    private static final String MOBILE_PATH_PARAM = "mobilePath";
    private static final String VIEW_PATH_ACCORDING_TO_DEVICE = "viewPathAccordingToDevice";
    private static final String ASSETS_PATH_ACCORDING_TO_COMMUNITY = "assetsPathAccordingToCommunity";
    private static final String ASSETS_PATH_WITHOUT_COMMUNITY = "assetsPathWithoutCommunity";
    private static final String ASSETS_PATH_ACCORDING_TO_COMMUNITY_WITHOUT_HOST = "assetsPathAccordingToCommunityWithoutHost";
    private static final String VIEWS_PATH = "/WEB-INF/views/";
    private static final String MOBILE_FOLDER_NAME = "mobile";
    private static final String WWW_FOLDER_NAME = "www";
    private static final String DEFAULT_COMMUNITY = "default";
    private static final String MOBILE_PATH = VIEWS_PATH + MOBILE_FOLDER_NAME;
    private static final String WEB_PATH = VIEWS_PATH + WWW_FOLDER_NAME;
    private static final String ASSETS_FOLDER_NAME = "assets";
    private static final String CDN_SUFFIX = "CDN";

    private static final Map<Boolean, String> deviceMap;
    private static final String IS_MOBILE_REQUEST = "isMobileRequest";

    private final Pattern VIEW_PATH_ACCORDING_TO_DEVICE_PATTERN = Pattern.compile("DEFAULT:$\\{" + VIEW_PATH_ACCORDING_TO_DEVICE + "\\}/(.+)");

    private ServletContext servletContext;
    private Properties cdnProperties;

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
                String communityName = WebUtils.getCookie(httpServletRequest, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME).getValue();
                Device device = DeviceUtils.getCurrentDevice(httpServletRequest);
                if (device != null) {
                    isMobile = device.isMobile();
                }

                String deviceFolderName;
                if (isMobile) {
                    deviceFolderName = MOBILE_FOLDER_NAME;
                }
                else {
                    deviceFolderName = WWW_FOLDER_NAME;
                }

                String path = getViewPath(isMobile, communityName, attributeContext);

                Map<String, Object> requestScopeMap = tilesContext.getRequestScope();
                requestScopeMap.put(MOBILE_PATH_PARAM, path);
                requestScopeMap.put(VIEW_PATH_ACCORDING_TO_DEVICE, path);
                requestScopeMap.put(IS_MOBILE_REQUEST, isMobile);

                String assetsCDN = getAssetsCDN(communityName, new StringBuilder().append(httpServletRequest.getContextPath()).append("/").toString());

                String internalPath = new StringBuilder().append(ASSETS_FOLDER_NAME).append("/").
                    append(deviceFolderName).append("/").append(communityName).append("/").toString();

                requestScopeMap.put(ASSETS_PATH_ACCORDING_TO_COMMUNITY_WITHOUT_HOST, internalPath);
                requestScopeMap.put(ASSETS_PATH_ACCORDING_TO_COMMUNITY, new StringBuilder().append(assetsCDN).append(internalPath).toString());
                requestScopeMap.put(ASSETS_PATH_WITHOUT_COMMUNITY, new StringBuilder().append(assetsCDN).append(ASSETS_FOLDER_NAME).append("/").toString());

                requestScopeMap.put("community", communityName);
                requestScopeMap.put("samsungCommunity", "samsung");
                break;
            }
        }

    }

    /**
     * Returns a path for assets based on assetsCDNMap values.
     *
     * @param communityName the community name for this CDN
     * @param webAppContext the default web app context to be returned if no CDN value is present
     * @return
     */
    private String getAssetsCDN(String communityName, String webAppContext) {
        // use specific CDN for community or a general CDN if no specific CDN is present...
        String communityCdn = cdnProperties.getProperty(communityName + CDN_SUFFIX);
        if (communityCdn != null && !communityCdn.trim().isEmpty()) {
            return communityCdn.trim();
        }

        String generalCDN = cdnProperties.getProperty(CDN_SUFFIX);
        if (generalCDN != null && !generalCDN.trim().isEmpty()) {
            return generalCDN.trim();
        }

        // if no assets CDN is specified, we use the assets from the web application - this is done for backward compatibility if no CND property is specified
        return webAppContext;
    }

    //TODO Finish this
    protected String getViewPath(boolean isMobile, String communityName, AttributeContext attributeContext) {
        String path = deviceMap.get(isMobile);

        String viewPath = path + File.separator + communityName;
        File viewDir = new File(servletContext.getRealPath(viewPath));
        if (!viewDir.exists()) {
            viewPath = path + File.separator + DEFAULT_COMMUNITY;
        }

        for (String attrName : attributeContext.getLocalAttributeNames()) {
            Attribute attribute = attributeContext.getAttribute(attrName);
            Expression expression = attribute.getExpressionObject();
            if (expression != null) {
                String expressionValue = expression.getExpression();
                String value = "DEFAULT:${requestScope.viewPathAccordingToDevice}/payments/oppsms/content.jsp";
                final String pattern = "DEFAULT:$\\{requestScope.viewPathAccordingToDevice\\}/(.+)";
                Matcher matcher = VIEW_PATH_ACCORDING_TO_DEVICE_PATTERN.matcher(expressionValue);

                if (matcher.matches()) {
                    value = viewPath + matcher.group(1);
                }
            }
        }

        return viewPath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setCdnProperties(Properties cdnProperties) {
        this.cdnProperties = cdnProperties;
    }

}