/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.dto.googleplus;

import mobi.nowtechnologies.server.social.dto.SocialInfoType;
import mobi.nowtechnologies.server.social.dto.UserDetailsDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by oar on 4/28/2014.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class GooglePlusUserDetailsDto extends UserDetailsDto {

    private String googlePlusId;

    @Override
    public SocialInfoType getSocialInfoType() {
        return SocialInfoType.GooglePlus;
    }

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }


}
