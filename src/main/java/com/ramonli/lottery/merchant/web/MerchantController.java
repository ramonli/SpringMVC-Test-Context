package com.ramonli.lottery.merchant.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/merchant")
public interface MerchantController {

	@RequestMapping(method = RequestMethod.GET, headers = "X-Trans-Type=200")
	String query(HttpServletRequest req, HttpServletResponse resp, String code);

}
