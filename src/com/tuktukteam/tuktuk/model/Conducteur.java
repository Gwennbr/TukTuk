package com.tuktukteam.tuktuk.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.tuktukteam.genericdao.annotations.ColumnTag;

@Entity
@Table(name="conducteur")
@PrimaryKeyJoinColumn(name="COND_ID", referencedColumnName="PER_ID")
public class Conducteur extends Personne {
	private static final long serialVersionUID = 1L;

	@Column(name="COND_IBAN")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String iban;
	
	@Column(name="COND_BIC")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String bic;
	
	@Column(name="COND_NUM_IMMATRICULATION")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String numImmat;

	@Column(name="COND_LATITUDE")
	@ColumnTag({ColumnTag.FRONT_RESTRICTED, "coordonnees"})
	private double latitude;
	
	@Column(name="COND_LONGITUDE")
	@ColumnTag({ColumnTag.FRONT_RESTRICTED, "coordonnees"})
	private double longitude;
	
	@Column(name="COND_AVAILABLE")
	private boolean available;
	
	@Column(name="COND_DEBUT_PAUSE")
	private Long dateDebutPause;

	@OneToMany(mappedBy="conducteur")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private List<Course> courses;
	
	public Long getDateDebutPause() {
		return dateDebutPause;
	}

	public void setDateDebutPause(Long dateDebutPause) {
		this.dateDebutPause = dateDebutPause;
	}

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
