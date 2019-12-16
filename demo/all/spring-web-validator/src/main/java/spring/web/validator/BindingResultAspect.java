package spring.web.validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * HibernateValidator错误结果处理切面
 * Created by macro on 2018/4/26.
 */
@Aspect
@Component
@Order(2)
public class BindingResultAspect {
    @Pointcut("execution(public * spring.web.validator.controller.*.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BindingResult) {
                BindingResult result = (BindingResult) arg;
                if (result.hasErrors()) {
                    FieldError fieldError = result.getFieldError();
                    if(fieldError!=null){
                        return CommonResult.validateFailed(fieldError.getDefaultMessage());
                    }else{
                        return CommonResult.validateFailed();
                    }
                }
            }
        }
        return joinPoint.proceed();
    }

    public static class CommonResult<T> {
        private long code;
        private String message;
        private T data;

        protected CommonResult() {
        }

        protected CommonResult(long code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        /**
         * 成功返回结果
         *
         * @param data 获取的数据
         */
        public static <T> CommonResult<T> success(T data) {
            return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
        }

        /**
         * 成功返回结果
         *
         * @param data 获取的数据
         * @param  message 提示信息
         */
        public static <T> CommonResult<T> success(T data, String message) {
            return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
        }

        /**
         * 失败返回结果
         * @param errorCode 错误码
         */
        public static <T> CommonResult<T> failed(IErrorCode errorCode) {
            return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
        }

        /**
         * 失败返回结果
         * @param message 提示信息
         */
        public static <T> CommonResult<T> failed(String message) {
            return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
        }

        /**
         * 失败返回结果
         */
        public static <T> CommonResult<T> failed() {
            return failed(ResultCode.FAILED);
        }

        /**
         * 参数验证失败返回结果
         */
        public static <T> CommonResult<T> validateFailed() {
            return failed(ResultCode.VALIDATE_FAILED);
        }

        /**
         * 参数验证失败返回结果
         * @param message 提示信息
         */
        public static <T> CommonResult<T> validateFailed(String message) {
            return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
        }

        /**
         * 未登录返回结果
         */
        public static <T> CommonResult<T> unauthorized(T data) {
            return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
        }

        /**
         * 未授权返回结果
         */
        public static <T> CommonResult<T> forbidden(T data) {
            return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
        }

        public long getCode() {
            return code;
        }

        public void setCode(long code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public enum ResultCode implements IErrorCode {
        SUCCESS(200, "操作成功"),
        FAILED(500, "操作失败"),
        VALIDATE_FAILED(404, "参数检验失败"),
        UNAUTHORIZED(401, "暂未登录或token已经过期"),
        FORBIDDEN(403, "没有相关权限");
        private long code;
        private String message;

        private ResultCode(long code, String message) {
            this.code = code;
            this.message = message;
        }

        public long getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public interface IErrorCode {
        long getCode();

        String getMessage();
    }

}
