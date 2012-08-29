package com.ramonli.lottery.merchant.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.ramonli.lottery.core.support.dao.BaseJpaDao;
import com.ramonli.lottery.merchant.Merchant;

@Repository("merchantDao")
public class JpaMerchantDao extends BaseJpaDao implements MerchantDao {

	@SuppressWarnings("unchecked")
	public Merchant getByCode(String code) throws DataAccessException {
		/*
		 * Test @PersistenceUnit and @PersistenceContext
		 */
		logger.debug("****" + this.getJpaEntityManager());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merchantCode", code);
		List<Merchant> result = this.getJpaTemplate().findByNamedParams(
		        "from Merchant m where m.code=:merchantCode", params);
		if (result.size() > 0)
			return result.get(0);
		return null;
	}

	public void update(Merchant merchant) throws DataAccessException {
		/*
		 * if no @ID assigned to merchant, exception will be thrown out. If
		 * found entity by @ID, entity will be updated, otherwise insertion will
		 * be performed.
		 */
		this.getJpaTemplate().merge(merchant);
	}
}
