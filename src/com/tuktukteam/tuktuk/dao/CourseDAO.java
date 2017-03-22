package com.tuktukteam.tuktuk.dao;

import com.tuktukteam.genericdao.GenericDAO;
import com.tuktukteam.tuktuk.model.Course;

public class CourseDAO extends GenericDAO<Course, Integer> {

	public CourseDAO() {
		super(Course.class);
	}	
}
