package mobi.nowtechnologies.server.admin.settings;

import mobi.nowtechnologies.server.admin.settings.asm.SettingsAsm;
import mobi.nowtechnologies.server.admin.settings.asm.dto.SettingsDto;
import mobi.nowtechnologies.server.admin.settings.service.SettingsService;
import mobi.nowtechnologies.server.dto.streamzine.ChartListItemDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineAdminMediaAsm;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SettingsController {

    @Value(value = "${cloudFile.filesURL}")
    private String imageURL;
    @Resource
    private SettingsAsm settingsAsm;
    @Resource
    private StreamzineAdminMediaAsm streamzineAdminMediaAsm;
    @Resource
    private ChartService chartService;
    @Resource
    private MobileApplicationPagesService mobileApplicationPagesService;
    @Resource
    private SettingsService settingsService;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ModelAndView settings() {
        ModelAndView modelAndView = new ModelAndView("settings/settings");
        modelAndView.addObject("imageURL", imageURL);
        return modelAndView;
    }

    @RequestMapping(value = "/settings/get", method = RequestMethod.GET)
    public
    @ResponseBody
    SettingsDto get(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        List<ChartDetail> chartDetails = chartService.getChartsByCommunityAndPublishTime(communityRewriteUrl, new Date());
        List<ChartListItemDto> chartListItemDtos = streamzineAdminMediaAsm.toChartListItemDtos(chartDetails);

        Set<String> pages = mobileApplicationPagesService.getPages();
        Set<String> actions = mobileApplicationPagesService.getActions();
        SettingsDto dto = settingsAsm.createDto(communityRewriteUrl, chartListItemDtos, pages, actions);

        logger().info("Getting {} for community id {}", dto, communityRewriteUrl);

        return dto;
    }

    @RequestMapping(value = "/settings/save", method = RequestMethod.POST)
    public
    @ResponseBody
    void save(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl, @RequestBody SettingsDto toSave) {
        logger().info("Saving {} for community id {}", toSave, communityRewriteUrl);

        settingsService.makeImport(communityRewriteUrl, toSave);
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}
