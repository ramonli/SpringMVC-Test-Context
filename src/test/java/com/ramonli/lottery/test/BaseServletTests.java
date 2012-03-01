package com.ramonli.lottery.test;

import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * This test will be ran against <code>DispatchServlet</code> directly, then we
 * can whether we have a corrent SpringMVC setting, like @RequestMapping,
 * WebDataBinder etc.
 * <p>
 * Spring TestContext Framework. If extending from
 * <code>AbstractTransactionalJUnit4SpringContextTests</code>, you don't need to
 * declare <code>@RunWith</code>,
 * <code>TestExecutionListeners(3 default listeners)</code> and
 * <code>@Transactional</code>. Refer to
 * {@link AbstractTransactionalJUnit4SpringContextTests} for more information.
 * <p>
 * Legacy JUnit 3.8 class hierarchy is deprecated.
 * <p>
 * Reference:
 * <ul>
 * <li>https://jira.springsource.org/browse/SPR-5243</li>
 * <li>http://forum.springsource.org/showthread.php?86124-How-to-register-
 * BeanPostProcessor-programmatically</li>
 * </ul>
 * 
 * @author Ramon Li
 */

// @RunWith(SpringJUnit4ClassRunner.class)

// Refer to the doc of WebContextLoader.
@ContextConfiguration(loader = WebContextLoader.class, locations = {
        "classpath:spring-service.xml", "classpath:spring-dao.xml", "classpath:spring-test.xml" })
// this annonation defines the transaction manager for each test case.
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
// As our TEST extending from AbstractTransactionalJUnit4SpringContextTests,
// below 3 listeners have been registered by default, and it will be inherited
// by subclass.
// @TestExecutionListeners(listeners = {ShardAwareTestExecutionListener.class})
// @Transactional
public class BaseServletTests extends AbstractTransactionalJUnit4SpringContextTests {
	private static Logger logger = LoggerFactory.getLogger(BaseServletTests.class);

	/**
	 * Always auto wire the data source to a javax.sql.DataSource with name
	 * 'dataSource' even there are multple data sources. It means there must be
	 * a DataSource bean named 'dataSource'.
	 * <p>
	 * @see AbstractTransactionalJUnit4SpringContextTests#setDataSource(javax.sql.DataSource)
	 */
	@PersistenceContext(unitName = "lottery")
	protected EntityManager entityManager;

	protected DispatcherServlet servlet;

	// run once for current test suite.
	@BeforeClass
	public static void beforeClass() {
		logger.debug("@BeforeClass:beforeClass()");
	}

	/**
	 * logic to verify the initial state before a transaction is started.
	 * <p>
	 * The @BeforeTransaction methods declared in superclasses will be run after
	 * those of the current class.
	 */
	@BeforeTransaction
	public void verifyInitialDatabaseState() throws Exception {
		logger.debug("--------------------------------");
		logger.debug("@BeforeTransaction:verifyInitialDatabaseState()");

		logger.debug("EntityManager:" + this.entityManager);
		logger.debug("ApplicationContext:" + this.applicationContext);
		// Map<String, BeanPostProcessor> map =
		// this.applicationContext.getBeansOfType(BeanPostProcessor.class);
		ConfigurableApplicationContext ac = (ConfigurableApplicationContext) this.applicationContext;
		Map<String, BeanPostProcessor> map = ac.getBeanFactory().getBeansOfType(
		        BeanPostProcessor.class);
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			logger.debug("BeanPostProcessor - " + key + ":" + map.get(key));
		}
		if (map.isEmpty())
			logger.debug("No BeanPostProcessor found!");

		/*
		 * TODO: DispatcherServlet.init(servletCfg) will initialize a
		 * WebApplicationContext based on this.applicationContext which
		 * initialized by <code>WebContextLoader</code>. As the initialization
		 * is expensive, we don't want to do it for each test, but it is also
		 * hard to do it in @BeforeClass, as we can't refer to a instance
		 * variable(this.applicationContext) in a static method...How to fix it??
		 */
		// initializa DispatcherServlet
		MockServletContext servletCtx = new MockServletContext();
		// must set this ApplicationContext which loaded by spring test-context
		// framework to ServletContext, it will be the parent of the
		// ApplicationContext loaded by DispatcherServlet from XXX-servlet.xml.
		servletCtx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
		        this.applicationContext);
		MockServletConfig servletCfg = new MockServletConfig(servletCtx, "lottery");
		// set SpringMVC configration location...SpringMVC can translate the
		// init-param into a setter/getter call to DispatcherServlet
		// automatically.
		servletCfg.addInitParameter("contextConfigLocation", "META-INF/lottery-servlet.xml");
		servlet = new DispatcherServlet();
		// must call init() to initialize the context of DispatcherServlet, for
		// example initialize a WebApplicationContext based on XXX-servlet.xml.
		servlet.init(servletCfg);
	}

	/**
	 * set up test data within the transaction.
	 * <p>
	 * The @Before methods of superclasses will be run before those of the
	 * current class. No other ordering is defined.
	 */
	@Before
	public void setUpTestDataWithinTransaction() {
		logger.debug("@Before:setUpTestDataWithinTransaction()");

	}

	/**
	 * execute "tear down" logic within the transaction.
	 * <p>
	 * The @After methods declared in superclasses will be run after those of
	 * the current class.
	 */
	@After
	public void tearDownWithinTransaction() {
		logger.debug("@After:tearDownWithinTransaction()");
	}

	/**
	 * logic to verify the final state after transaction has rolled back.
	 * <p>
	 * The @AfterTransaction methods declared in superclasses will be run after
	 * those of the current class.
	 */
	@AfterTransaction
	public void verifyFinalDatabaseState() {
		logger.debug("@AfterTransaction:verifyFinalDatabaseState()");
		
		logger.debug("--------------------------------");
	}

	@AfterClass
	public static void afterClass() {
		logger.debug("@AfterClass:afterClass()");
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
