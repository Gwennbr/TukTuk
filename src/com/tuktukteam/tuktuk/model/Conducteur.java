package com.tuktukteam.tuktuk.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="conducteur")
@PrimaryKeyJoinColumn(name="COND_ID", referencedColumnName="PER_ID")
public class Conducteur extends Personne {
	private static final long serialVersionUID = 1L;

	@Column(name="COND_IBAN")
	private String iban;
	
	@Column(name="COND_BIC")
	private String bic;
	
	@Column(name="COND_NUM_IMMATRICULATION")
	private String numImmat;

	@Column(name="COND_LATITUDE")
	private double latitude;
	
	@Column(name="COND_LONGITUDE")
	private double longitude;
	
	@Column(name="COND_AVAILABLE")
	private boolean available;
	
	@OneToMany(mappedBy="conducteur")
	private List<Course> courses;

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	
	public String getNumImmat() {
		return numImmat;
	}

	public void setNumImmat(String numImmat) {
		this.numImmat = numImmat;
	}

	
}
