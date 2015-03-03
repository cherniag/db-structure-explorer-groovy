package mobi.nowtechnologies.server.admin.validator;

import mobi.nowtechnologies.server.dto.ChartDto;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ChartDetailFactory;
import mobi.nowtechnologies.server.service.ChartService;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import java.util.Arrays;

import org.springframework.validation.Errors;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestUtils.class})
public class ChartDtoValidatorTest {

    private ChartDtoValidator fixture;
    @Mock
    private ChartService mockChartService;

    @Before
    public void setUp() {
        fixture = new ChartDtoValidator();
        fixture.setChartService(mockChartService);
        PowerMockito.mockStatic(RequestUtils.class);
        Mockito.when(RequestUtils.getCommunityURL()).thenReturn("o2");
    }

    @Test
    public void testCustomValidate_Success() {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        chartDetail1.setPosition((byte) 1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        chartDetail2.setPosition((byte) 3);

        ChartDto chartDto = new ChartDto();
        chartDto.setPosition((byte) 1);
        chartDto.setId(1);

        Errors errors = Mockito.mock(Errors.class);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                return null;
            }
        }).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(mockChartService.getChartsByCommunity(anyString(), anyString(), any(ChartType.class))).thenReturn(Arrays.asList(chartDetail1, chartDetail2));
        Mockito.when(errors.hasErrors()).thenReturn(Boolean.FALSE);

        boolean hasErrors = fixture.customValidate(chartDto, errors);

        assertFalse(hasErrors);

        Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCustomValidate_SamePosition_Failure() {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(2);
        chartDetail1.setPosition((byte) 1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(3);
        chartDetail1.setPosition((byte) 3);

        ChartDto chartDto = new ChartDto();
        chartDto.setPosition((byte) 1);
        chartDto.setId(1);

        Errors errors = Mockito.mock(Errors.class);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                return null;
            }
        }).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(mockChartService.getChartsByCommunity(anyString(), anyString(), any(ChartType.class))).thenReturn(Arrays.asList(chartDetail1, chartDetail2));
        Mockito.when(errors.hasErrors()).thenReturn(Boolean.TRUE);

        boolean hasErrors = fixture.customValidate(chartDto, errors);

        assertTrue(hasErrors);

        Mockito.verify(errors, Mockito.times(1)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCustomValidate_FourthPlaylist_Success() {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(1);
        chartDetail1.setPosition((byte) 1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(2);
        chartDetail1.setPosition((byte) 3);

        ChartDto chartDto = new ChartDto();
        chartDto.setPosition((byte) 0);
        chartDto.setChartType(ChartType.FOURTH_CHART);
        chartDto.setId(1);

        Errors errors = Mockito.mock(Errors.class);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                return null;
            }
        }).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.when(mockChartService.getChartsByCommunity(anyString(), anyString(), any(ChartType.class))).thenReturn(Arrays.asList(chartDetail1, chartDetail2));
        Mockito.when(errors.hasErrors()).thenReturn(Boolean.FALSE);

        boolean hasErrors = fixture.customValidate(chartDto, errors);

        assertFalse(hasErrors);

        Mockito.verify(errors, Mockito.times(0)).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testCustomValidate_NotZeroPositionFourthPlaylist_Failure() {
        ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
        chartDetail1.getChart().setI(2);
        chartDetail1.setPosition((byte) 1);
        ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
        chartDetail2.getChart().setI(3);
        chartDetail1.setPosition((byte) 3);

        ChartDto chartDto = new ChartDto();
        chartDto.setPosition((byte) 1);
        chartDto.setChartType(ChartType.FOURTH_CHART);
        chartDto.setId(1);

        Errors errors = Mockito.mock(Errors.class);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                return null;
            }
        }).when(errors).rejectValue(Mockito.anyString(), eq("chart.position.error.invalidPositionForFourthChart"), Mockito.anyString());
        Mockito.when(mockChartService.getChartsByCommunity(anyString(), anyString(), any(ChartType.class))).thenReturn(Arrays.asList(chartDetail1, chartDetail2));
        Mockito.when(errors.hasErrors()).thenReturn(Boolean.TRUE);

        boolean hasErrors = fixture.customValidate(chartDto, errors);

        assertTrue(hasErrors);

        verify(errors, times(1)).rejectValue(Mockito.anyString(), eq("chart.position.error.invalidPositionForFourthChart"), Mockito.anyString());
    }
}