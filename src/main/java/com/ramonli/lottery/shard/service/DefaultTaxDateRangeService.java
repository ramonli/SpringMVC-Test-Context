package com.ramonli.lottery.shard.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramonli.lottery.merchant.TaxDateRange;
import com.ramonli.lottery.merchant.dao.ITaxDateRangeDao;
import com.ramonli.lottery.merchant.service.MerchantService;

public class DefaultTaxDateRangeService implements ITaxDateRangeService {
	private static Logger logger = LoggerFactory.getLogger(DefaultTaxDateRangeService.class);
	private ITaxDateRangeDao taxDateRangeDao;
	private MerchantService merchantService;

	@Override
	public void update(String id, boolean throwExp) {
		logger.trace("update:Enter!");

		TaxDateRange range = this.get(id);
		range.setDescription(range.getDescription() + " - " + new Date());
		logger.debug("update tax_date_range.");
		this.getTaxDateRangeDao().update(range);
		
		String merchantCode = "M-1";
		logger.debug("Start to query merchant by code({})", merchantCode);
		this.getMerchantService().query(merchantCode);

		if (throwExp)
			throw new RuntimeException("try to rollbackc current transaction.");
		logger.trace("update:Quit!");
	}

	@Override
	public TaxDateRange get(String id) {
		logger.trace("get:Enter!");
//		logger.debug("Current shardKeyContext:" + ShardKeyContextHolder.getShardKey());

		TaxDateRange tdr = this.getTaxDateRangeDao().findById(id);
		logger.trace("get:Quit!");
		return tdr;
	}

	public ITaxDateRangeDao getTaxDateRangeDao() {
		return taxDateRangeDao;
	}

	public void setTaxDateRangeDao(ITaxDateRangeDao taxDateRangeDao) {
		this.taxDateRangeDao = taxDateRangeDao;
	}

	public MerchantService getMerchantService() {
    	return merchantService;
    }

	public void setMerchantService(MerchantService merchantService) {
    	this.merchantService = merchantService;
    }

}
