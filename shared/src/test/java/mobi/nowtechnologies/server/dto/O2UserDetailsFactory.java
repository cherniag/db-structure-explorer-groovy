package mobi.nowtechnologies.server.dto;


import mobi.nowtechnologies.server.shared.enums.ProviderType;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class O2UserDetailsFactory
 {

	public static O2UserDetails createO2UserDetails() {
		return new O2UserDetails(O2.toString(), "");
	}


	public static O2UserDetails createO2UserDetails2() {
		return new O2UserDetails("0123456789", "0123456789");
	}


	public static O2UserDetails createO2UserDetails3() {
		return new O2UserDetails();
	}
}