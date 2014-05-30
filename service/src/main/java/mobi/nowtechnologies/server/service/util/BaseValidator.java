package mobi.nowtechnologies.server.service.util;

import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

public abstract class BaseValidator implements org.springframework.validation.Validator {
	
	private static SpringValidatorAdapter springValidator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        javax.validation.Validator validator = validatorFactory.usingContext().getValidator();
        springValidator = new SpringValidatorAdapter(validator);
    }

    public abstract boolean customValidate(Object target, Errors errors);

    public void validate(Object target, Errors errors) {
        baseValidate(target, errors);
        customValidate(target, errors);
    }

    protected void baseValidate(Object target, Errors errors){
        springValidator.validate(target, errors);
    }

}