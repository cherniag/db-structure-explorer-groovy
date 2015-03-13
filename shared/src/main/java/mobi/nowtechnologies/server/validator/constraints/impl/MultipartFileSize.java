package mobi.nowtechnologies.server.validator.constraints.impl;

import mobi.nowtechnologies.server.validator.constraints.FileSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileSize implements ConstraintValidator<FileSize, MultipartFile> {

    private long min;
    private long max;

    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (null == value || value.isEmpty()) {
            return true;
        }
        long size = value.getSize();
        return size >= min && size <= max;
    }
}