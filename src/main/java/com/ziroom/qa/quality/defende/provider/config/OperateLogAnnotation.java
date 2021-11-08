package com.ziroom.qa.quality.defende.provider.config;

import java.lang.annotation.*;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLogAnnotation {

    String moduleName() default "";

    String option() default "";
}
