package mobi.nowtechnologies.server.transport.controller;

/**
 * Created by Oleg Artomov on 7/8/2014.
 */
public class AccountCheckResponseConstants {

    public static final String USER_DETAILS_XML_PATH = "//userDetails";
    public static final String USER_XML_PATH = "//user";

    public static final String USER_JSON_PATH = "$.response.data[0].user";
    public static final String USER_DETAILS_JSON_PATH = USER_JSON_PATH + ".userDetails";
}
