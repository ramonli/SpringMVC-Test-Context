package com.ramonli.lottery.router;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.METHOD })
public @interface RequestMap {

	/**
	 * Sprcify the request type of transaction. The format of value must follow
	 * the definition of {@link RoutineStrategy#from(RequestMap)}, it is totally
	 * implementation dependent.
	 */
	String value();
}
