package no.ntnu.assignmentsystem.services.impl;

import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.services.CourseView;

public class CourseViewFactory {
	public static CourseView createCourseView(Course course) {
		CourseView courseView = new CourseViewImpl();
		courseView.setId(course.getId());
		courseView.setTitle(course.getTitle());
		return courseView;
	}
}
