package jim.framework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jim.framework.system.model.EUserType;

/**
 * 注解，当@Access加在类上时，当前类中的所有方法都要经过权限过滤。当加在方法上时，同时类上没有加时，只对该方法进行权限过滤。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {
	boolean value() default true;
	
	EUserType[] userType() default {};
}
