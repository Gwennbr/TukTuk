package com.tuktukteam.tuktuk.dao;

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
}
