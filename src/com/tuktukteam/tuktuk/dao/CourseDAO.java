package com.tuktukteam.tuktuk.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Course;

@Transactional
@Repository
public class CourseDAO extends GenericDAO<Course, Integer> {

	public CourseDAO() {
		super(Course.class);
	}	
	
	
	public List<Course> getRidesWithoutDriver() {
		return entityManager.createQuery("FROM Course WHERE conducteur IS NULL", Course.class).getResultList();		
	}
	
	public Course getActualCustomerRide(int id) {
		return entityManager.createQuery("FROM Course WHERE client.id = :idClient AND dateFinCourse IS NULL", Course.class).setParameter("idClient", id).getSingleResult();
	}
	
	public Course getActualDriverRide(int id) {
		return entityManager.createQuery("FROM Course WHERE conducteur.id = :idCond AND dateFinCourse IS NULL", Course.class).setParameter("idCond", id).getSingleResult();
	}
	
	public List<Course> getCustomerCompletedRides(int id) {
		return entityManager.createQuery("FROM Course WHERE client.id = :idClient AND dateFinCourse IS NOT NULL", Course.class).setParameter("idClient", id).getResultList();
	}
	
	public List<Course> getDriverCompletedRides(int id) {
		return entityManager.createQuery("FROM Course WHERE conducteur.id = :idCond AND dateFinCourse IS NOT NULL", Course.class).setParameter("idCond", id).getResultList();
	}
	
	
}
