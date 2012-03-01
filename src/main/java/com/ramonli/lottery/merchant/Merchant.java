package com.ramonli.lottery.merchant;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="MERCHANT")
@Entity
public class Merchant implements java.io.Serializable, com.ramonli.lottery.core.Entity{
    private static final long serialVersionUID = 6139641558287268977L;

	@Id
	@Column(name="MERCHANT_ID")
	private long id;
	
	@Column(name="MERCHANT_NAME")
	private String name;
	
	@Column(name="MERCHANT_CODE")
	private String code;
	
	@Column(name="CREDIT_LIMIT_VALUE")
	private BigDecimal creditLimit;
	
	// -1 means dynamic credit level, only leaf merchant can be set to -1.
	@Column(name="CREDIT_LEVEL_VALUE")
	private BigDecimal creditLevel = new BigDecimal("0");
	
	@Column(name="STATUS")
	private int status;	// refer to Operator.status
	
	public Merchant(){}

	public Merchant(long id, String name, String code, BigDecimal creditLimit,
            BigDecimal creditLevel, int status) {
	    super();
	    this.id = id;
	    this.name = name;
	    this.code = code;
	    this.creditLimit = creditLimit;
	    this.creditLevel = creditLevel;
	    this.status = status;
    }

	public long getId() {
    	return id;
    }

	public void setId(long id) {
    	this.id = id;
    }

	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	public String getCode() {
    	return code;
    }

	public void setCode(String code) {
    	this.code = code;
    }

	public BigDecimal getCreditLimit() {
    	return creditLimit;
    }

	public void setCreditLimit(BigDecimal creditLimit) {
    	this.creditLimit = creditLimit;
    }

	public BigDecimal getCreditLevel() {
    	return creditLevel;
    }

	public void setCreditLevel(BigDecimal creditLevel) {
    	this.creditLevel = creditLevel;
    }

	public int getStatus() {
    	return status;
    }

	public void setStatus(int status) {
    	this.status = status;
    }

}
