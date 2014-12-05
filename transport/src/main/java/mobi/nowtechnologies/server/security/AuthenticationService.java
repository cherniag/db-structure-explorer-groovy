package mobi.nowtechnologies.server.security;

/**
 * Created by zam on 11/26/2014.
 */
public interface AuthenticationService<TAuthenticationData, TPrincipal> {

    TPrincipal authenticate(TAuthenticationData authenticationData) throws Exception;
}
