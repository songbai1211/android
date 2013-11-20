package com.yanfan.easyIOC.ui.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 声明当前Activity下点击返回按钮回到桌面，不会结束当前进程。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) 
public @interface Desktop {
}