/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.admin.prop;

import mobi.nowtechnologies.server.shared.message.MergedProps;
import mobi.nowtechnologies.server.shared.message.PropLocale;
import mobi.nowtechnologies.server.utils.AppPropResourceConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

// @author Titov Mykhaylo (titov) on 02.04.2015.
@Controller
@RequestMapping("/admin")
public class PropController {

    @Resource
    private MergedProps messageProps;

    @Resource
    private AppPropResourceConfigurer appPropResourceConfigurer;

    @Resource
    private PropLocale propLocale;

    @Resource
    private MergedProps serviceProps;

    public void setAppPropResourceConfigurer(AppPropResourceConfigurer appPropResourceConfigurer) {
        this.appPropResourceConfigurer = appPropResourceConfigurer;
    }

    @RequestMapping(value = "/messageProps", method = RequestMethod.GET)
    public ModelAndView getMessageProps(HttpServletRequest request){

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        final Locale locale = localeResolver.resolveLocale(request);

        final Properties properties = messageProps.getProperties(locale);

        final ModelAndView modelAndView = new ModelAndView("prop", "properties", properties);
        modelAndView.addObject("locale", locale);
        return modelAndView;
    }

    @RequestMapping(value = "/serviceProps", method = RequestMethod.GET)
    public ModelAndView getServiceProps(@RequestParam(required = false) String community, @RequestParam(required = false) String language, @RequestParam(required = false) String country){

        Locale locale = PropLocale.DEFAULT_LOCALE;
        if(language != null){
            if(country != null){
                locale = new Locale(language, country);
            }else{
                locale = new Locale(language);
            }
        }

        final Locale communityLocale = propLocale.getCommunityLocale(community, locale);
        final Properties properties = serviceProps.getProperties(communityLocale);

        final ModelAndView modelAndView = new ModelAndView("prop", "properties", properties);
        modelAndView.addObject("locale", communityLocale);
        return modelAndView;
    }

    @RequestMapping(value = "/appProps", method = RequestMethod.GET)
    public ModelAndView getAppProps() throws IOException {

        Properties properties = appPropResourceConfigurer.mergeProperties();

        return new ModelAndView("prop", "properties", properties);
    }

}
