package mobi.nowtechnologies.server.validator.constraints;

import mobi.nowtechnologies.server.validator.constraints.impl.MultipartFileSize;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that the size of the uploaded file is between min and max included
 *
 * @author Mayboroda Dmytro
 */
@Documented
@Constraint(validatedBy = MultipartFileSize.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface FileSize {

    long min() default 0;

    long max() default Integer.MAX_VALUE;

    String message() default "{mobi.nowtechnologies.server.validator.constraints.FileSize.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}