package mobi.nowtechnologies.server.service;

import javax.servlet.http.HttpServletRequest;

import mobi.nowtechnologies.server.error.ThrottlingException;

public interface ThrottlingService {

	boolean handle(HttpServletRequest request, String username, String communityUrl) throws ThrottlingException;

    void throttling(HttpServletRequest request, String userName, String deviceUID, String community);
}