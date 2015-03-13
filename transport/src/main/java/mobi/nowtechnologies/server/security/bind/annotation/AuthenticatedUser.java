package mobi.nowtechnologies.server.security.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zam on 11/25/2014.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUser {

    String USER_NAME = "USER_NAME";
    String USER_TOKEN = "USER_TOKEN";
    String TIMESTAMP = "TIMESTAMP";
    String DEVICE_UID = "DEVICE_UID";

    String AUTHENTICATED_USER_REQUEST_ATTRIBUTE = "AUTHENTICATED_USER_REQUEST_ATTRIBUTE";

    /**
     * Whether the attribute is required. <p>Default is {@code true}, leading to an exception thrown in case of the attribute missing in the request. Switch this to {@code false} if you prefer a
     * {@code null} in case of the attribute missing.
     */
    boolean required() default true;
}
