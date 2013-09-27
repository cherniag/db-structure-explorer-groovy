package mobi.nowtechnologies.server.transport.controller;

import junit.framework.Assert;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.*;
import static mobi.nowtechnologies.server.transport.controller.ApplyInitPromoController.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 15:17
 */
@RunWith(PowerMockRunner.class)
public class ApplyInitPromoControllerTest {

    private ModelAndView modelAndView;

    private ApplyInitPromoController applyInitPromoControllerFixture;

    @Mock
    public UserService userServiceMock;

    @Mock
    public UpdateO2UserTask updateO2UserTaskMock;

    @Before
    public void setUp(){
        applyInitPromoControllerFixture = new ApplyInitPromoController();

        applyInitPromoControllerFixture.setUserService(userServiceMock);
        applyInitPromoControllerFixture.setUpdateO2UserTask(updateO2UserTaskMock);
    }

    @Test
    public void should() throws Exception{
        //given
        String communityName = "o2";
        String userName = "+380913008899";
        String userToken = "userToken";
        String timestamp = "0";
        String token = "token";
        String community = "o2";
        String apiVersion = "4.0";

        //when
        User user = new User().withMobile("mobile").withActivationStatus(ACTIVATED);
        User mobileUser = new User();

        doReturn(user).when(userServiceMock).findByNameAndCommunity(userName, communityName);
        doReturn(mobileUser).when(userServiceMock).findByNameAndCommunity(user.getMobile(), communityName);
        doNothing().when(updateO2UserTaskMock).handleUserUpdate(user);
        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        doReturn(accountCheckDTO).when(userServiceMock).applyInitPromo(user, mobileUser, token, false);

        modelAndView = applyInitPromoControllerFixture.applyPromotion(communityName, userName, userToken, timestamp, token, community, apiVersion);

        //then
        assertNotNull(modelAndView);

        Object model = modelAndView.getModelMap().get(MODEL_NAME);
        assertNotNull(model);

        assertThat(model, instanceOf(Response.class));
        Response response = (Response) model;

        Object[] responseContent = response.getObject();
        assertNotNull(responseContent);

        assertThat(responseContent.length, is(1));
        assertThat(responseContent[0], instanceOf(AccountCheckDTO.class));

        AccountCheckDTO actualAccountCheckDTO = (AccountCheckDTO) responseContent[0];
        assertThat(actualAccountCheckDTO, is(accountCheckDTO));

    }
}
