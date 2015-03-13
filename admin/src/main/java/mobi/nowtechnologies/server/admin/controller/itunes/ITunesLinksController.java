package mobi.nowtechnologies.server.admin.controller.itunes;

import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDto;
import mobi.nowtechnologies.server.dto.streamzine.error.ErrorDtoAsm;
import mobi.nowtechnologies.server.shared.dto.admin.ChartItemDto;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.Collection;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Oleg Artomov on 9/22/2014.
 */
@Controller
public class ITunesLinksController {

    @Resource
    private ErrorDtoAsm errorDtoAsm;

    @Resource
    private ITunesValidator iTunesValidator;


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Set<ErrorDto> handleBindException(MethodArgumentNotValidException bindException) {
        return errorDtoAsm.create(bindException);
    }

    @RequestMapping(value = "/validateITunesLinks", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void validateITunesLinks(@Valid @RequestBody Collection<ChartItemDto> chartItemDtos) {

    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(iTunesValidator);
    }


}
