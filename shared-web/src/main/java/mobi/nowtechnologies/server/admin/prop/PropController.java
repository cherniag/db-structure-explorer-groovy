/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.admin.prop;

import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.utils.AppPropResourceConfigurer;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

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
    private AppPropResourceConfigurer appPropResourceConfigurer;

    @Resource
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @RequestMapping(value = "/serviceProps", method = RequestMethod.GET)
    public ModelAndView getProps(@RequestParam String communityUrl, @RequestParam(required = false) String language, @RequestParam(required = false) String country){

        Locale locale = CommunityResourceBundleMessageSource.DEFAULT_LOCALE;
        if(language != null){
            if(country != null){
                locale = new Locale(language, country);
            }else{
                locale = new Locale(language);
            }
        }

        final Properties properties = communityResourceBundleMessageSource.getProperties(communityUrl, locale);

        return new ModelAndView("prop", "properties", properties);
    }

    @RequestMapping(value = "/appProps", method = RequestMethod.GET)
    public ModelAndView getProps() throws IOException {

        Properties properties = appPropResourceConfigurer.mergeProperties();

        return new ModelAndView("prop", "properties", properties);
    }

}
