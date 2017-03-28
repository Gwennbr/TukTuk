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
}
