/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.social.service.facebook;

/**
 * Created by enes on 3/11/15.
 */
public class FacebookProfileImage {

    private String url;
    private boolean silhouette;

    public FacebookProfileImage(String url, boolean silhouette) {
        this.url = url;
        this.silhouette = silhouette;
    }

    public String getUrl() {
        return url;
    }

    public boolean isSilhouette() {
        return silhouette;
    }
}
