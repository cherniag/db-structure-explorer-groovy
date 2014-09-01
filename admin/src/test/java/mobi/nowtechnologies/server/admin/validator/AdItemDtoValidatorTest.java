package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.dto.AdItemDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class AdItemDtoValidatorTest{
	
	private static AdItemDtoValidator adItemDtoValidator;

    @BeforeClass
	public static void setUp() {
		adItemDtoValidator = new AdItemDtoValidator();
	}
	
	@Test
	public void testCustomValidate_Success(){
		AdItemDto adItemDto = new AdItemDto();
		
		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setFile(new MockMultipartFile("test", "content".getBytes()));
		adItemDto.setMessage("message");
        final List<Object[]> errorList = new ArrayList<Object[]>();
		
		Errors errors = mock(Errors.class);
		
		doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                errorList.add(invocation.getArguments());
                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertFalse(hasErrors);
		
		verify(errors, times(0)).rejectValue(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testCustomValidate_IdIsNotNullAndImageFileNameIsNull_Success(){
		AdItemDto adItemDto = new AdItemDto();
		
		adItemDto.setId(Integer.MAX_VALUE);
		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setFile(new MockMultipartFile("test", "content".getBytes()));
		adItemDto.setMessage("message");
		adItemDto.setImageFileName(null);
		
		final List<Object[]> errorList = new ArrayList<Object[]>();
		
		Errors errors = mock(Errors.class);
		
		doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                Object[] arguments = invocation.getArguments();
                errorList.add(arguments);

                String field = (String) arguments[0];
                String errorCode = (String) arguments[1];
                String defaultMessage = (String) arguments[2];

                assertEquals("imageFileName", field);
                assertEquals("ad.imageFileNameFieldIsNull.error", errorCode);
                assertEquals("The field imageFileName is mandatory", defaultMessage);

                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertFalse(hasErrors);
		
		assertEquals(0, errorList.size());
		
		verify(errors, times(0)).rejectValue(anyString(), anyString(), anyString());
	}

    @Test
    public void testCustomValidate_FileIsNullAndRemoveFileIsTrue_Success(){
        AdItemDto adItemDto = new AdItemDto();

        adItemDto.setActionType(AdActionType.URL);
        adItemDto.setAction("http://google.com.ua");
        adItemDto.setMessage("message");
        adItemDto.setRemoveImage(true);

        final List<Object[]> errorList = new ArrayList<Object[]>();

        Errors errors = mock(Errors.class);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                errorList.add(invocation.getArguments());
                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);

        boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);

        assertFalse(hasErrors);
        assertEquals(0, errorList.size());

        verify(errors, times(0)).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
	public void testCustomValidate_FileIsNullAndRemoveFileIsFalse_Failure(){
		AdItemDto adItemDto = new AdItemDto();

		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setMessage("message");

		final List<Object[]> errorList = new ArrayList<Object[]>();

		Errors errors = mock(Errors.class);

		doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                Object[] arguments = invocation.getArguments();
                errorList.add(arguments);

                String field = (String) arguments[0];
                String errorCode = (String) arguments[1];
                String defaultMessage = (String) arguments[2];

                assertEquals("file", field);
                assertEquals("ad.noFile.error", errorCode);
                assertEquals("No file is uploaded but \"None image\" is unchecked. Please check \"None image\" if you intentionally want to skip image upload.", defaultMessage);

                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);

        boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);

		assertTrue(hasErrors);
		assertEquals(1, errorList.size());

		verify(errors).rejectValue(anyString(), anyString(), anyString());
	}

    @Test
    public void testCustomValidate_FileIsEmptyAndRemoveFileIsFalse_Failure(){
        AdItemDto adItemDto = new AdItemDto();

        adItemDto.setActionType(AdActionType.URL);
        adItemDto.setAction("http://google.com.ua");
        adItemDto.setMessage("message");
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(true);
        adItemDto.setFile(multipartFile);

        final List<Object[]> errorList = new ArrayList<Object[]>();

        Errors errors = mock(Errors.class);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                Object[] arguments = invocation.getArguments();
                errorList.add(arguments);

                String field = (String) arguments[0];
                String errorCode = (String) arguments[1];
                String defaultMessage = (String) arguments[2];

                assertEquals("file", field);
                assertEquals("ad.noFile.error", errorCode);
                assertEquals("No file is uploaded but \"None image\" is unchecked. Please check \"None image\" if you intentionally want to skip image upload.", defaultMessage);

                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);

        boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);

        assertTrue(hasErrors);
        assertEquals(1, errorList.size());

        verify(errors).rejectValue(anyString(), anyString(), anyString());
    }

    private void mockErrors(final List<Object[]> errorList, Errors errors) {
        when(errors.hasErrors()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return !errorList.isEmpty();
            }
        });
    }

	@Test
	public void testCustomValidate_WrongFileSize_Failure(){
		AdItemDto adItemDto = new AdItemDto();
		
		byte[] content = new byte[100000]; 

		Arrays.fill(content, 0, content.length-1, Byte.MAX_VALUE);
		
		adItemDto.setFile(new MockMultipartFile("test", content));
		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setMessage("message");
		
		final List<Object[]> errorList = new ArrayList<Object[]>();
		
		Errors errors = mock(Errors.class);
		
		doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                Object[] arguments = invocation.getArguments();
                errorList.add(arguments);

                String field = (String) arguments[0];
                String errorCode = (String) arguments[1];
                String defaultMessage = (String) arguments[2];

                assertEquals("file", field);
                assertEquals("ad.wrongFileSize.error", errorCode);
                assertEquals("Wrong file size. Should be more than 1 and less than 30720 bytes (30.72 kBytes)", defaultMessage);

                return null;
            }
        }).when(errors).rejectValue(anyString(), anyString(), anyString());

        mockErrors(errorList, errors);
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertTrue(hasErrors);
		assertEquals(1, errorList.size());
		
		verify(errors, times(1)).rejectValue(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSupports_Success(){
		boolean isSupport = adItemDtoValidator.supports(AdItemDto.class);
		assertTrue(isSupport);
	}
	
	@Test
	public void testSupports_Failure(){
		boolean isSupport = adItemDtoValidator.supports(String.class);
		assertFalse(isSupport);
	}
	
}