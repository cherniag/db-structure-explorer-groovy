package mobi.nowtechnologies.server.admin.controller.streamzine;


import mobi.nowtechnologies.server.domain.streamzine.TypesMappingInfo;
import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.dto.streamzine.MediaDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.service.CloudFileImagesService;
import mobi.nowtechnologies.server.service.MediaService;
import mobi.nowtechnologies.server.service.streamzine.*;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineAdminMediaAsm;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineUpdateAdminAsm;
import mobi.nowtechnologies.server.service.streamzine.asm.TypesMappingAsm;
import mobi.nowtechnologies.server.shared.dto.admin.ChartDto;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Controller
public class StreamzineController {
    public static final String URL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String URL_DATE_TIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";
    private static final PageRequest PAGE_REQUEST_50 = new PageRequest(0, 50);

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;
    @Resource
    private StreamzineAdminMediaAsm streamzineAdminMediaAsm;
    @Resource
    private TypesMappingAsm typesMappingAsm;

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

    @Value("${admin.streamzine.enabled.community.url}")
    private String streamzineCommunity;

    @Resource
    private StreamzineTypesMappingService streamzineTypesMappingService;

    @RequestMapping(value = "/streamzine/media/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getMediaList(@RequestParam(value = "q", required = false, defaultValue = "") String searchWords,
                                     @RequestParam(value = "ids", required = false, defaultValue = "") String excludedIsrcsString,
                                     @RequestParam(value = "id") long updateId ,
                                     @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) {

        logger.info("Input params: searchWords [{}] excludedIsrcs [{}] updateId [{}] communityRewriteUrl [{}]", searchWords, excludedIsrcsString, updateId, communityRewriteUrl);

        List<String> mediaIsrcs = extractExcludedIsrcs(excludedIsrcsString);

        Update update = streamzineUpdateService.get(updateId);
        List<Media> medias = mediaService.getMediasForAvailableCommunityCharts(communityRewriteUrl, update.getDate().getTime(), escapeSearchWord(searchWords), mediaIsrcs);

        List<MediaDto> chartItemDtos = streamzineAdminMediaAsm.toMediaDtos(medias);

        return new ModelAndView()
                .addObject(ChartItemDto.CHART_ITEM_DTO_LIST, chartItemDtos);
    }

    @RequestMapping(value = "/streamzine/user/list", method = RequestMethod.GET)
    public ModelAndView getUserList(@RequestParam(value = "q", required = false) String searchWords,
                                    @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) {
        logger.info("input parameters: searchWords [{}], communityRewriteUrl [{}]", searchWords, communityRewriteUrl);
        List<User> users = userRepository.findByUserNameAndCommunityRewriteUrl(escapeSearchWord(searchWords), communityRewriteUrl, PAGE_REQUEST_50);
        return new ModelAndView()
                .addObject(
                        UserDto.USER_DTO_LIST,
                        streamzineUpdateAdminAsm.toUserDtos(users)
                );
    }

    @RequestMapping(value = "/streamzine/chart/list", method = RequestMethod.GET)
    public ModelAndView getChartList(@RequestParam(value = "id") long updateId,
                                     @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl){
        logger.info("input parameters: updateId [{}], communityRewriteUrl [{}]", updateId, communityRewriteUrl);
        Update update = streamzineUpdateService.get(updateId);
        List<ChartDetail> chartDetails = chartService.getChartsByCommunityAndPublishTime(communityRewriteUrl, update.getDate());
        return new ModelAndView().addObject(
                ChartDto.CHART_DTO_LIST,
                streamzineAdminMediaAsm.toChartListItemDtos(chartDetails)
        );
    }

    @RequestMapping(value = "/streamzine/pages/list", method = RequestMethod.GET)
    public ModelAndView getPages() {
        return new ModelAndView().addObject("pages", mobileApplicationPagesService.getPages());
    }

    @RequestMapping(value = "/streamzine/actions/list", method = RequestMethod.GET)
    public ModelAndView getActions() {
        return new ModelAndView().addObject("actions", mobileApplicationPagesService.getActions());
    }

    @RequestMapping(value = "/streamzine/upload/image", method = RequestMethod.POST)
    public ModelAndView uploadImage(MultipartFile file) {
        ImageDTO dto = cloudFileImagesService.uploadImage(file);
        ModelAndView modelAndView = new ModelAndView("streamzine/image_response");
        modelAndView.addObject("dto", dto);
        modelAndView.addObject("calcWidth", calcWidth(dto, 200));
        return modelAndView;
    }

    @RequestMapping(value = "/streamzine", method = RequestMethod.GET)
    public ModelAndView getUpdatesList(@RequestParam(required = false, value = "selectedPublishDate", defaultValue = "")
                                            @DateTimeFormat(pattern = URL_DATE_FORMAT) Date selectedPublishDate) {
        logger.info("input parameters: selectedPublishDate [{}]", selectedPublishDate);
        if (selectedPublishDate == null) {
            selectedPublishDate = new Date();
        }

        ModelAndView modelAndView = putCommon(new ModelAndView("streamzine/streamzine"), selectedPublishDate);
        modelAndView.addObject("list", getDtoList(selectedPublishDate));
        modelAndView.addObject("selectedPublishDate", selectedPublishDate);
        return modelAndView;
    }

    @RequestMapping(value = "/streamzine/add/{publishDate}", method = RequestMethod.GET)
    public ModelAndView addUpdate(@PathVariable(value = "publishDate") @DateTimeFormat(pattern = URL_DATE_TIME_FORMAT) Date publishDate, RedirectAttributes redirectAttributes) {
        if(publishDate.before(new Date()) || streamzineUpdateService.get(publishDate) != null) {
            redirectAttributes.addFlashAttribute("notValidDate", publishDate);
        } else {
            streamzineUpdateService.create(publishDate);
        }
        return redirectToMainPage(publishDate);
    }

    @RequestMapping(value = "/streamzine/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editUpdate(@PathVariable(value = "id") long id) {
        Update update = streamzineUpdateService.get(id);

        if(update == null) {
            throw new ResourceNotFoundException("Not found streamzine update by id: " + id);
        }

        ModelAndView modelAndView = putCommon(new ModelAndView("streamzine/streamzine"), update.getDate());
        modelAndView.addObject("list", getDtoList(update.getDate()));
        modelAndView.addObject("update", getUpdateDto(update));
        modelAndView.addObject("incomingUpdate", getIncomingDto(update));
        modelAndView.addObject("selectedPublishDate", update.getDate());
        return modelAndView;
    }

    @RequestMapping(value = "/streamzine/delete/{id}/{publishDate}", method = RequestMethod.GET)
    public ModelAndView delUpdate(
            @PathVariable(value = "id") long id,
            @PathVariable(value = "publishDate") @DateTimeFormat(pattern = URL_DATE_FORMAT) Date publishDate,
            RedirectAttributes redirectAttributes) {
        Update update = streamzineUpdateService.get(id);
        if  (!update.canEdit()){
            redirectAttributes.addFlashAttribute("notValidPublishedDate", publishDate);
        }
        else {
            streamzineUpdateService.delete(id);
        }
        return redirectToMainPage(publishDate);
    }

    private List<String> extractExcludedIsrcs(String excludedIsrcs) {
        DeepLinkInfoService.ManualCompilationData manualCompilationData = new DeepLinkInfoService.ManualCompilationData(excludedIsrcs);
        return manualCompilationData.getMediaIsrcs();
    }

    private int calcWidth(ImageDTO dto, int width) {
        if(dto.getWidth() != null) {
            return Math.min(dto.getWidth().intValue(), width);
        }

        return width;
    }

    private ModelAndView putCommon(ModelAndView model, Date selectedDate) {
        TypesMappingInfo info = streamzineTypesMappingService.getTypesMappingInfos();

        model.addObject("filesURL", filesURL);
        model.addObject("imageURL", imageURL);
        model.addObject("contentTypeMapping", typesMappingAsm.toDtos(info.getRules()));
        model.addObject("enabledCommunity", streamzineCommunity);
        model.addObject("updatePublishDates", streamzineUpdateService.getUpdatePublishDates(selectedDate));

        return model;
    }

    private String escapeSearchWord(String searchWords) {
        if(searchWords == null){
            return "";
        }
        return searchWords.replaceAll("\\^", "^^").replaceAll("%", "^%").replaceAll("_", "^_");
    }


    private UpdateDto getUpdateDto(Update update) {
        return streamzineUpdateAdminAsm.convertOneWithBlocks(update);
    }

    private UpdateDto getIncomingDto(Update update) {
        return streamzineUpdateAdminAsm.convertOneWithBlocksToIncoming(update);
    }

    private List<UpdateDto> getDtoList(Date selectedPublishDate) {
        Collection<Update> list = streamzineUpdateService.list(selectedPublishDate);
        return streamzineUpdateAdminAsm.convertMany(list);
    }

    private ModelAndView redirectToMainPage(Date publishDate) {
        return new ModelAndView("redirect:/streamzine?selectedPublishDate=" + formatDate(publishDate, URL_DATE_FORMAT));
    }

    private String formatDate(Date publishDate, String urlDateFormat) {
        return new SimpleDateFormat(urlDateFormat).format(publishDate);
    }

}
