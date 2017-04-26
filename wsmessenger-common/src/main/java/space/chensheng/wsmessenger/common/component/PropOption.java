package space.chensheng.wsmessenger.common.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface PropOption {
	
	/**
	 * The field should not be null.
	 * @return
	 */
	boolean notNull() default false;
	
	/**
	 * Ignore injecting field.
	 * @return
	 */
	boolean ignore() default false;
}
