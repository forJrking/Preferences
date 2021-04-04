package com.example.spholder.proxy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 应用程序配置注解
 * Created by rae on 2020-02-20.
 */
@Documented
@Retention(RUNTIME)
public @interface SpConfig {
    /*** xml名称*/
    String xmlName() default "";

    /** DES: mmkv */
    boolean isMMKV() default false;

    /** DES: 加密key */
    String cryptKey() default "";

    /** DES: 多进程 */
    boolean isMultiProcess() default false;
}