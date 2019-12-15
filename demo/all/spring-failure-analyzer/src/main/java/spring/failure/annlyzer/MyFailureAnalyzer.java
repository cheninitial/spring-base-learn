package spring.failure.annlyzer;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class MyFailureAnalyzer extends AbstractFailureAnalyzer<MyException> {


    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, MyException cause) {
        return new FailureAnalysis(cause.getMessage(), "MyException", rootFailure);
    }

}
