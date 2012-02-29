package com.ramonli.lottery.merchant.dao;

import org.springframework.dao.DataAccessException;

import com.ramonli.lottery.merchant.Merchant;

public interface MerchantDao {
	
	Merchant getByCode(String code) throws DataAccessException;
	
	void update(Merchant merchant) throws DataAccessException;
}
