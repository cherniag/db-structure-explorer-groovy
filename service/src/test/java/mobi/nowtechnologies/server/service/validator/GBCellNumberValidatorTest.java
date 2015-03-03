package mobi.nowtechnologies.server.service.validator;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.runners.*;

import junit.framework.Assert;

/**
 * User: Alexsandr_Kolpakov Date: 9/27/13 Time: 5:30 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class GBCellNumberValidatorTest {

    private GBCellNumberValidator fixture;

    @Before
    public void setUp() {
        fixture = new GBCellNumberValidator();
    }

    @Test
    public void testCustomValidate_valid_004421111111_Success() throws Exception {
        String phoneNumber = "00447111111111";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447111111111", result);
    }

    @Test
    public void testCustomValidate_valid_004421111111_WithHyphenAndParenthesesAndSpace_Success() throws Exception {
        String phoneNumber = "(00 44)-7111_111111";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447111111111", result);
    }

    @Test
    public void testCustomValidate_valid_0044211111111_Success() throws Exception {
        String phoneNumber = "00447111111111";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447111111111", result);
    }

    @Test
    public void testCustomValidate_valid_plus6421111111_Success() throws Exception {
        String phoneNumber = "+447111111111";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447111111111", result);
    }

    @Test
    public void testCustomValidate_valid_0211141198_Success() throws Exception {
        String phoneNumber = "07111411981";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447111411981", result);
    }

    @Test
    public void testCustomValidate_valid_2321154197_Success() throws Exception {
        String phoneNumber = "7321154197";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals("+447321154197", result);
    }

    @Test
    public void testCustomValidate_tooMuchNumbers_232115419774732186_Success() throws Exception {
        String phoneNumber = "232115419774732186";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals(null, result);
    }

    @Test
    public void testCustomValidate_notValidPrefix_3333333333333_Success() throws Exception {
        String phoneNumber = "33333333333";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals(null, result);
    }

    @Test
    public void testCustomValidate_tooMuchNumbers_d23211d5419774732186_Success() throws Exception {
        String phoneNumber = "d23211d5419";

        String result = fixture.validateAndNormalize(phoneNumber);

        Assert.assertEquals(null, result);
    }
}
