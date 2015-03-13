package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.error.ThrottlingException;

import javax.servlet.http.HttpServletRequest;

public interface ThrottlingService {

    boolean handle(HttpServletRequest request, String username, String communityUrl) throws ThrottlingException;

    void throttling(HttpServletRequest request, String userName, String deviceUID, String community);
}