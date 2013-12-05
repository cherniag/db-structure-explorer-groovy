package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.transport.controller.ApplyInitPromoController.MODEL_NAME;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

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

    @Mock
    public AccCheckController accCheckControllerMock;

    @Mock
    public NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServicesMock;
    private User user;
    private User mobileUser;

    private AccountCheckDto applyPromotion(String communityName, String userName, String userToken, String timestamp, String token, String community, String apiVersion) {
        user = new User().withUserName(userName).withMobile("mobile").withActivationStatus(ACTIVATED);
        mobileUser = new User();

        doReturn(user).when(userServiceMock).findByNameAndCommunity(user.getUserName(), communityName);
        doReturn(user).when(userServiceMock).checkCredentials(user.getUserName(), userToken, timestamp, communityName);
        doNothing().when(updateO2UserTaskMock).handleUserUpdate(user);
        AccountCheckDto AccountCheckDto = new AccountCheckDto(new AccountCheckDTO().withUserName(userName).withUserToken(userToken).withUser(mobileUser));
        doReturn(AccountCheckDto).when(accCheckControllerMock).processAccCheck(mobileUser);
        doReturn(mobileUser).when(userServiceMock).applyInitPromo(user, token, false);
        doReturn("rememberMeToken").when(nowTechTokenBasedRememberMeServicesMock).getRememberMeToken(AccountCheckDto.userName, AccountCheckDto.userToken);

        modelAndView = applyInitPromoControllerFixture.applyPromotion(communityName, userName, userToken, timestamp, token, community, apiVersion);
        return AccountCheckDto;
    }

    private void verifyThatApplyInitPromoWithOutCallingProviderAPIForUserUpdating(String communityName, String token, String timestamp, AccountCheckDto AccountCheckDto) {
        assertNotNull(modelAndView);

        Object model = modelAndView.getModelMap().get(MODEL_NAME);
        assertNotNull(model);

        assertThat(model, instanceOf(Response.class));
        Response response = (Response) model;

        Object[] responseContent = response.getObject();
        assertNotNull(responseContent);

        assertThat(responseContent.length, is(1));
        assertThat(responseContent[0], instanceOf(AccountCheckDto.class));

        AccountCheckDto actualAccountCheckDto = (AccountCheckDto) responseContent[0];
        assertThat(actualAccountCheckDto, is(AccountCheckDto));

        verify(userServiceMock, times(0)).findByNameAndCommunity(user.getUserName(), communityName);
        verify(userServiceMock, times(1)).checkCredentials(user.getUserName(), token, timestamp, communityName);
        verify(nowTechTokenBasedRememberMeServicesMock, times(0)).getRememberMeToken(AccountCheckDto.userName, AccountCheckDto.userToken);
        verify(updateO2UserTaskMock, times(0)).handleUserUpdate(user);
    }

    @Before
    public void setUp(){
        applyInitPromoControllerFixture = new ApplyInitPromoController();

        applyInitPromoControllerFixture.setUserService(userServiceMock);
        applyInitPromoControllerFixture.setUpdateO2UserTask(updateO2UserTaskMock);
        applyInitPromoControllerFixture.setAccCheckController(accCheckControllerMock);
        applyInitPromoControllerFixture.setNowTechTokenBasedRememberMeServices(nowTechTokenBasedRememberMeServicesMock);
    }

    @Test
    public void shouldApplyInitPromoWithOutCallingProviderAPIForUserUpdating() throws Exception{
        //given
        String communityName = "o2";
        String userName = "+380913008899";
        String userToken = "userToken";
        String timestamp = "0";
        String token = "token";
        String community = "o2";
        String apiVersion = "4.0";

        //when
        AccountCheckDto AccountCheckDto = applyPromotion(communityName, userName, userToken, timestamp, token, community, apiVersion);

        //then
        verifyThatApplyInitPromoWithOutCallingProviderAPIForUserUpdating(communityName, userToken, timestamp, AccountCheckDto);
    }
}
