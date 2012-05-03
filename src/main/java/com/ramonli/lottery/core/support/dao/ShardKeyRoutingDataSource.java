package com.ramonli.lottery.core.support.dao;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardKeyRoutingDataSource extends AbstractRoutingDataSource {
	private Log logger = LogFactory.getLog(ShardKeyRoutingDataSource.class);

	@Override
	protected Object determineCurrentLookupKey() {
		return ShardKeyContextHolder.getShardKey();
	}

	@Override
	protected DataSource determineTargetDataSource() {
		DataSource ds = super.determineTargetDataSource();
		logger.info("Use lookupKey(" + this.determineCurrentLookupKey() + "), get data source: "
		        + ds);
		return ds;
	}
}
