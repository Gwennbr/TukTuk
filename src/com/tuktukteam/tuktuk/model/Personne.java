package com.tuktukteam.tuktuk.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.tuktukteam.genericdao.annotations.ColumnTag;
import com.tuktukteam.genericdao.annotations.FindByValues;
import com.tuktukteam.genericdao.annotations.HashedValue;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="personne")
@Inheritance(strategy=InheritanceType.JOINED)
@Data @NoArgsConstructor
public class Personne implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PER_ID") 
	private int id; 
	
	@Column(name="PER_NOM")
	private String nom;
	
	@Column(name="PER_PRENOM") 
	private String prenom;
	
	@Column(name="PER_TEL")
	private String tel;
	
	@Column(name="PER_MAIL")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String mail;
	
	@FindByValues @HashedValue
	@Column(name="PER_PASSWORD")
	@ColumnTag({ColumnTag.FRONT_RESTRICTED, "password"})
	private String password;
	
	/*
	@Lob @Type(type="org.hibernate.type.BlobType")
	@Column(name="PER_IMAGE")
	private Blob image;
	*/
	
	@FindByValues @Column(name="PER_USERNAME")
	@ColumnTag(ColumnTag.FRONT_RESTRICTED)
	private String username;
	
	@Column(name="PER_DATE_INSCRIPTION")
	private Date date_inscription;
	
	public Personne(String username, String password) {
		super();
		this.password = password;
		this.username = username;
	}

}
