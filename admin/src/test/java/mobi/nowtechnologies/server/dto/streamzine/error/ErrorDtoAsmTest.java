package mobi.nowtechnologies.server.dto.streamzine.error;

import org.springframework.context.MessageSource;

import org.junit.*;
import org.mockito.*;

public class ErrorDtoAsmTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    ErrorDtoAsm errorDtoAsm;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateUnknown() throws Exception {
        /*
        *
        *         Errors errors = mock(Errors.class);

        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn("field");
        when(fieldError.getDefaultMessage()).thenReturn("default message");

        ObjectError objectError = mock(ObjectError.class);
        when(objectError.getDefaultMessage()).thenReturn("global message");

        when(errors.getFieldErrors()).thenReturn(Lists.newArrayList(fieldError));
        when(errors.getGlobalErrors()).thenReturn(Lists.newArrayList(objectError));

        List<ErrorDto> dtos = new ArrayList<ErrorDto>(ErrorDto.composeErrorDtos(errors));
        assertEquals("default message", dtos.get(dtos.indexOf(new ErrorDto("field"))).getMessage());
        assertEquals("global message", dtos.get(dtos.indexOf(new ErrorDto(""))).getMessage());
        *
        *
        * */
    }
}