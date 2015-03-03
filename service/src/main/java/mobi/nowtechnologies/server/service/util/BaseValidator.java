package mobi.nowtechnologies.server.service.util;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public abstract class BaseValidator implements Validator {

    private static SpringValidatorAdapter springValidator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = validatorFactory.usingContext().getValidator();
        springValidator = new SpringValidatorAdapter(validator);
    }

    protected abstract boolean customValidate(Object target, Errors errors);

    public void validate(Object target, Errors errors) {
        baseValidate(target, errors);
        customValidate(target, errors);
    }

    protected void baseValidate(Object target, Errors errors) {
        springValidator.validate(target, errors);
    }

}