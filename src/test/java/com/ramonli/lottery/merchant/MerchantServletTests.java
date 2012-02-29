package com.ramonli.lottery.merchant;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.ramonli.lottery.merchant.dao.MerchantDao;
import com.ramonli.lottery.test.WebContextLoader;

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

// As our TEST is extended from AbstractTransactionalJUnit4SpringContextTests,
// no need to state @RunWith explicitly.
// @RunWith(SpringJUnit4ClassRunner.class)

// If run test against a web interface, for instance SpringMVC
// DispatcherServlet, a
// WebApplicationContext instance must be initialized, not default
// GenericApplicationContext.
// Here WebContextLoader will delegate request to
// org.springframework.web.context.ContextLoader
// to initialize a WebApplicationContext(refer to ContextLoaderListener).
@ContextConfiguration(loader = WebContextLoader.class, locations = {
        "classpath:spring-service.xml", "classpath:spring-dao.xml", "classpath:spring-test.xml" })
// @ContextConfiguration(loader = WebContextLoader.class, locations = {
// "classpath:spring-dao.xml"})
// this annonation defines the transaction manager for each test case.
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
// As our TEST extending from AbstractTransactionalJUnit4SpringContextTests,
// below 3 listeners have been registered by default, and it will be inherited
// by subclass.
// @TestExecutionListeners(listeners = {
// TransactionalTestExecutionListener.class,
// ShardAwareTestExecutionListener.class })
// As our TEST is extended from AbstractTransactionalJUnit4SpringContextTests,
// no need to state @Transactional explicitly.
// @Transactional
public class MerchantServletTests extends AbstractTransactionalJUnit4SpringContextTests {
	private Logger logger = LoggerFactory.getLogger(MerchantServletTests.class);

	// Must declare @Autowired(by type) or @Resource(JSR-250)(by name)
	// explicitly, otherwise spring won't inject the dependency.
	@Autowired
	private MerchantDao merchantDao;

	/**
	 * Always auto wire the data source to a javax.sql.DataSource with name
	 * 'dataSource' even there are multple data sources. It means there must be
	 * a DataSource bean named 'dataSource'.
	 * <p>
	 * @see AbstractTransactionalJUnit4SpringContextTests#setDataSource(javax.sql.DataSource)
	 */
	@PersistenceContext(unitName = "lottery")
	protected EntityManager entityManager;

	@BeforeTransaction
	public void verifyInitialDatabaseState() {
		// logic to verify the initial state before a transaction is started
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

		logger.debug("merchantDao:" + this.merchantDao);
	}

	// call for each test case.
	@Before
	public void setUpTestDataWithinTransaction() {
		// set up test data within the transaction
		logger.debug("@Before:setUpTestDataWithinTransaction()");

	}

	@Test
	public void testQueryByCode() throws Exception{
		logger.debug("testQueryByCode::Enter!");
		// have a try first
		MockServletContext servletCtx = new MockServletContext();
		servletCtx.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
		        this.applicationContext);
		MockServletConfig servletCfg = new MockServletConfig(servletCtx, "lottery");
		// set SpringMVC configration location 
		servletCfg.addInitParameter("contextConfigLocation", "META-INF/lottery-servlet.xml");
		DispatcherServlet servlet = new DispatcherServlet();
		servlet.init(servletCfg);
		
		MockHttpServletRequest req = new MockHttpServletRequest(servletCtx, "GET", "/merchant");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		req.addHeader("X-Trans-Type", "200");
		req.addHeader("Content-Type", "text/plain");
		req.setContent("hello!".getBytes());
		
		servlet.service(req, resp);
		assertEquals("M-111", resp.getContentAsString());
	}

//	@Test
//	public void testUpdate() {
//		logger.debug("testUpdate::Enter!");
//	}

	@After
	public void tearDownWithinTransaction() {
		// execute "tear down" logic within the transaction.
		logger.debug("@After:tearDownWithinTransaction()");
	}

	@AfterTransaction
	public void verifyFinalDatabaseState() {
		// logic to verify the final state after transaction has rolled back
		logger.debug("@AfterTransaction:verifyFinalDatabaseState()");

	}
}
