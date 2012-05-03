package com.ramonli.lottery.merchant.dao;

import com.ramonli.lottery.merchant.TaxDateRange;

public interface ITaxDateRangeDao {

	TaxDateRange findById(String id);

	void update(TaxDateRange entity);

}
