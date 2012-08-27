package com.ramonli.lottery.router;

import java.lang.reflect.Method;

/**
 * A interface to verify the parameters of handler method.
 * 
 * @author Ramon Li
 */
public interface HandlerMethodParameterValidator {
	
	void verify(Method handlerMethod);
}
