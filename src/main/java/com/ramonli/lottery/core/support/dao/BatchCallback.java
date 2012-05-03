package com.ramonli.lottery.core.support.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * A callback implementation to support JDBC batch operation, refer to {@link
 * PayoutDaoImpl#updateSettlementFlagByTransaction(List<Transaction>
 * transactions)}
 * 
 * @author Ramon Li
 */
public abstract class BatchCallback<T> {
	private List<T> parameters;

	public BatchCallback(List<T> parameters) {
		this.parameters = parameters;
	}

	public abstract String getQuery();

	public void assembleParameters(PreparedStatement ps) throws SQLException {
		for (T t : this.parameters) {
			this.setBatchParameter(ps, t);
			ps.addBatch();
		}
	}

	public abstract void setBatchParameter(PreparedStatement ps, T t) throws SQLException;
}
