package com.fang2chen.test.spring.boot.spring.boot.configuring.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("app.system")
@Data
public class MyProfilesBean {

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration sessionTimeout = Duration.ofSeconds(30);

    @DurationUnit(ChronoUnit.SECONDS)
    private Duration httpTimeout = Duration.ofSeconds(30);

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize bufferSize = DataSize.ofMegabytes(2);

    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize sizeThreshold = DataSize.ofMegabytes(2);

}
