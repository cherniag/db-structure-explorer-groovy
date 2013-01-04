package mobi.nowtechnologies.server.admin.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mobi.nowtechnologies.server.dto.AdItemDto;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Errors;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@RunWith(PowerMockRunner.class)
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
		
		Errors errors = Mockito.mock(Errors.class);
		
		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				
				return null;
			}
		}).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Mockito.when(errors.hasErrors()).thenReturn(Boolean.FALSE);
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertFalse(hasErrors);
		
		Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
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
		
		Errors errors = Mockito.mock(Errors.class);
		
		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				
				Object[] arguments = invocation.getArguments();
				errorList.add(arguments);
				
				String field = (String)arguments[0];
				String errorCode = (String)arguments[1];
				String defaultMessage = (String)arguments[2];
				
				assertEquals("imageFileName", field);
				assertEquals("ad.imageFileNameFieldIsNull.error", errorCode);
				assertEquals("The field imageFileName is mandatory", defaultMessage);
				
				return null;
			}
		}).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Mockito.when(errors.hasErrors()).thenReturn(Boolean.FALSE);
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertFalse(hasErrors);
		
		assertEquals(0, errorList.size());
		
		Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testCustomValidate_FileIsNullAndIdIsNull_Success(){
		AdItemDto adItemDto = new AdItemDto();
		
		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setMessage("message");
		
		final List<Object[]> errorList = new ArrayList<Object[]>();
		
		Errors errors = Mockito.mock(Errors.class);
		
		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
	
				Object[] arguments = invocation.getArguments();
				errorList.add(arguments);
				
				String field = (String)arguments[0];
				String errorCode = (String)arguments[1];
				String defaultMessage = (String)arguments[2];
				
				assertEquals("file", field);
				assertEquals("ad.fileFieldIsNull.error", errorCode);
				assertEquals("The field file is mandatory", defaultMessage);
				
				return null;
			}
		}).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Mockito.when(errors.hasErrors()).thenReturn(errorList.isEmpty());
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertTrue(hasErrors);
		assertEquals(0, errorList.size());
		
		Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testCustomValidate_WrongFileSize_Failure(){
		AdItemDto adItemDto = new AdItemDto();
		
		byte[] content = new byte[100000]; 
		for (int i = 0; i < content.length; i++) {
			content[i]=Byte.MAX_VALUE;
		}
		
		adItemDto.setFile(new MockMultipartFile("test", content));
		adItemDto.setActionType(AdActionType.URL);
		adItemDto.setAction("http://google.com.ua");
		adItemDto.setMessage("message");
		
		final List<Object[]> errorList = new ArrayList<Object[]>();
		
		Errors errors = Mockito.mock(Errors.class);
		
		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
	
				Object[] arguments = invocation.getArguments();
				errorList.add(arguments);
				
				String field = (String)arguments[0];
				String errorCode = (String)arguments[1];
				String defaultMessage = (String)arguments[2];
				
				assertEquals("file", field);
				assertEquals("ad.wrongFileSize.error", errorCode);
				assertEquals("Wrong file size. Should be more than 1 and less than 30720 bytes (30.72 kBytes)", defaultMessage);
				
				return null;
			}
		}).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Mockito.when(errors.hasErrors()).thenReturn(errorList.isEmpty());
		
		boolean hasErrors = adItemDtoValidator.customValidate(adItemDto, errors);
		
		assertTrue(hasErrors);
		assertEquals(1, errorList.size());
		
		Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
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