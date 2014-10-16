package mobi.nowtechnologies.server.shared.web.spring;

import java.lang.annotation.*;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IfModifiedSinceHeader {
}
