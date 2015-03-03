package mobi.nowtechnologies.server.shared.dto;


/**
 * @author Titov Mykhaylo (titov)
 */
public class UserFacebookDetailsDtoFactory {

    private UserFacebookDetailsDtoFactory() {
    }


    public static UserFacebookDetailsDto createUserFacebookDetailsDto() {
        UserFacebookDetailsDto userFacebookDetailsDto = new UserFacebookDetailsDto();

        userFacebookDetailsDto.setAPI_VERSION("apiVersion");
        userFacebookDetailsDto.setAPP_VERSION("appVersion");
        userFacebookDetailsDto.setCOMMUNITY_NAME("communityName");
        userFacebookDetailsDto.setDEVICE_UID("deviceUID");
        userFacebookDetailsDto.setIpAddress("ipAddress");
        userFacebookDetailsDto.setSTORED_TOKEN("storedToken");

        return userFacebookDetailsDto;
    }
}