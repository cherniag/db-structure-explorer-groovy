package mobi.nowtechnologies.server.admin.controller.streamzine;


import mobi.nowtechnologies.server.dto.ImageDTO;
import mobi.nowtechnologies.server.dto.streamzine.FileNameAliasDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.service.streamzine.BadgesService;
import mobi.nowtechnologies.server.service.streamzine.asm.FileNameAliasDtoAsm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/streamzine/badges")
public class StreamzineBadgeController {
    @Resource
    private FileNameAliasDtoAsm fileNameAliasDtoAsm;
    @Value("${admin.streamzine.enabled.community.url}")
    private String streamzineCommunity;
    @Resource
    private BadgesService badgesService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView getBadges() {
        List<FilenameAlias> badges = badgesService.findAllBadges();
        List<FileNameAliasDto> dtos = fileNameAliasDtoAsm.convertMany(badges);
        return new ModelAndView().addObject("badges", dtos);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public ModelAndView updateBadgeName(@RequestParam(value = "oldName") String oldName, @RequestParam(value = "newName") String newName) {
        boolean noNameDuplication = badgesService.update(oldName, newName);

        ModelAndView badges = getBadges();

        if(!noNameDuplication) {
            badges.addObject("notUniqueName", true);
        }

        return badges;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ModelAndView deleteBadge(@RequestParam(value = "name") String name) {
        badgesService.delete(name);
        return getBadges();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadBadge(MultipartFile file) {
        ImageDTO dto = badgesService.upload(file);
        ModelAndView modelAndView = new ModelAndView("streamzine/image_response");
        modelAndView.addObject("dto", dto);
        modelAndView.addObject("calcWidth", calcWidth(dto, 200));
        return modelAndView;
    }

    private int calcWidth(ImageDTO dto, int width) {
        if(dto.getWidth() != null) {
            return Math.min(dto.getWidth().intValue(), width);
        }

        return width;
    }
}
