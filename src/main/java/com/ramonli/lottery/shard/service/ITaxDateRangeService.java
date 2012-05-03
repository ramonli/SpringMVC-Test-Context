package com.ramonli.lottery.shard.service;

import com.ramonli.lottery.merchant.TaxDateRange;

public interface ITaxDateRangeService {
	
	/**
	 * Update the underlying tax date range.
	 * 
	 * @param tdr	The entity id.
	 * @param throwExp	Whehter throw a runtime exception? to result in a transaction rollback. 
	 */
	void update(String id, boolean throwExp);
	
	TaxDateRange get(String id);

}
