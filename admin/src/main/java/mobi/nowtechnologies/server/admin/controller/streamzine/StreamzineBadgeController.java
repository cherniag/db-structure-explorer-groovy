package mobi.nowtechnologies.server.admin.controller.streamzine;


import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.dto.streamzine.badge.BadgesDtoAsm;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/streamzine/badges")
public class StreamzineBadgeController {

    @Resource
    private BadgesService badgesService;
    @Resource
    private BadgesDtoAsm badgesDtoAsm;
    @Resource
    private CommunityRepository communityRepository;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView getBadges(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) {
        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);
        List<FilenameAlias> fileNames = badgesService.findFilenameAliases(community);
        List<FileNameAliasDto> dtos = badgesDtoAsm.toFilenameDtos(fileNames);
        return new ModelAndView().addObject("fileNames", dtos);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndView updateBadgeName(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl, @RequestParam(value = "id") long id,
                                        @RequestParam(value = "newName") String newName) {
        badgesService.update(id, newName);

        return getBadges(communityRewriteUrl);
    }
}
