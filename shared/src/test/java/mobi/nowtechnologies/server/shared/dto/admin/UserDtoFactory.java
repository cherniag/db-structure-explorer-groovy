package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Collections;
import java.util.Date;

import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;

/**
 * The class <code>UserDtoFactory</code> implements static methods that return
 * instances of the class <code>{@link UserDto}</code>.
 * 
 * @generatedBy CodePro at 21.08.12 9:57
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UserDtoFactory {
	/**
	 * Prevent creation of instances of this class.
	 * 
	 * @generatedBy CodePro at 21.08.12 9:57
	 */
	private UserDtoFactory() {
	}

	/**
	 * Create an instance of the class <code>{@link UserDto}</code>.
	 * 
	 * @generatedBy CodePro at 21.08.12 9:57
	 */
	@SuppressWarnings("unchecked")
	public static UserDto createUserDto() {
	
		UserDto userDto = new UserDto();
		userDto.setAddress1("address1");
		userDto.setAddress1("address2");
		userDto.setCanContact(false);
		userDto.setCity("city");
		userDto.setCode("code");
		userDto.setConformStoredToken("conformStoredToken");
		userDto.setCountry(1);
		userDto.setCurrentPaymentDetailsDto(null);
		userDto.setDevice("device");
		userDto.setDeviceModel("deviceModel");
		userDto.setDeviceString("deviceString");
		userDto.setDeviceTypeId((byte)1);
		userDto.setDeviceUID("deviceUID");
		userDto.setDisplayName("displayName");
		userDto.setFacebookId("facebookId");
		userDto.setFirstDeviceLogin(new Date(1591115236000L));
		userDto.setFirstName("firstName");
		userDto.setFirstUserLogin(new Date(159111523696740L));
		userDto.setFreeBalance((byte)1);
		userDto.setFreeTrial(true);
		userDto.setId(1);
		userDto.setIpAddress("ipAddress");
		userDto.setLastDeviceLogin(new Date(15911152369670L));
		userDto.setLastName("lastName");
		userDto.setLastPaymentTx(1);
		userDto.setLastSuccessfulPaymentTime(new Date(15911523696740L));
		userDto.setLastWebLogin(new Date(1591523696740L));
		userDto.setMobile("mobile");
		userDto.setNewStoredToken("newStoredToken");
		userDto.setNextSubPayment(new Date(1581523696740L));
		userDto.setNumPsmsRetries(2);
		userDto.setOperator(1);
		userDto.setPaymentDetailsDtos(Collections.EMPTY_LIST);
		userDto.setPaymentEnabled(false);
		userDto.setPaymentStatus(3);
		userDto.setPaymentType("paymentType");
		userDto.setPin("pin");
		userDto.setPostcode("postcode");
		userDto.setPotentialPromoCodePromotionDto(null);
		userDto.setPotentialPromoCodePromotionId(null);
		userDto.setPotentialPromotionDto(null);
		userDto.setSessionID("sessionID");
		userDto.setSubBalance((byte)1);
		userDto.setTempToken("tempToken");
		userDto.setTitle("title");
		userDto.setToken("token");
		userDto.setUserGroup("userGroup");
		userDto.setUserGroupId((byte)1);
		userDto.setUserName("userName");
		userDto.setUserStatus(UserStatus.EULA);
		userDto.setUserStatusId((byte)1);
		userDto.setUserType(UserType.NORMAL);
		
		return userDto;
	}

	/**
	 * Create an instance of the class <code>{@link UserDto}</code>.
	 * 
	 * @generatedBy CodePro at 21.08.12 9:57
	 */
	public static UserDto createUserDto2() {
		return new UserDto();
	}
}