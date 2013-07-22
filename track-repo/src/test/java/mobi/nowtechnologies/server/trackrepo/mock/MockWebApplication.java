package mobi.nowtechnologies.server.trackrepo.mock;

import java.lang.annotation.*;

/**
 * @author Titov Mykhaylo (titov)
 *
 * Configures a mock {@link org.springframework.web.context.WebApplicationContext}.  Each test class (or parent class) using
 * {@link MockWebApplicationContextLoader} must be annotated with this.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MockWebApplication {
        /**
         * The location of the webapp directory relative to your project.
         * For maven users, this is generally src/main/webapp (default).
         */
        String webapp() default "src/main/webapp";
        
        /**
         * The servlet name as defined in the web.xml.
         */
        String name();
}
