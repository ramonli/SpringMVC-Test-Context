package com.ramonli.lottery.merchant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramonli.lottery.merchant.Merchant;
import com.ramonli.lottery.merchant.dao.MerchantDao;

public class DefaultMerchantService implements MerchantService {
	private Logger logger = LoggerFactory.getLogger(DefaultMerchantService.class);
	private MerchantDao merchantDao;

	@Override
	public Merchant query(String code) {
		logger.trace("query:Enter!");
		Merchant merchant = this.getMerchantDao().getByCode(code);
		logger.trace("query:Quit!");
		return merchant;
	}

	@Override
	public void merge(Merchant merchant) {
		// TODO merge client Merchant to JPA context first
		this.getMerchantDao().update(merchant);
	}

	public MerchantDao getMerchantDao() {
    	return merchantDao;
    }

	public void setMerchantDao(MerchantDao merchantDao) {
    	this.merchantDao = merchantDao;
    }

}
