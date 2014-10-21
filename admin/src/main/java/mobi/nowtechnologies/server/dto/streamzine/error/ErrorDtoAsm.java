package mobi.nowtechnologies.server.dto.streamzine.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;
import java.util.TreeSet;

public class ErrorDtoAsm {
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Set<ErrorDto> create(MethodArgumentNotValidException exception) {
        return create(exception.getBindingResult());
    }

    public Set<ErrorDto> createUnknown() {
        Set<ErrorDto> errorDtos = new TreeSet<ErrorDto>();
        errorDtos.add(createGlobalError("error.unknown", null));
        return errorDtos;
    }

    public Set<ErrorDto> create(InvalidFormatException exception) {
        final String notValidValueErrorMessage = messageSource.getMessage("error.not.valid", null, LocaleContextHolder.getLocale());

        Set<ErrorDto> errorDtos = new TreeSet<ErrorDto>();
        for (JsonMappingException.Reference ref : exception.getPath()) {
            ErrorDto dto = new ErrorDto();
            dto.setKey(ref.getFieldName());
            dto.setMessage(notValidValueErrorMessage);
            errorDtos.add(dto);
        }
        return errorDtos;
    }

    public Set<ErrorDto> create(Errors errors) {
        Set<ErrorDto> errorDtos = new TreeSet<ErrorDto>();

        for (FieldError fieldError : errors.getFieldErrors()) {
            ErrorDto dto = new ErrorDto();
            dto.setKey(fieldError.getField());
            dto.setMessage(fieldError.getDefaultMessage());
            errorDtos.add(dto);
        }

        for (ObjectError objectError : errors.getGlobalErrors()) {
            errorDtos.add(createGlobalError(objectError.getCode(), objectError.getArguments(), objectError.getDefaultMessage()));
        }

        return errorDtos;
    }

    public ErrorDto createGlobalError(String key, Object[] arguments) {
        String description = messageSource.getMessage(key, arguments, LocaleContextHolder.getLocale());

        ErrorDto dto = new ErrorDto();
        dto.setKey("");
        dto.setMessage(description);
        return dto;
    }

    public ErrorDto createGlobalError(String key, Object[] arguments, String defaultMessage) {
        String description = messageSource.getMessage(key, arguments, defaultMessage, LocaleContextHolder.getLocale());

        ErrorDto dto = new ErrorDto();
        dto.setKey("");
        dto.setMessage(description);
        return dto;
    }
}
