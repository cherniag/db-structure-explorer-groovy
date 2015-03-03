package mobi.nowtechnologies.server.controller;

import mobi.nowtechnologies.server.dto.VersionDto;
import mobi.nowtechnologies.server.service.VersionService;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Maksym Chernolevskyi (maksym)
 * @author Titov Mykhaylo (titov)
 */
@Controller
@RequestMapping(value = {"/VERSION", "/version"})
public class VersionController {

    @Resource
    private VersionService versionService;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    VersionDto sendVersion() {
        return versionService.getVersion();
    }

}