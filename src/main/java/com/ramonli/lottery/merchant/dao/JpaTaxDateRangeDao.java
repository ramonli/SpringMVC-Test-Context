package com.ramonli.lottery.merchant.dao;

import com.ramonli.lottery.core.support.dao.BaseJpaDao;
import com.ramonli.lottery.merchant.TaxDateRange;

public class JpaTaxDateRangeDao extends BaseJpaDao implements ITaxDateRangeDao {

	@Override
	public TaxDateRange findById(String id) {
		return this.getJpaTemplate().find(TaxDateRange.class, id);
	}

	@Override
	public void update(TaxDateRange entity) {
		this.getJpaTemplate().merge(entity);
	}

}
