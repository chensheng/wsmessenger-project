package space.chensheng.wsmessenger.message.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MessageOptions {
	int order() default 0;
	
	boolean ignore() default false;
	
	boolean describeLastStrLen() default false;
	
	boolean notNull() default true;
}
