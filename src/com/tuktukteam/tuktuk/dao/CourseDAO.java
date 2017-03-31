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
		try {
			return entityManager.createQuery("FROM Course WHERE conducteur IS NULL", Course.class).getResultList();
		} catch (Exception e) {
			return null;
		}

	}

	public Course getActualCustomerRide(int id) {
		try {
			return entityManager
					.createQuery("FROM Course WHERE client.id = :idClient AND dateFinCourse IS NULL", Course.class)
					.setParameter("idClient", id).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public Course getActualDriverRide(int id) {
		try {
			return entityManager
					.createQuery("FROM Course WHERE conducteur.id = :idCond AND dateFinCourse IS NULL", Course.class)
					.setParameter("idCond", id).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Course> getCustomerCompletedRides(int id) {
		try {
			return entityManager
					.createQuery("FROM Course WHERE client.id = :idClient AND dateFinCourse IS NOT NULL", Course.class)
					.setParameter("idClient", id).getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	public List<Course> getDriverCompletedRides(int id) {
		try{
		return entityManager.createQuery("FROM Course WHERE conducteur.id = :idCond AND dateFinCourse IS NOT NULL", Course.class).setParameter("idCond", id).getResultList();
		}catch(Exception e){
			return null;
		}
	}
}
