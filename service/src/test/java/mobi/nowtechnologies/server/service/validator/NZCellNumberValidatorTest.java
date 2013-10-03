package mobi.nowtechnologies.server.service.validator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 9/27/13
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class NZCellNumberValidatorTest {
    private NZCellNumberValidator fixture;

    @Before
    public void setUp(){
         fixture = new NZCellNumberValidator();
    }

    @Test
    public void testCustomValidate_valid_006421111111_Success() throws Exception {
        String phoneNumber = "006421111111";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+6421111111", result);
    }

    @Test
    public void testCustomValidate_valid_006421111111_WithHyphenAndParenthesesAndSpace_Success() throws Exception {
        String phoneNumber = "(00 64)-2111_1111";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+6421111111", result);
    }

    @Test
    public void testCustomValidate_valid_0064211111111_Success() throws Exception {
        String phoneNumber = "0064211111111";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+64211111111", result);
    }

    @Test
    public void testCustomValidate_valid_00642111111111_Success() throws Exception {
        String phoneNumber = "00642111111111";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+642111111111", result);
    }

    @Test
    public void testCustomValidate_valid_plus6421111111_Success() throws Exception {
        String phoneNumber = "+6421111111";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+6421111111", result);
    }

    @Test
    public void testCustomValidate_valid_0211141198_Success() throws Exception {
        String phoneNumber = "0211141198";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+64211141198", result);
    }

    @Test
    public void testCustomValidate_valid_2321154197_Success() throws Exception {
        String phoneNumber = "2321154197";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals("+642321154197", result);
    }

    @Test
    public void testCustomValidate_tooMuchNumbers_232115419774732186_Success() throws Exception {
        String phoneNumber = "232115419774732186";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals(null, result);
    }

    @Test
    public void testCustomValidate_notValidPrefix_3333333333333_Success() throws Exception {
        String phoneNumber = "33333333333";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals(null, result);
    }

    @Test
    public void testCustomValidate_tooMuchNumbers_d23211d5419774732186_Success() throws Exception {
        String phoneNumber = "d23211d5419";

        String result = fixture.validate(phoneNumber);

        Assert.assertEquals(null, result);
    }
}
