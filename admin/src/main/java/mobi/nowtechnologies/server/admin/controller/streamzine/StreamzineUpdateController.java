package mobi.nowtechnologies.server.admin.controller.streamzine;

import mobi.nowtechnologies.server.admin.validator.UpdateValidator;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDtoAsm;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.service.streamzine.asm.StreamzineUpdateAdminAsm;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class StreamzineUpdateController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UpdateValidator updateValidator;
    @Resource
    private StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;
    @Resource
    private StreamzineUpdateService streamzineUpdateService;
    @Resource
    private ErrorDtoAsm errorDtoAsm;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(updateValidator);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleBindException(MethodArgumentNotValidException bindException) {
        return errorDtoAsm.create(bindException);
    }

    @RequestMapping(value = "/streamzine/update", method = RequestMethod.POST)
    public
    @ResponseBody
    void updateUpdateAndBlocks(@RequestBody @Valid UpdateIncomingDto dto, @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String community) {
        logger.info("Going to update the streamzine: [{}], community [{}]", dto, community);
        Collections.sort(dto.getBlocks(), OrdinalBlockDto.COMPARATOR);
        Update incoming = streamzineUpdateAdminAsm.fromIncomingDto(dto, community);
        streamzineUpdateService.update(dto.getId(), incoming);
    }

}
