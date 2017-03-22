package com.tuktukteam.tuktuk.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.mysql.jdbc.Blob;
import com.tuktukteam.genericdao.annotations.HashedValue;

@Entity
@Table(name="personne")
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
	
	@Column(name="PER_MAIL")
	private String mail;
	
	@HashedValue
	@Column(name="PER_PASSWORD")
	private String password;
	
	@Lob @Type(type="org.hibernate.type.BlobType")
	@Column(name="PER_IMAGE")
	private Blob image;
	
	@Column(name="PER_USERNAME")
	private String username;
	
	@Column(name="PER_DATE_INSCRIPTION")
	private Date date_inscription;
	
	public Personne() {
		
	}
	
	public Personne(String username, String password) {
		super();
		this.password = password;
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDate_inscription() {
		return date_inscription;
	}

	public void setDate_inscription(Date date_inscription) {
		this.date_inscription = date_inscription;
	}
}
