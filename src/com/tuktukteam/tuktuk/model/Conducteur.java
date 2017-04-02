package com.tuktukteam.tuktuk.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tuktukteam.genericdao.annotations.ColumnTag;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name="conducteur")
@PrimaryKeyJoinColumn(name="COND_ID", referencedColumnName="PER_ID")
@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
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
	@JsonIgnore
	private List<Course> courses;
	
}
