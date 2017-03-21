package com.tuktukteam.tuktuk.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="COND_ID", referencedColumnName="PER_ID")
public class Conducteur {
	
	@Column(name="COND_IBAN")
	private String iban;
	
	@Column(name="COND_BIC")
	private String bic;

	@Column(name="COND_LATITUDE")
	private double latitude;
	
	@Column(name="COND_LONGITUDE")
	private double longitude;
	
	@Column(name="COND_AVAILABLE")
	private boolean available;
}
