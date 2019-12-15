package com.fang2chen.test.spring.boot.spring.boot.configuring;

import com.fang2chen.test.spring.boot.spring.boot.configuring.component.MyProfilesValidBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

//@Component
public class SamplePropertiesValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == MyProfilesValidBean.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "host", "host.empty");
        ValidationUtils.rejectIfEmpty(errors, "port", "port.empty");
        MyProfilesValidBean properties = (MyProfilesValidBean) target;
        if (properties.getHost() != null) {
            errors.rejectValue("host", "Invalid host");
        }
    }
}
