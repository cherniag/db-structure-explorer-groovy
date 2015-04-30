/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class PhoneNumberCommandService {
    private Map<String, UserService> communityUserServices = new HashMap<>();

    public void setCommunityUserServices(Map<String, UserService> communityUserServices) {
        this.communityUserServices.putAll(communityUserServices);
    }

    //
    // API
    //
    public User activate(User user, String phone) {
        UserService userService = findUserService(user);

        user = userService.activatePhoneNumber(user, phone);

        return user;
    }

    public User activateAndPopulate(User user, String phone) {
        User u = activate(user, phone);

        if (phone != null) {
            findUserService(user).populateSubscriberData(user);
        }

        return u;
    }

    //
    // internals
    //
    private UserService findUserService(User user) {
        String community = user.getCommunity().getRewriteUrlParameter();

        Preconditions.checkArgument(communityUserServices.containsKey(community));

        return communityUserServices.get(community);
    }
}



