package com.ramonli.lottery.router;

import java.lang.reflect.Method;

/**
 * A default implementation of <code>HandlerMethodParameterValidator</code>, it
 * will do nothing.
 * 
 * @author Ramon Li
 */
public class DefaultHandlerMethodParameterValidator implements HandlerMethodParameterValidator {

	@Override
	public void verify(Method handlerMethod) {
		// do nothing
	}

}
