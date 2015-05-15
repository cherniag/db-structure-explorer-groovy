package mobi.nowtechnologies.server.admin.controller.streamzine;

import mobi.nowtechnologies.server.admin.validator.BadgeValidator;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.dto.streamzine.badge.BadgeInfoDto;
import mobi.nowtechnologies.server.dto.streamzine.badge.BadgeResolutionDto;
import mobi.nowtechnologies.server.dto.streamzine.badge.BadgesDtoAsm;
import mobi.nowtechnologies.server.dto.streamzine.badge.ResolutionDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDtoAsm;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Dimensions;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.persistence.repository.BadgeMappingRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FilenameAliasRepository;
import mobi.nowtechnologies.server.persistence.repository.ResolutionRepository;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.service.streamzine.CloudFileImagesService;
import mobi.nowtechnologies.server.service.streamzine.ImageDTO;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BadgeController {

    @Resource
    private BadgesService badgesService;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private BadgeMappingRepository badgeMappingRepository;
    @Resource
    private ResolutionRepository resolutionRepository;
    @Resource
    private BadgesDtoAsm badgesDtoAsm;
    @Resource
    private CloudFileImagesService cloudFileImagesService;
    @Value(value = "${cloudFile.filesURL}")
    private String imageURL;
    @Resource
    private FilenameAliasRepository filenameAliasRepository;
    @Resource
    private ErrorDtoAsm errorDtoAsm;
    @Resource
    private BadgeValidator badgeValidator;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleBindException(MethodArgumentNotValidException bindException) {
        return errorDtoAsm.create(bindException);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleInvalidJson(HttpMessageNotReadableException error) {
        if (error.getCause() instanceof InvalidFormatException) {
            return errorDtoAsm.create((InvalidFormatException) error.getCause());
        }
        return errorDtoAsm.createUnknown();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleDuplicates(DataIntegrityViolationException error) {
        Set<ErrorDto> dtos = new HashSet<ErrorDto>();
        dtos.add(errorDtoAsm.createGlobalError("error.duplicate.value", null));
        return dtos;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(badgeValidator);
    }

    @RequestMapping(value = "/badges/remove/badge/{id}", method = RequestMethod.GET)
    public ModelAndView removeBadge(@PathVariable("id") long id, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) {
        logger().info("removing badge community: {} by id: {}", communityRewriteUrl, id);

        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        badgesService.hideBadge(community, id);

        return new ModelAndView("redirect:/badges");
    }

    @RequestMapping(value = "/badges/remove/resolution/{id}", method = RequestMethod.GET)
    public ModelAndView removeResolution(@PathVariable("id") long id) {
        logger().info("removing resolution by id: {}", id);

        List<FilenameAlias> filenameAliases = badgesService.removeResolution(id);

        for (FilenameAlias filenameAlias : filenameAliases) {
            badgesService.deleteCloudFileByAlias(filenameAlias);
        }
        return new ModelAndView("redirect:/badges");
    }

    @RequestMapping(value = "/badges", method = RequestMethod.GET)
    public ModelAndView badgesList(@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) {
        logger().info("get badges list for community: {}", communityRewriteUrl);

        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        ModelAndView modelAndView = new ModelAndView("badges/badges");
        modelAndView.addObject("allResolutions", badgesDtoAsm.toResolutionDtos(resolutionRepository.findAllSorted()));
        modelAndView.addObject("allDefaultBadges", badgesDtoAsm.toBadgeMappingListDtos(badgeMappingRepository.findAllDefault(community)));
        modelAndView.addObject("badges", badgesDtoAsm.convert(badgesService.getMatrix(community)));
        modelAndView.addObject("imageURL", imageURL);
        modelAndView.addObject("deviceTypes", new TreeSet<>(DeviceType.ALL_DEVICE_TYPES));
        return modelAndView;
    }

    @RequestMapping(value = "/badges/resolution/add", method = RequestMethod.POST)
    @ResponseBody
    public void resolution(@RequestBody @Valid ResolutionDto dto, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl)
        throws MethodArgumentNotValidException {
        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        logger().info("Creating resolution for: {}", community);

        Resolution resolution = badgesDtoAsm.toResolution(dto);
        badgesService.createResolution(resolution);
    }

    @RequestMapping(value = "/badges/badges/assign", method = RequestMethod.POST)
    @ResponseBody
    public void assignBadgeResolution(@RequestBody @Valid BadgeResolutionDto dto,
                                      @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) throws IOException {
        logger().info("Assign resolution: {} and community {}", dto, communityRewriteUrl);

        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);
        Resolution resolution = resolutionRepository.findOne(dto.getResolutionId());
        FilenameAlias original = filenameAliasRepository.findOne(dto.getAliasId());

        Assert.notNull(resolution);
        Assert.notNull(original);

        badgesService.createBadgeResolution(community, resolution, original, dto.getWidth(), dto.getHeight());
    }

    @RequestMapping(value = "/badges/image/preview", method = RequestMethod.POST)
    public ModelAndView uploadImage(MultipartFile file, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl) throws IOException {
        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);

        Assert.notNull(community);

        final String fileNameInCloud = community.getRewriteUrlParameter() + "_badge_" + file.getOriginalFilename();

        logger().info("Upload file: {} with name: {}", file, fileNameInCloud);

        ImageDTO dto = cloudFileImagesService.uploadImageWithGivenName(file.getBytes(), fileNameInCloud);
        ModelAndView modelAndView = new ModelAndView("badges/image_response");
        modelAndView.addObject("dto", dto);
        modelAndView.addObject("calcWidth", calcWidth(dto, 200));
        return modelAndView;
    }

    @RequestMapping(value = "/badges/image/add", method = RequestMethod.POST)
    @ResponseBody
    public void uploadImage(@RequestBody @Valid BadgeInfoDto badgeInfoDto, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String communityRewriteUrl)
        throws IOException {
        logger().info("Adding placeholder, info: {} for community: {}", badgeInfoDto, communityRewriteUrl);

        Community community = communityRepository.findByRewriteUrlParameter(communityRewriteUrl);
        Dimensions dim = new Dimensions(badgeInfoDto.getWidth(), badgeInfoDto.getHeight());
        badgesService.uploadBadge(community, badgeInfoDto.getTitle(), badgeInfoDto.getFile(), dim);
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

    private int calcWidth(ImageDTO dto, int width) {
        if (dto.getWidth() != null) {
            return Math.min(dto.getWidth().intValue(), width);
        }

        return width;
    }
}
