/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.googleplus.impl.mock;

import java.util.Date;
import java.util.Map;

import org.springframework.social.google.api.plus.Person;


public class SuccessfulGooglePlusProfile extends Person {

    private String id;
    private String givenName;
    private String familyName;
    private String displayName;
    private String imageUrl;
    private Date birthday;
    private String gender;
    private String accountEmail;
    private Map<String, Boolean> placesLived;

    public SuccessfulGooglePlusProfile(String id, String givenName, String familyName, String displayName, String imageUrl, Date birthday, String gender, String accountEmail,
                                       Map<String, Boolean> placesLived) {
        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.displayName = displayName;
        this.imageUrl = imageUrl;
        this.birthday = birthday;
        this.gender = gender;
        this.accountEmail = accountEmail;
        this.placesLived = placesLived;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public String getAccountEmail() {
        return accountEmail;
    }

    @Override
    public Map<String, Boolean> getPlacesLived() {
        return placesLived;
    }
}
