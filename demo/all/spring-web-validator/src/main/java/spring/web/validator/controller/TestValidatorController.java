package spring.web.validator.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.web.validator.validator.IsNumberValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
public class TestValidatorController {

    @Getter
    @Setter
    private String name = "TestValidatorController";

    @RequestMapping(value = "/number", method = RequestMethod.POST)
    public Object number(
            @RequestBody @Validated NumberValue numberValue,
            BindingResult result
    ) {
        return "success";
    }

    public static class NumberValue {

//        @IsNumberValidator(message = "必须是数字")
        @NotNull(message = "value 不能为空")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}
