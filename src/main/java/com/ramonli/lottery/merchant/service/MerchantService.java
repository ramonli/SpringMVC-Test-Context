package com.ramonli.lottery.merchant.service;

import com.ramonli.lottery.merchant.Merchant;

public interface MerchantService {
	
	Merchant query(String code);
	
	void update(Merchant merchant);
}
