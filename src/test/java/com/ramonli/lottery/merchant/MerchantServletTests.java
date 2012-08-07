package com.ramonli.lottery.merchant;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.ramonli.lottery.merchant.dao.MerchantDao;
import com.ramonli.lottery.test.BaseServletTests;

public class MerchantServletTests extends BaseServletTests {
	private static Logger logger = LoggerFactory.getLogger(MerchantServletTests.class);

	// Must declare @Autowired(by type) or @Resource(JSR-250)(by name)
	// explicitly, otherwise spring won't inject the dependency.
	@Autowired
	private MerchantDao merchantDao;

	@Before
	public void beforeInTransaction() {
		logger.debug("@Before:beforeInTransaction()");
		logger.debug("merchantDao:" + this.applicationContext.getBeansOfType(MerchantDao.class));
		logger.debug("merchantDao:"
		        + BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext,
		                MerchantDao.class));
	}

	@Test
	// overrides the class-level defaultRollback setting
	public void testQueryByCode() throws Exception {
		logger.debug("testQueryByCode::Enter!");

		MockHttpServletRequest req = new MockHttpServletRequest(servlet.getServletContext(), "GET",
		        "/merchant");
		MockHttpServletResponse resp = new MockHttpServletResponse();
		req.addHeader("X-Trans-Type", "200");
		// SpringMVC will use this header to determine whether the media-type is
		// supported or not, and choose a appropriate HttpMessageConvert to
		// convert message body.
		req.addHeader("Content-Type", "text/plain");
		req.setContent("M-1".getBytes());

		servlet.service(req, resp);
		assertEquals("M-111", resp.getContentAsString());
	}

//	@Test
////	@Rollback(false)
//	public void testUpdate() throws Exception {
//		logger.debug("testUpdate::Enter!");
//		MockHttpServletRequest req = new MockHttpServletRequest(servlet.getServletContext(), "PUT",
//		        "/merchant");
//		MockHttpServletResponse resp = new MockHttpServletResponse();
//		req.addHeader("X-Trans-Type", "201");
//
//		/**
//		 * SpringMVC will use this header to determine whether the media-type is
//		 * supported or not, and choose a appropriate HttpMessageConvert to
//		 * convert message body.
//		 * <p>
//		 * "application/x-www-form-urlencoded" is the default content type for
//		 * html form, refer to
//		 * http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1
//		 */
//		req.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		req.setContent("name=MM&code=M-1".getBytes());
//
//		servlet.service(req, resp);
//		
//		assertEquals(200, resp.getStatus());
//
//		/**
//		 * False Positive<br/>
//		 * -----------------------------------------------------<br/>
//		 * When you test code involving an ORM framework such as JPA or
//		 * Hibernate, flush the underlying session within test methods which
//		 * update the state of the session. Failing to flush the ORM framework's
//		 * underlying session can produce false positives: your test may pass,
//		 * but the same code throws an exception in a live, production
//		 * environment. For example, you call updateEntityInHibernateSession()
//		 * and end the test, the ORM framework won't flush session to underlying
//		 * database(a exception will be thrown out), so your test passes, false
//		 * positive raises.
//		 * <p>
//		 * Refer to
//		 * http://static.springsource.org/spring/docs/3.1.x/spring-framework
//		 * -reference/html/testing.html#testcontext-tx for more inforamtion.
//		 */
//		// this.entityManager.flush();
//		
//		
//		//assert database
//		Merchant merchant = this.getMerchantDao().getByCode("M-1");
//		assertEquals("MM", merchant.getName());
//	}
//	
//	@Test
////	@Rollback(false)
//	public void testCreate() throws Exception {
//		logger.debug("testCreate::Enter!");
//		MockHttpServletRequest req = new MockHttpServletRequest(servlet.getServletContext(), "POST",
//		        "/merchant");
//		MockHttpServletResponse resp = new MockHttpServletResponse();
//		req.addHeader("X-Trans-Type", "202");
//
//		/**
//		 * SpringMVC will use this header to determine whether the media-type is
//		 * supported or not, and choose a appropriate HttpMessageConvert to
//		 * convert message body.
//		 * <p>
//		 * "application/x-www-form-urlencoded" is the default content type for
//		 * html form, refer to
//		 * http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1
//		 */
//		req.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		req.setContent("name=M999&code=M-999&creditlevel=20000".getBytes());
//
//		servlet.service(req, resp);
//		
//		assertEquals(200, resp.getStatus());
//		
//		//assert database
//		Merchant merchant = this.getMerchantDao().getByCode("M-999");
//		assertEquals("M999", merchant.getName());
//		assertEquals(9999, merchant.getId());
//		assertEquals(20000.0, merchant.getCreditLimit().doubleValue(), 0);
//	}
	

	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

}
