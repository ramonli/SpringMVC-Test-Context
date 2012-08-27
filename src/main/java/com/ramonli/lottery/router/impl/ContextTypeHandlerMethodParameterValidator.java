package com.ramonli.lottery.router.impl;

import java.lang.reflect.Method;

import javax.naming.Context;

import com.ramonli.lottery.router.HandlerMethodParameterValidator;

/**
 * Hanlder method can only accept 2 parameters of type <code>Context</code>
 */
public class ContextTypeHandlerMethodParameterValidator implements HandlerMethodParameterValidator {

	@Override
	public void verify(Method handlerMethod) {
		Class<?>[] params = handlerMethod.getParameterTypes();
		if (params.length != 2) {
			throw new IllegalArgumentException("Handler method can only accept 2 parameters("
			        + Context.class + "," + Context.class + "), while method(" + handlerMethod
			        + ") required " + params.length + " parameters.");
		}
		for (Class<?> param : params) {
			if (!Context.class.isAssignableFrom(param))
				throw new IllegalArgumentException("Handler method can only accept 2 parameters("
				        + Context.class + "," + Context.class + "), while method(" + handlerMethod
				        + ") required a parameter of type(" + param + ").");
		}
	}

}
