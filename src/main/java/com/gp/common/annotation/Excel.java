package com.gp.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Excel {
    String name() default "";
    int sort() default Integer.MAX_VALUE;
    double width() default 16;
    int type() default 0;
    String readConverterExp() default "";
    String dateFormat() default "";
    String defaultValue() default "";
}
