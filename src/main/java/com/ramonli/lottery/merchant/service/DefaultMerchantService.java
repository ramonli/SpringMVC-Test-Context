package com.ramonli.lottery.merchant.service;

import com.ramonli.lottery.merchant.Merchant;
import com.ramonli.lottery.merchant.dao.MerchantDao;

public class DefaultMerchantService implements MerchantService {
	private MerchantDao merchantDao;

	@Override
	public Merchant query(String code) {
		return this.getMerchantDao().getByCode(code);
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
