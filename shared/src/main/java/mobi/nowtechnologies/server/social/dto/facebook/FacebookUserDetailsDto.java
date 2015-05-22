/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.dto.facebook;

import mobi.nowtechnologies.server.social.dto.SocialInfoType;
import mobi.nowtechnologies.server.social.dto.UserDetailsDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Created by oar on 2/10/14.
 */
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class FacebookUserDetailsDto extends UserDetailsDto {


    private String facebookId;
    private String facebookProfileImageUrl;
    private Boolean facebookProfileImageSilhouette;


    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }


    @Override
    public SocialInfoType getSocialInfoType() {
        return SocialInfoType.Facebook;
    }

    public String getFacebookProfileImageUrl() {
        return facebookProfileImageUrl;
    }

    public void setFacebookProfileImageUrl(String facebookProfileImageUrl) {
        this.facebookProfileImageUrl = facebookProfileImageUrl;
    }

    public Boolean isFacebookProfileImageSilhouette() {
        return facebookProfileImageSilhouette;
    }

    public void setFacebookProfileImageSilhouette(Boolean facebookProfileImageSilhouette) {
        this.facebookProfileImageSilhouette = facebookProfileImageSilhouette;
    }
}
