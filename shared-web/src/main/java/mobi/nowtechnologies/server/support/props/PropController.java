/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.support.props;

import mobi.nowtechnologies.server.shared.message.PropLocale;

import javax.annotation.Resource;

import java.lang.reflect.Method;
import java.util.Locale;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.MessageSourceSupport;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// @author Titov Mykhaylo (titov) on 02.04.2015.
@Controller
@RequestMapping("/admin")
public class PropController {

    @Resource
    private MessageSourceSupport messageSource;

    @Resource
    private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

    @Resource
    private PropLocale propLocale;

    @Resource
    private ReloadableResourceBundleMessageSource serviceReloadableResourceBundleMessageSource;

    public void setPropertyPlaceholderConfigurer(PropertyPlaceholderConfigurer propertyPlaceholderConfigurer) {
        this.propertyPlaceholderConfigurer = propertyPlaceholderConfigurer;
    }

    @RequestMapping(value = "/messageProps", method = RequestMethod.GET)
    public ModelAndView getMessageProps(@RequestParam(required = false) String community, @RequestParam(required = false) String language, @RequestParam(required = false) String country)
        throws Exception {
        return internal(community, language, country, messageSource);
    }

    @RequestMapping(value = "/serviceProps", method = RequestMethod.GET)
    public ModelAndView getServiceProps(@RequestParam(required = false) String community, @RequestParam(required = false) String language, @RequestParam(required = false) String country)
        throws Exception {
        return internal(community, language, country, serviceReloadableResourceBundleMessageSource);
    }

    ModelAndView internal(String community, String language, String country, MessageSourceSupport messageSource) throws Exception {
        Locale locale = PropLocale.DEFAULT_LOCALE;
        if (language != null) {
            if (country != null) {
                locale = new Locale(language, country);
            } else {
                locale = new Locale(language);
            }
        }

        Locale communityLocale = propLocale.getCommunityLocale(community, locale);

        final Method getMergedPropertiesMethod = ReloadableResourceBundleMessageSource.class.getDeclaredMethod("getMergedProperties", Locale.class);
        getMergedPropertiesMethod.setAccessible(true);
        getMergedPropertiesMethod.setAccessible(true);

        final Object propertiesHolder = ReflectionUtils.invokeMethod(getMergedPropertiesMethod, messageSource, communityLocale);

        Class<?> propertiesHolderClass = Class.forName("org.springframework.context.support.ReloadableResourceBundleMessageSource$PropertiesHolder");
        final Method getPropertiesMethod = ReflectionUtils.findMethod(propertiesHolderClass, "getProperties");

        final Object properties = ReflectionUtils.invokeMethod(getPropertiesMethod, propertiesHolder);

        ModelAndView modelAndView = new ModelAndView("prop");
        modelAndView.addObject("properties", properties);
        modelAndView.addObject("locale", communityLocale);

        return modelAndView;
    }


    @RequestMapping(value = "/appProps", method = RequestMethod.GET)
    public ModelAndView getAppProps() throws Exception {

        final Method mergePropertiesMethod = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
        mergePropertiesMethod.setAccessible(true);
        final Object mergedProperties = ReflectionUtils.invokeMethod(mergePropertiesMethod, propertyPlaceholderConfigurer);

        return new ModelAndView("prop", "properties", mergedProperties);
    }

}
