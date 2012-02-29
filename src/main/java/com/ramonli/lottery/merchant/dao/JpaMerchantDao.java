package com.ramonli.lottery.merchant.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import com.ramonli.lottery.merchant.Merchant;

public class JpaMerchantDao extends JpaDaoSupport implements MerchantDao {

	public Merchant getByCode(String code) throws DataAccessException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("merchantCode", code);
		List<Merchant> result = this.getJpaTemplate().findByNamedParams(
		        "from Merchant m where m.code=:merchantCode", params);
		if (result.size() > 0)
			return result.get(0);
		return null;
	}

	public void update(Merchant merchant) throws DataAccessException {
		this.getJpaTemplate().merge(merchant);
	}
}
