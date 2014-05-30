package mobi.nowtechnologies.server.admin.controller.streamzine;

import mobi.nowtechnologies.server.admin.validator.UpdateValidator;
import mobi.nowtechnologies.server.dto.streamzine.OrdinalBlockDto;
import mobi.nowtechnologies.server.dto.streamzine.UpdateIncomingDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDto;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateAdminAsm;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class StreamzineUpdateController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private UpdateValidator updateValidator;
    @Resource
    private StreamzineUpdateAdminAsm streamzineUpdateAdminAsm;
    @Resource
    private StreamzineUpdateService streamzineUpdateService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(updateValidator);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleBindException(MethodArgumentNotValidException bindException) {
        return composeErrorDtos(bindException.getBindingResult());
    }

    @RequestMapping(value = "/streamzine/update", method = RequestMethod.POST)
    public @ResponseBody void updateUpdateAndBlocks(@RequestBody @Valid UpdateIncomingDto dto,
                                                      @CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME, required = false) String community) {
        logger.info("Going to update the streamzine: [{}], community [{}]", dto, community);
        Collections.sort(dto.getBlocks(), OrdinalBlockDto.COMPARATOR);
        Update incoming = streamzineUpdateAdminAsm.fromIncomingDto(dto, community);
        streamzineUpdateService.update(dto.getId(), incoming);
    }


    private Set<ErrorDto> composeErrorDtos(Errors errors) {
        Set<ErrorDto> errorDtos = new TreeSet<ErrorDto>();

        for (FieldError fieldError : errors.getFieldErrors()) {
            ErrorDto dto = new ErrorDto();
            dto.setKey(fieldError.getField());
            dto.setMessage(fieldError.getDefaultMessage());
            errorDtos.add(dto);
        }

        return errorDtos;
    }


}
