package mobi.nowtechnologies.server.admin.controller.streamzine;


import mobi.nowtechnologies.server.assembler.streamzine.DeepLinkInfoService;
import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.dto.streamzine.MediaDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.PlayerType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.CloudFileImagesService;
import mobi.nowtechnologies.server.service.streamzine.ImageDTO;
import mobi.nowtechnologies.server.service.streamzine.MobileApplicationPagesService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineTypesMappingService;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.service.streamzine.asm.RulesInfoAsm;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineAdminMediaAsm;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineUpdateAdminAsm;
import mobi.nowtechnologies.server.service.streamzine.asm.TypesMappingAsm;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import static mobi.nowtechnologies.server.shared.web.utils.RequestUtils.getHttpServletRequest;

import javax.annotation.Resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StreamzineController {

    public static final String URL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
    public static final String INDEX_PAGE = "/streamzine";
    private static final PageRequest PAGE_REQUEST_50 = new PageRequest(0, 50);
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;
    @Resource
    private StreamzineAdminMediaAsm streamzineAdminMediaAsm;
    @Resource
    private TypesMappingAsm typesMappingAsm;
    @Resource
    private RulesInfoAsm rulesInfoAsm;
    @Resource
    private StreamzineUpdateService streamzineUpdateService;
    @Resource
    private CloudFileImagesService cloudFileImagesService;
    @Resource
    private MediaService mediaService;
    @Resource
    private UserRepository userRepository;
    @Resource
    private MobileApplicationPagesService mobileApplicationPagesService;
    @Resource
    private ChartService chartService;
    @Value("${cloudFile.mediaCoverFileURL}")
    private String filesURL;
    @Value(value = "${cloudFile.filesURL}")
    private String imageURL;
    @Value("${streamzine.available.communities}")
    private String[] streamzineCommunities;
    @Resource
    private StreamzineTypesMappingService streamzineTypesMappingService;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private MessageSource messageSource;

    @RequestMapping(value = "/streamzine/media/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getMediaList(@RequestParam(value = "q", required = false, defaultValue = "") String searchWords,
                                     @RequestParam(value = "ids", required = false, defaultValue = "") String excludedIdsString, @RequestParam(value = "id") long updateId,
                                     @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {

        logger.info("Input params: searchWords [{}] excludedIds [{}] updateId [{}] communityRewriteUrl [{}]", searchWords, excludedIdsString, updateId, communityRewriteUrl);

        List<Integer> mediaIds = extractExcludedIds(excludedIdsString);

        Update update = streamzineUpdateService.get(updateId);
        Set<Media> medias = mediaService.getMediasForAvailableCommunityCharts(communityRewriteUrl, update.getDate().getTime(), escapeSearchWord(searchWords), mediaIds);

        List<MediaDto> chartItemDtos = streamzineAdminMediaAsm.toMediaDtos(medias);

        return new ModelAndView().addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
    }

    @RequestMapping(value = "/streamzine/user/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getUserList(@RequestParam(value = "q", required = false) String searchWords, @RequestParam(value = "ids", required = false, defaultValue = "") String excludedUserNames,
                                    @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        logger.info("input parameters: searchWords [{}], communityRewriteUrl [{}], excludedUserNames [{}]", searchWords, communityRewriteUrl, excludedUserNames);
        List<User> users = findUsers(searchWords, excludedUserNames, communityRewriteUrl);
        return new ModelAndView().addObject(UserDto.USER_DTO_LIST, streamzineUpdateAdminAsm.toUserDtos(users));
    }

    @RequestMapping(value = "/streamzine/chart/list", method = RequestMethod.GET)
    public ModelAndView getChartList(@RequestParam(value = "id") long updateId, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        logger.info("input parameters: updateId [{}], communityRewriteUrl [{}]", updateId, communityRewriteUrl);
        Update update = streamzineUpdateService.get(updateId);
        List<ChartDetail> chartDetails = chartService.getChartsByCommunityAndPublishTime(communityRewriteUrl, update.getDate());
        return new ModelAndView().addObject(ChartDto.CHART_DTO_LIST, streamzineAdminMediaAsm.toChartListItemDtos(chartDetails));
    }

    @RequestMapping(value = "/streamzine/pages/list", method = RequestMethod.GET)
    public ModelAndView getPages(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        return new ModelAndView().addObject("pages", mobileApplicationPagesService.getPages(communityRewriteUrl));
    }

    @RequestMapping(value = "/streamzine/actions/list", method = RequestMethod.GET)
    public ModelAndView getActions(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        return new ModelAndView().addObject("actions", mobileApplicationPagesService.getActions(communityRewriteUrl));
    }

    @RequestMapping(value = "/streamzine/upload/image", method = RequestMethod.POST)
    public ModelAndView uploadImage(MultipartFile file) throws IOException {
        ImageDTO dto = cloudFileImagesService.uploadImageWithGivenName(file.getBytes(), file.getOriginalFilename());
        ModelAndView modelAndView = new ModelAndView("streamzine/image_response");
        modelAndView.addObject("dto", dto);
        modelAndView.addObject("calcWidth", calcWidth(dto, 200));
        return modelAndView;
    }

    @RequestMapping(value = INDEX_PAGE, method = RequestMethod.GET)
    public ModelAndView index(@RequestParam(required = false, value = "selectedPublishDate", defaultValue = "") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate,
                              @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        Assert.notNull(community);

        logger.info("input parameters: selectedPublishDate [{}]", selectedPublishDate);
        if (selectedPublishDate == null) {
            selectedPublishDate = new Date();
        }

        ModelAndView modelAndView = putCommon(new ModelAndView("streamzine/streamzine"), selectedPublishDate, community);
        modelAndView.addObject("list", getDtoList(selectedPublishDate, community));
        modelAndView.addObject("selectedPublishDate", selectedPublishDate);
        return modelAndView;
    }

    @RequestMapping(value = "/streamzine/add/{publishDate}", method = RequestMethod.GET)
    public ModelAndView addUpdate(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl,
                                  @PathVariable(value = "publishDate") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date publishDate, RedirectAttributes redirectAttributes) {
        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        if (publishDate.before(new Date()) || streamzineUpdateService.get(publishDate, community) != null) {
            redirectAttributes.addFlashAttribute("notValidDate", publishDate);
            return redirectToMainPage(publishDate);
        } else {
            Update update = streamzineUpdateService.create(publishDate, community);
            return new ModelAndView("redirect:/streamzine/edit/" + update.getId());
        }
    }

    @RequestMapping(value = "/streamzine/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editUpdate(@PathVariable(value = "id") long id, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityRewriteUrl) {
        Update update = streamzineUpdateService.get(id);

        if (update == null) {
            throw new ResourceNotFoundException("Not found streamzine update by id: " + id);
        }

        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);
        Assert.notNull(community, "Community not found for: " + communityRewriteUrl);

        ModelAndView modelAndView = putCommon(new ModelAndView("streamzine/streamzine"), update.getDate(), community);
        modelAndView.addObject("list", getDtoList(update.getDate(), community));
        modelAndView.addObject("update", streamzineUpdateAdminAsm.convertOneWithBlocks(update, community));
        modelAndView.addObject("incomingUpdate", streamzineUpdateAdminAsm.convertOneWithBlocksToIncoming(update, community));
        modelAndView.addObject("selectedPublishDate", update.getDate());
        return modelAndView;
    }

    @RequestMapping(value = "/streamzine/delete/{id}/{publishDate}", method = RequestMethod.GET)
    public ModelAndView delUpdate(@PathVariable(value = "id") long id, @PathVariable(value = "publishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date publishDate,
                                  RedirectAttributes redirectAttributes) {
        Update update = streamzineUpdateService.get(id);
        if (!update.canEdit()) {
            redirectAttributes.addFlashAttribute("notValidPublishedDate", publishDate);
        } else {
            streamzineUpdateService.delete(id);
        }
        return redirectToMainPage(publishDate);
    }

    private List<Integer> extractExcludedIds(String excludedIds) {
        DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(excludedIds);
        return manualCompilationData.getMediaIds();
    }

    private int calcWidth(ImageDTO dto, int width) {
        if (dto.getWidth() != null) {
            return Math.min(dto.getWidth().intValue(), width);
        }

        return width;
    }

    private ModelAndView putCommon(ModelAndView model, Date selectedDate, Community community) {
        TypesMappingInfo info = streamzineTypesMappingService.getTypesMappingInfos();

        model.addObject("filesURL", filesURL);
        model.addObject("imageURL", imageURL);
        model.addObject("contentTypeMapping", typesMappingAsm.toDtos(info.getRules()));
        model.addObject("enabledCommunities", Arrays.asList(streamzineCommunities));
        model.addObject("updatePublishDates", streamzineUpdateService.getUpdatePublishDates(selectedDate, community));
        model.addObject("badgeMappingRules", rulesInfoAsm.getBadgeMappingInfo());
        model.addObject("titlesMappingRules", rulesInfoAsm.getTitlesMappingInfo());
        model.addObject("opener", rulesInfoAsm.buildTypesForOpener());
        model.addObject("players", getLocalizedPlayerTypes());
        model.addObject("defaultPlayer", PlayerType.getDefaultPlayerType().name());
        return model;
    }

    private Map<String, String> getLocalizedPlayerTypes() {
        Map<String, String> playerTypes = new HashMap<String, String>();
        for (PlayerType playerType : PlayerType.values()) {
            String message = messageSource.getMessage("streamzine." + playerType.name(), null, getHttpServletRequest().getLocale());
            playerTypes.put(playerType.name(), message);
        }
        return playerTypes;
    }

    private String escapeSearchWord(String searchWords) {
        if (searchWords == null) {
            return "";
        }
        return searchWords.replaceAll("\\^", "^^").replaceAll("%", "^%").replaceAll("_", "^_");
    }


    private List<UpdateDto> getDtoList(Date selectedPublishDate, Community community) {
        Collection<Update> list = streamzineUpdateService.list(selectedPublishDate, community);
        return streamzineUpdateAdminAsm.convertMany(list);
    }

    private ModelAndView redirectToMainPage(Date publishDate) {
        return new ModelAndView("redirect:/streamzine?selectedPublishDate=" + formatDate(publishDate, URL_DATE_FORMAT));
    }

    private String formatDate(Date publishDate, String urlDateFormat) {
        return new SimpleDateFormat(urlDateFormat).format(publishDate);
    }


    private List<User> findUsers(String searchWords, String excludedUserNames, String communityRewriteUrl) {
        if (!StringUtils.isEmpty(excludedUserNames)) {
            return userRepository.findByUserNameAndCommunity(escapeSearchWord(searchWords), communityRewriteUrl, Lists.newArrayList(excludedUserNames.split("#")), PAGE_REQUEST_50);
        } else {
            return userRepository.findByUserNameAndCommunity(escapeSearchWord(searchWords), communityRewriteUrl, PAGE_REQUEST_50);
        }
    }
}
