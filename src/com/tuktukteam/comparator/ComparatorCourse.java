package com.tuktukteam.comparator;

import java.util.Comparator;

import com.tuktukteam.tuktuk.model.Conducteur;
import com.tuktukteam.tuktuk.model.Course;
import com.tuktukteam.tuktuk.restcontroller.CourseRestController;

public class ComparatorCourse implements Comparator<Course>{
	
	private Conducteur c;
	
	public ComparatorCourse() {
		
	}
	
	public ComparatorCourse(Conducteur c) {
		this.c = c;
	}


	@Override
	public int compare(Course course1, Course course2) {		
		return CourseRestController.calculDistance(c, course1.getAdresseDepart()) - CourseRestController.calculDistance(c, course2.getAdresseDepart());
	}

}
