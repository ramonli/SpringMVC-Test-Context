package com.ramonli.lottery.router;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javassist.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * A <code>BeanProcessor</code> to register all @Controller handler bean and @RequestMap
 * method. The handler method and bean will be register under the key of type
 * <code>RoutineStrategy</code>, so the implementation of
 * <code>RoutineStrategy</code> must override <code>hashCode</code> and
 * <code>equals</code> to make sure it can be used as a map key.
 * <p>
 * A general spring configuration will be as below:
 * 
 * <pre>
 * {@code
 * <context:component-scan base-package="com.mpos.lottery.te" />
 * 
 * <bean id="webRourseHandlerBeatPostProcessor"
 * 	class="com.mpos.lottery.te.port.router.RoutineMetaDataBeanPostProcessor">
 * 	<property name="routineStrategyClass" value="com.mpos.lottery.te.port.domain.RoutineKey" />
 * 	<property name="handlerMethodParameterValidator">
 * 		<bean class="com.mpos.lottery.te.port.domain.ContextTypeHandlerMethodParameterValidator" />
 * 	</property>
 * </bean>
 * </xml>}
 * </pre>
 * <p>
 * All controllers(annotated by @Controller) will be registered automatically by
 * 'component-scan' process, and its dependencies will be injected automatically
 * too.
 * 
 * @author Ramon Li
 */
public class RoutineMetaDataBeanPostProcessor implements BeanPostProcessor {
	private Log logger = LogFactory.getLog(RoutineMetaDataBeanPostProcessor.class);
	private Map<RoutineStrategy, Method> methodMap = new HashMap<RoutineStrategy, Method>();
	private Map<RoutineStrategy, Object> handlerMap = new HashMap<RoutineStrategy, Object>();
	// spring dependencies
	private String routineStrategyClass;
	private HandlerMethodParameterValidator handlerMethodParameterValidator = new DefaultHandlerMethodParameterValidator();

	/**
	 * Register all @Controler beans and @RequestMap methods. Each @RequestMap
	 * method will be registered under the key <code>RoutineStrategy</code>
	 * which will be assembled from value of @RequestMap.
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName)
	        throws BeansException {
		// Find whether this bean has annotated by @Controller
		Controller controller = this.findAnnotationOnBean(bean, Controller.class);
		if (controller != null) {
			if (logger.isDebugEnabled())
				logger.debug("Found a @Controller(" + bean + ")");
			// fetch all request mapping information and store them in cache
			this.registerResourceMethod(bean);
		}

		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
	        throws BeansException {
		// for default implementation, you must return the passed-in bean
		// directly.
		return bean;
	}

	/**
	 * Lookup a appropriate @RequestMap handler method by supplied
	 * <code>RoutineStrategy</code>.
	 * 
	 * @param routineStrategy The key to lookup handler method.
	 * @return a 2-length array, the 1st will be handler bean, and 2nd will be
	 *         handler method. If no handler found, null will be returned.
	 */
	public Object[] lookupHandlerMethod(RoutineStrategy routineStrategy) {
		if (logger.isDebugEnabled())
			logger.debug("Lookup controller by key(" + routineStrategy + ").");
		Object[] result = null;
		Object handler = this.handlerMap.get(routineStrategy);
		if (handler != null) {
			result = new Object[2];
			result[0] = handler;
			result[1] = this.methodMap.get(routineStrategy);
		}
		return result;
	}

	/**
	 * Retrieve all qualified @Controller, it is registered by key of type
	 * <code>RoutineStrategy</code>
	 */
	public Map<RoutineStrategy, Object> getHandlerMap() {
		return Collections.unmodifiableMap(this.handlerMap);
	}

	/**
	 * Retrieve all qualified handler methods, it is registered by key of type
	 * <code>RoutineStrategy</code>
	 */
	public Map<RoutineStrategy, Method> getHandlerMethodMap() {
		return Collections.unmodifiableMap(this.methodMap);
	}

	// --------------------------------------------------------
	// HELPER METHODS
	// --------------------------------------------------------

	protected RoutineStrategy instantiateRoutineStrategy() {
		try {
			Class clazz = ClassUtils.forName(this.getRoutineStrategyClass(), Thread.currentThread()
			        .getContextClassLoader());
			if (!RoutineStrategy.class.isAssignableFrom(clazz)) {
				throw new IllegalStateException("The class with supplied name("
				        + this.getRoutineStrategyClass() + ") must be type of "
				        + RoutineStrategy.class);
			}
			Constructor constructor = ClassUtils.getConstructorIfAvailable(clazz, new Class[] {});
			if (logger.isDebugEnabled())
				logger.debug("Loaded RoutineStrategy class:" + clazz + " successfully.");
			return (RoutineStrategy) constructor.newInstance(new Object[] {});
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Find a {@link Annotation} of <code>annotationType</code> on the specified
	 * bean, traversing its interfaces and super classes if no annotation can be
	 * found on the given class itself, as well as checking its raw bean class
	 * if not found on the exposed bean reference (e.g. in case of a proxy).
	 */
	protected <A extends Annotation> A findAnnotationOnBean(Object bean, Class<A> annotationType) {
		A ann = null;
		Class<?> beanType = this.getUserClass(bean);
		if (beanType != null) {
			ann = AnnotationUtils.findAnnotation(beanType, annotationType);
		}
		return ann;
	}

	/**
	 * Return the user-defined class for the given instance: usually simply the
	 * class of the given instance, but the original class in case of a
	 * CGLIB-generated subclass and proxy class.
	 * 
	 * @param bean the instance to check
	 * @return the user-defined class
	 */
	protected Class<?> getUserClass(Object bean) {
		Class<?> clazz = AopUtils.getTargetClass(bean);
		if (clazz != null && clazz.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	/**
	 * Register all methods, including superclass and interfaces, which
	 * annotated by <code>@RequestMap</code>.
	 */
	protected void registerResourceMethod(final Object bean) {
		final Class<?> userHandlerType = this.getUserClass(bean);
		ReflectionUtils.doWithMethods(userHandlerType, new ReflectionUtils.MethodCallback() {
			public void doWith(Method method) {
				Method specificMethod = ClassUtils.getMostSpecificMethod(method, userHandlerType);
				RequestMap requestMap = AnnotationUtils.findAnnotation(specificMethod,
				        RequestMap.class);
				if (requestMap != null) {
					// verify method parameters
					RoutineMetaDataBeanPostProcessor.this.handlerMethodParameterValidator
					        .verify(specificMethod);
					// assemble routine key
					RoutineStrategy routineStrategy = RoutineMetaDataBeanPostProcessor.this
					        .instantiateRoutineStrategy();
					routineStrategy.from(requestMap);
					// verify whether a handler has been registered by this key
					if (methodMap.get(routineStrategy) != null) {
						throw new IllegalStateException("Can't register handler method("
						        + specificMethod + ") by key(" + routineStrategy
						        + "), it has been registered with("
						        + methodMap.get(routineStrategy) + ").");
					}
					// now register handler method
					methodMap.put(routineStrategy, specificMethod);
					handlerMap.put(routineStrategy, bean);
					if (logger.isDebugEnabled()) {
						logger.debug("Found a @RequestMap method(" + specificMethod + ") on bean("
						        + bean + "), and register it under key(" + routineStrategy + ").");
					}
				}
			}
		}, new ReflectionUtils.MethodFilter() {

			/**
			 * Only public method supported.
			 */
			@Override
			public boolean matches(Method method) {
				return Modifier.isPublic(method.getModifiers());
			}
		});
	}

	/**
	 * Get a single {@link Annotation} of <code>annotationType</code> from the
	 * supplied {@link Method}, traversing its super methods if no annotation
	 * can be found on the given method itself.
	 * <p>
	 * Annotations on methods are not inherited by default, so we need to handle
	 * this explicitly.
	 * @param method the method to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation found, or <code>null</code> if none found
	 */
	protected <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
		A annotation = AnnotationUtils.getAnnotation(method, annotationType);
		Class<?> cl = method.getDeclaringClass();
		if (annotation == null) {
			annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
		}
		while (annotation == null) {
			cl = cl.getSuperclass();
			if (cl == null || cl == Object.class) {
				break;
			}
			try {
				Method equivalentMethod = cl.getDeclaredMethod(method.getName(),
				        method.getParameterTypes());
				annotation = AnnotationUtils.getAnnotation(equivalentMethod, annotationType);
				if (annotation == null) {
					annotation = searchOnInterfaces(method, annotationType, cl.getInterfaces());
				}
			} catch (NoSuchMethodException ex) {
				// We're done...
			}
		}
		return annotation;
	}

	protected <A extends Annotation> A searchOnInterfaces(Method method, Class<A> annotationType,
	        Class[] ifcs) {
		A annotation = null;
		for (Class<?> iface : ifcs) {
			try {
				Method equivalentMethod = iface.getMethod(method.getName(),
				        method.getParameterTypes());
				annotation = AnnotationUtils.getAnnotation(equivalentMethod, annotationType);
			} catch (NoSuchMethodException ex) {
				// Skip this interface - it doesn't have the method...
			}
			if (annotation != null) {
				break;
			}
		}
		return annotation;
	}

	// --------------------------------------------------------
	// SPRING DEPENDENCIES INJECTION
	// --------------------------------------------------------

	public String getRoutineStrategyClass() {
		return routineStrategyClass;
	}

	public void setRoutineStrategyClass(String routineStrategyClass) {
		this.routineStrategyClass = routineStrategyClass;
	}

	public HandlerMethodParameterValidator getHandlerMethodParameterValidator() {
		return handlerMethodParameterValidator;
	}

	public void setHandlerMethodParameterValidator(
	        HandlerMethodParameterValidator handlerMethodParameterValidator) {
		this.handlerMethodParameterValidator = handlerMethodParameterValidator;
	}

}
