package com.lcx.annotation;

import com.lcx.common.constant.Item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckQueryProcess {

    String process();

    String item() default Item.SCORE;

}
