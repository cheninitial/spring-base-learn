package spring.web.validator.validator;

import cn.hutool.core.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import spring.web.validator.controller.TestValidatorController;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class IsNumberValidatorClass implements ConstraintValidator<IsNumberValidator, String> {

    private final static String NAME = "IsNumberValidatorClass";

    private boolean required;

    @Override
    public void initialize(IsNumberValidator constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        System.out.println(String.format("%s 验证 %s", NAME, s));


        if (!required) {
            return true;
        }

        if (NumberUtil.isNumber(s)) {
            return true;
        }

        return false;
    }
}
