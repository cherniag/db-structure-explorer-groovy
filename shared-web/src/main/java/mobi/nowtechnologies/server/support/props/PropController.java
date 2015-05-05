/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.support.props;

import mobi.nowtechnologies.common.util.LocaleUtils;

import javax.annotation.Resource;

import java.util.Locale;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// @author Titov Mykhaylo (titov) on 02.04.2015.
@Controller
@RequestMapping("/admin")
public class PropController {

    @Resource
    private PropertyPlaceholderConfigurer propertyPlaceholderConfigurer;

    @Resource
    private ReloadableResourceBundleMessageSource messageSource;

    @Resource
    private ReloadableResourceBundleMessageSource serviceReloadableResourceBundleMessageSource;

    public void setPropertyPlaceholderConfigurer(PropertyPlaceholderConfigurer propertyPlaceholderConfigurer) {
        this.propertyPlaceholderConfigurer = propertyPlaceholderConfigurer;
    }

    @RequestMapping(value = "/appProps", method = RequestMethod.GET)
    public ModelAndView getApplicationProperties() throws Exception {
        Object properties = getValue(propertyPlaceholderConfigurer, "mergeProperties");
        return new ModelAndView("prop", "properties", properties);
    }

    @RequestMapping(value = "/messageProps", method = RequestMethod.GET)
    public ModelAndView getMessageProperties(@RequestParam(required = false) String community, @RequestParam(required = false) String language, @RequestParam(required = false) String country)
        throws Exception {
        return internal(LocaleUtils.buildLocale(community, language, country), messageSource);
    }

    @RequestMapping(value = "/serviceProps", method = RequestMethod.GET)
    public ModelAndView getServiceProperties(@RequestParam(required = false) String community, @RequestParam(required = false) String language, @RequestParam(required = false) String country)
        throws Exception {
        return internal(LocaleUtils.buildLocale(community, language, country), serviceReloadableResourceBundleMessageSource);
    }

    ModelAndView internal(Locale communityLocale, ReloadableResourceBundleMessageSource messageSource) throws Exception {
        Object propertiesHolder = getValue(messageSource, "getMergedProperties", communityLocale);
        Object properties = getValue(propertiesHolder, "getProperties");

        ModelAndView modelAndView = new ModelAndView("prop", "properties", properties);
        modelAndView.addObject("locale", communityLocale);
        return modelAndView;
    }

    private Object getValue(Object targetObject, String targetMethod, Object... arguments) throws Exception {
        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetObject(targetObject);
        invoker.setTargetMethod(targetMethod);
        invoker.setArguments(arguments);
        return invoker.invoke();
    }

}
