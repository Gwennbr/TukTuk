package com.tuktukteam.tuktuk.model;

public class Coordonnee {
	
	private double latitude, longitude;
	
	public Coordonnee() {
		
	}
	
	public Coordonnee(double lat, double lng) {
		latitude = lat;
		longitude = lng;
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
	
	
}
