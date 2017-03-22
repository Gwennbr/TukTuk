package com.tuktukteam.tuktuk.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="client")
@PrimaryKeyJoinColumn(name="CLI_ID", referencedColumnName="PER_ID")
public class Client extends Personne {
	private static final long serialVersionUID = 1L;

	@Column(name="CLI_NUM_CB")
	private String numeroCarteBancaire;
	
	@Column(name="CLI_PICTOGRAMME")
	private String pictogramme;

	@Column(name="CLI_DATE_VALIDITE_CB")
	private Date dateValiditeCB;
	
	@OneToMany(mappedBy="client")
	private List<Course> courses;

	
	
	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

	public String getNumeroCarteBancaire() {
		return numeroCarteBancaire;
	}

	public void setNumeroCarteBancaire(String numeroCarteBancaire) {
		this.numeroCarteBancaire = numeroCarteBancaire;
	}

	public String getPictogramme() {
		return pictogramme;
	}

	public void setPictogramme(String pictogramme) {
		this.pictogramme = pictogramme;
	}

	public Date getDateValiditeCB() {
		return dateValiditeCB;
	}

	public void setDateValiditeCB(Date dateValiditeCB) {
		this.dateValiditeCB = dateValiditeCB;
	}
	
	
	
	
}
