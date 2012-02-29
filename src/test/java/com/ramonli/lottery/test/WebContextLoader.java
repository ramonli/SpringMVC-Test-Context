package com.ramonli.lottery.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextLoader;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Why need a custom ContextLoader? As I want to run test against
 * <code>DispatcherServlet</code> in which a <code>WebApplicationContext</code>
 * is needed, but the default ContextLoader of @ContextConfiguration will
 * initialize a GenericApplicationContext.
 * <p>
 * One intresting problem of this custom WebContextLoader is that the returned
 * <code>WebApplicationContext</code> has no below
 * <code>BeanPostProcessor</code>s resigtered:
 * <ul>
 * <li>org.springframework.beans.factory.annotation.
 * AutowiredAnnotationBeanPostProcessor</li>
 * <li>org.springframework.beans.factory.annotation.
 * RequiredAnnotationBeanPostProcessor</li>
 * <li>org.springframework.context.annotation.CommonAnnotationBeanPostProcessor</li>
 * <li>
 * org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor</li>
 * </ul>
 * But for default <code>GenericApplicationContext</code>, all those 4
 * BeanPostProcessers have been registered.
 * <p>
 * Each of those 4 <code>BeanPostProcessor</code> does their own job, for
 * example <code>AutowiredAnnotationBeanPostProcessor</code> will inject
 * dependency into a field/property with @Autowire annotation in test class.
 * <p>
 * To fix this, I have tries 2 approachs:
 * <p>
 * - <b>Register <code>BeanPostProcessor</code>s programmatically</b><br/>
 * 
 * <pre>
 * ConfigurableWebApplicationContext wac = new XmlWebApplicationContext();
 * MockServletContext sc = new MockServletContext();
 * wac.setServletContext(sc);
 * wac.setConfigLocation(buffer.toString());
 * wac.refresh();// must initialize ApplicationContext before call
 * // addBeanPostProcessor()
 * wac.getBeanFactory().addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
 * wac.getBeanFactory().addBeanPostProcessor(new PersistenceAnnotationBeanPostProcessor());
 * wac.refresh();
 * </pre>
 * 
 * Anyway it doesn't work, and completely have no idea about it.
 * <p>
 * - <b>Define a decicated Test Context file</b><br/>
 * Define a separated spring test context file, for exmple spring-test.xml, and
 * state those <code>BeanPostProcessor</code> in that file explicitly.
 * <p>
 * It works!!
 * <p>
 * Please refer to below resources for more information:
 * <ul>
 * <li>https://jira.springsource.org/browse/SPR-5243</li>
 * <li>http://forum.springsource.org/showthread.php?86124-How-to-register-
 * BeanPostProcessor-programmatically</li>
 * </ul>
 * 
 * @author Ramon Li
 */
public class WebContextLoader implements ContextLoader {
	private Logger logger = LoggerFactory.getLogger(WebContextLoader.class);

	@Override
	public String[] processLocations(Class<?> clazz, String... locations) {
		// you can convert the location to your favourite format, here we return
		// it silently.
		return locations;
	}

	@Override
	public ApplicationContext loadContext(String... locations) throws Exception {
		// assemble configurtion location string
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < locations.length; i++)
			buffer.append(locations[i]).append(",");

		/*
		 * Refer to {@link
		 * org.springframework.web.context.ContextLoader#initWebApplicationContext
		 * } for how to construct a WebApplicationContext.
		 */
		ConfigurableWebApplicationContext wac = new XmlWebApplicationContext();
		MockServletContext sc = new MockServletContext();
		wac.setServletContext(sc);
		wac.setConfigLocation(buffer.toString());
		// call refresh to initialize BeanFactory
		wac.refresh();

		// It seems that registering BeanPostProcessor programmatically doesn't
		// work. The alternative approach is define a separated spring XML file
		// to state those BeanPostProcessors explicitly...spring-test.xml
		// register BeanPostProcess manually
		// wac.getBeanFactory().addBeanPostProcessor(new
		// AutowiredAnnotationBeanPostProcessor());
		// wac.getBeanFactory().addBeanPostProcessor(new
		// PersistenceAnnotationBeanPostProcessor());

		// wac.refresh();
		logger.debug("Refresh ApplicationContext:" + wac);

		return wac;
	}
}
