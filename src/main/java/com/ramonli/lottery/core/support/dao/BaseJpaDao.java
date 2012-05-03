package com.ramonli.lottery.core.support.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Multiple entities which inherit from same superclass, each entity will maps
 * to different table.
 */
public class BaseJpaDao extends JpaDaoSupport {
	protected Log logger = LogFactory.getLog(BaseJpaDao.class);

	public void insert(Object entity) {
		this.getJpaTemplate().persist(entity);
	}

	public void update(Object entity) {
		this.getJpaTemplate().merge(entity);
	}

	public <T> T findById(Class<T> clazz, String id) {
		return this.getJpaTemplate().find(clazz, id);
	}

	/**
	 * Run SQL by direct JDBC access.
	 * <p>
	 * If you DAOs is managed by JPA context, DataSource won't be exposed to DAO
	 * directly. You can refer to the data source bean directly or try to
	 * retrieve it from <code>TransactionSynchronizationManager</code>. Once a
	 * transaction context has be setup, Spring will bind at least below to
	 * resource to current thread:
	 * <ul>
	 * <li>TransactionSynchronizationManager.bindResource(getDataSource(),
	 * conHolder);</li>
	 * <li>
	 * TransactionSynchronizationManager.bindResource(getEntityManagerFactory(),
	 * txObject.getEntityManagerHolder())</li>
	 * </ul>
	 * It means you can find DataSource by type checking from
	 * TransactionSynchronizationManager's resource key.
	 * 
	 * @param sql The sql which can apply to underlying database.
	 * @param callback A JDBC callback implementation.
	 * @param dataSource On which to get a underlying database connection.
	 * @return a entity list.
	 */
	protected final List queryList(String sql, JdbcQueryCallback callback, DataSource dataSource) {
		Connection conn = null;
		try {
			// we must get the connection from current entity manager, or
			// all data manipulation will be executed in a new transaction
			conn = DataSourceUtils.getConnection(dataSource);
			if (logger.isTraceEnabled()) {
				logger.trace("Get a connection:" + conn + " from current transaction context.");
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			callback.setParameter(ps);
			ResultSet rs = ps.executeQuery();
			List result = new ArrayList();
			while (rs.next()) {
				Object o = callback.objectFromRow(rs);
				if (o != null)
					result.add(o);
			}

			// Due to we get connection from current entity manager, the
			// transaction will be managed by entity manager.
			// conn.commit();
			rs.close();
			ps.close();

			return result;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	public static abstract class JdbcQueryCallback {

		/**
		 * Set parameters to prepared statement.
		 */
		public abstract void setParameter(PreparedStatement ps) throws SQLException;

		/**
		 * Assemble a object from current result set row.
		 */
		public abstract Object objectFromRow(ResultSet rs) throws SQLException;
	}

	/**
	 * A batch executing implementation to replace batch(final List<?>
	 * paramList)
	 */
	protected final void batch(BatchCallback batchCallback, DataSource dataSource) {
		// we must get the connection from current entity manager, or
		// all data manipulation will be executed in a new transaction
		Connection conn = null;
		try {
			conn = DataSourceUtils.getConnection(dataSource);
			if (logger.isDebugEnabled()) {
				logger.debug("Get a connection:" + conn + " from current transaction context.");
			}
			PreparedStatement ps = conn.prepareStatement(batchCallback.getQuery());
			batchCallback.assembleParameters(ps);
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}
}
