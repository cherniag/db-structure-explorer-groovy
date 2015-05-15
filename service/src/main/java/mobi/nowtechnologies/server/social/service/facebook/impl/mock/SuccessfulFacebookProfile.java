/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook.impl.mock;

import java.util.Locale;

import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.User;

class SuccessfulFacebookProfile extends User {

    private String email;
    private String country;
    private String city;
    private String birthday;

    public SuccessfulFacebookProfile(String id, String name, String firstName, String lastName, String gender) {
        super(id, name, firstName, lastName, gender, Locale.getDefault());
    }

    public void addOtherInfo(String email, String country, String city, String birthday) {
        this.email = email;
        this.country = country;
        this.city = city;
        this.birthday = birthday;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Reference getLocation() {
        // try to recognized null values: NULL_VALUE_MARKER
        if (AppTestFacebookTokenService.unmaskNullValueIfNeeded(country) == null) {
            return new Reference("", city);
        } else {
            String facebookLocation = city + "," + country;
            return new Reference("", facebookLocation);
        }
    }

    @Override
    public String getBirthday() {
        return birthday;
    }
}
