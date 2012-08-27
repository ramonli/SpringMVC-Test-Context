package com.ramonli.lottery.merchant.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ramonli.lottery.merchant.Merchant;
import com.ramonli.lottery.merchant.service.MerchantService;

@Controller
@RequestMapping(value = "/merchant")
public class DefaultMerchantController implements MerchantController{
	private final Logger logger = LoggerFactory.getLogger(DefaultMerchantController.class);

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private MerchantService merchantService;
	
	public @ResponseBody
	String query(HttpServletRequest req, HttpServletResponse resp, @RequestBody String code) {
		logger.debug("query::ENTER!");
		Merchant merchant = this.getMerchantService().query(code);
		return merchant.getName();
	}

	@RequestMapping(method = RequestMethod.PUT, headers = "X-Trans-Type=201")
	public void update(HttpServletRequest req, HttpServletResponse resp,
	        @RequestBody MultiValueMap<String, String> formParams) {
		logger.debug("update::ENTER!");
		logger.debug("request: name=" + formParams.getFirst("name") + ",code=" + formParams.getFirst("code"));
		Merchant merchant = new Merchant();
		merchant.setId(111);
		merchant.setCode(formParams.getFirst("code"));
		merchant.setName(formParams.getFirst("name"));
		
		this.getMerchantService().merge(merchant);
	}

	@RequestMapping(method = RequestMethod.POST, headers = "X-Trans-Type=202")
	public @ResponseBody Merchant create(HttpServletRequest req, HttpServletResponse resp,
	        @RequestBody Merchant merchant) {
		logger.debug("update::ENTER!");
		logger.debug("request: name=" + merchant.getName() + ",code=" + merchant.getCode());
		// assign a ID to entity
		merchant.setId(9999);
		
		this.getMerchantService().merge(merchant);
		
		return merchant;
	}	
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public MerchantService getMerchantService() {
		return merchantService;
	}

	public void setMerchantService(MerchantService merchantService) {
		this.merchantService = merchantService;
	}

}
