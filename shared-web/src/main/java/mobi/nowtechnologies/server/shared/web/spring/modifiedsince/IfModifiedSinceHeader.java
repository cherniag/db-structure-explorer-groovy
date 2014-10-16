package mobi.nowtechnologies.server.shared.web.spring.modifiedsince;

import java.lang.annotation.*;

import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedDefaultValue.CURRENT_DATE;

/**
 * Created by Oleg Artomov on 10/16/2014.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IfModifiedSinceHeader {
    IfModifiedDefaultValue defaultValue() default CURRENT_DATE;
}
