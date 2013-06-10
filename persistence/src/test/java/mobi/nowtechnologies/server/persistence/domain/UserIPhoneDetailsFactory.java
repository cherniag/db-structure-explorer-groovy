package mobi.nowtechnologies.server.persistence.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserIPhoneDetailsFactory
{

	public static UserIPhoneDetails createUserIPhoneDetails() {
		final UserIPhoneDetails userIPhoneDetails = new UserIPhoneDetails();
		userIPhoneDetails.setToken("565fc91f81d74e2299347f42d9ce42c326b02e8b8d109e54ee9c160c8dc0ec2b");

		return userIPhoneDetails;
	}

	public static UserIPhoneDetails createUserIPhoneDetails(String token) {
		final UserIPhoneDetails userIPhoneDetails = new UserIPhoneDetails();
		userIPhoneDetails.setToken(token);

		return userIPhoneDetails;
	}

	public static List<UserIPhoneDetails> createUserIPhoneDetailsSingletonList() {
		List<UserIPhoneDetails> userIPhoneDetailsSingletonList = Collections.<UserIPhoneDetails> singletonList(createUserIPhoneDetails());
		return userIPhoneDetailsSingletonList;
	}

	public static List<UserIPhoneDetails> createUserIPhoneDetailsList(int size) {
		List<UserIPhoneDetails> userIPhoneDetailsList = new ArrayList<UserIPhoneDetails>(size);

		for (int i = 0; i < size; i++) {
			userIPhoneDetailsList.add(createUserIPhoneDetails("565fc91f81d74e2299347f42d9ce42c326b02e8b8d109e54ee9c160c8dc0ec2" + i));
		}

		return userIPhoneDetailsList;
	}
}