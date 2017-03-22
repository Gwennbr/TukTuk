package com.tuktukteam.tuktuk.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="course")
public class Course implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COU_ID")
	private int id;	
	
	@ManyToOne
	@JoinColumn(name="COU_ID_CLIENT")
	private Client client;
	
	@ManyToOne
	@JoinColumn(name="COU_ID_COND")
	private Conducteur conducteur;
	
	@Column(name="COU_VALIDE")
	private boolean valide;
	
	@Column(name="COU_ADRESSE_DEPART")
	private String adresseDepart;

	@Column(name="COU_DATE_DEBUT")
	private Date dateDebutCourse;
	
	@Column(name="COU_DATE_FIN")
	private Date dateFinCourse;
	
	@Column(name="COU_DISTANCE")
	private double distance;
	
	@Column(name="COU_PRIX")
	private float prix;
	
	@Column(name="COU_NOTE_CLIENT")
	private float noteClient;
	
	@Column(name="COU_COM_CLIENT")
	private String comClient;
	
	@Column(name="COU_NOTE_COND")
	private float noteConducteur;
	
	@Column(name="COU_COM_COND")
	private String comConducteur;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getAdresseDepart() {
		return adresseDepart;
	}

	public void setAdresseDepart(String adresseDepart) {
		this.adresseDepart = adresseDepart;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Conducteur getConducteur() {
		return conducteur;
	}

	public void setConducteur(Conducteur conducteur) {
		this.conducteur = conducteur;
	}

	public boolean isValide() {
		return valide;
	}

	public void setValide(boolean valide) {
		this.valide = valide;
	}

	public Date getDateDebutCourse() {
		return dateDebutCourse;
	}

	public void setDateDebutCourse(Date dateDebutCourse) {
		this.dateDebutCourse = dateDebutCourse;
	}

	public Date getDateFinCourse() {
		return dateFinCourse;
	}

	public void setDateFinCourse(Date dateFinCourse) {
		this.dateFinCourse = dateFinCourse;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public float getPrix() {
		return prix;
	}

	public void setPrix(float prix) {
		this.prix = prix;
	}

	public float getNoteClient() {
		return noteClient;
	}

	public void setNoteClient(float noteClient) {
		this.noteClient = noteClient;
	}

	public String getComClient() {
		return comClient;
	}

	public void setComClient(String comClient) {
		this.comClient = comClient;
	}

	public float getNoteConducteur() {
		return noteConducteur;
	}

	public void setNoteConducteur(float noteConducteur) {
		this.noteConducteur = noteConducteur;
	}

	public String getComConducteur() {
		return comConducteur;
	}

	public void setComConducteur(String comConducteur) {
		this.comConducteur = comConducteur;
	}	
}
