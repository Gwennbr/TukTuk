package com.tuktukteam.tuktuk.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="course")
public class Course {

	private int id;
	
	private Client client;
	
	private Conducteur conducteur;
	
	private boolean valide;
	
	private Date dateDebutCourse;
	
	private Date dateFinCourse;
	
	private double distance;
	
	private float prix;
	
	private float noteClient;
	
	private String comClient;
	
	private float noteConducteur;
	
	private String comConducteur;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
