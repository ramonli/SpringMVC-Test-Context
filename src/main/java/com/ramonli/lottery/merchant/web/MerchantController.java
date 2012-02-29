package com.ramonli.lottery.merchant.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ramonli.lottery.merchant.dao.MerchantDao;

@Controller
@RequestMapping(value = "/merchant")
public class MerchantController {
	private final Logger logger = LoggerFactory.getLogger(MerchantController.class);

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private MerchantDao merchantDao;

	@RequestMapping(method = RequestMethod.GET, headers = "X-Trans-Type=200")
	public @ResponseBody
	String query(HttpServletRequest req, HttpServletResponse resp, @RequestBody String message) {
		logger.debug("query::ENTER!");
		logger.debug("applicationContext:" + this.applicationContext);
		logger.debug("merchantDao:" + this.applicationContext.getBeansOfType(MerchantDao.class));
		logger.debug("merchantDao:"
		        + BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext,
		                MerchantDao.class));
		logger.debug("merchantDao:" + this.getMerchantDao());
		try {
			resp.getWriter().println("I am fucking happy!");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return "M-111";
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

}
