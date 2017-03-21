package com.tuktukteam.tuktuk.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="CLI_ID", referencedColumnName="PER_ID")
public class Client {
	
	@Column(name="CLI_NUM_CB")
	private int num�roCarteBancaire;
	
	@Column(name="CLI_PICTOGRAMME")
	private int pictogramme;

	@Column(name="CLI_DATE_VALIDITE_CB")
	private Date dateValiditeCB;

	public int getNum�roCarteBancaire() {
		return num�roCarteBancaire;
	}

	public void setNum�roCarteBancaire(int num�roCarteBancaire) {
		this.num�roCarteBancaire = num�roCarteBancaire;
	}

	public int getPictogramme() {
		return pictogramme;
	}

	public void setPictogramme(int pictogramme) {
		this.pictogramme = pictogramme;
	}

	public Date getDateValiditeCB() {
		return dateValiditeCB;
	}

	public void setDateValiditeCB(Date dateValiditeCB) {
		this.dateValiditeCB = dateValiditeCB;
	}
	
	
}
