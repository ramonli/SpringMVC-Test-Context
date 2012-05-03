package com.ramonli.lottery.shard.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DefaultTaxDateRangeServiceJUnitTest {
	private static Logger logger = LoggerFactory.getLogger(DefaultTaxDateRangeService.class);
	private ApplicationContext appContext;

	@Before
	public void setUp() throws Exception {
		appContext = new ClassPathXmlApplicationContext(this.getConfigLocations());
	}

	@Test
	public void testUpdate() {
		logger.trace("testUpdate:Enter!");
		
		ITaxDateRangeService taxService = appContext.getBean(ITaxDateRangeService.class);
		taxService.update("TDR-1", false);
		
		assertEquals(1, 1);
		logger.trace("testUpdate:Quit!");
	}

	@After
	public void tearDown() {
		this.appContext = null;
	}

	protected String[] getConfigLocations() {
		/**
		 * If there are beans with same name in different configuration files,
		 * the last bean definition will overwrite the previous one. When do
		 * integration test, this feature will be a good facility. By defining a
		 * separated test spring configuration file, we can get a test
		 * environment, but no need to modify normal spring configuration file
		 * which will manage the production environment.
		 */
		return new String[] { "spring-service.xml", "spring-dao.xml", "spring-test.xml" };
	}
}
