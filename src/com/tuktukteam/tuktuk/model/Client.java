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
@Table(name="client")
@PrimaryKeyJoinColumn(name="CLI_ID", referencedColumnName="PER_ID")
@Data @NoArgsConstructor @EqualsAndHashCode(callSuper=true)
public class Client extends Personne {
	private static final long serialVersionUID = 1L;

	@Column(name="CLI_NUM_CB")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String numeroCarteBancaire;
	
	@Column(name="CLI_PICTOGRAMME") 
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String pictogramme;

	@Column(name="CLI_MOIS_VALIDITE_CB") 
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private Byte moisValiditeCB;
	
	@Column(name="CLI_ANNEE_VALIDITE_CB") 
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private Short anneeValiditeCB;
	
	@OneToMany(mappedBy="client") 
	@JsonIgnore
	private List<Course> courses;
	
}
