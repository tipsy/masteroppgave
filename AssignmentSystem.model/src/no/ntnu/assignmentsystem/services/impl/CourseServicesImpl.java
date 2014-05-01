package no.ntnu.assignmentsystem.services.impl;

import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.services.CourseServices;
import no.ntnu.assignmentsystem.services.CourseView;
import no.ntnu.assignmentsystem.services.ServicesFactory;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

public class CourseServicesImpl extends MinimalEObjectImpl implements
		CourseServices {
	private ServicesFactory servicesFactory;
	private EList<Course> courses;

	public CourseServicesImpl(ServicesFactory servicesFactory, EList<Course> courses) {
		this.servicesFactory = servicesFactory;
		this.courses = courses;
	}
	
	@Override
	public EList<CourseView> getCourses() {
		return courses.stream().map(course -> {
			CourseView courseView = servicesFactory.createCourseView();
			courseView.setTitle(course.getName());
			return courseView;
		}).collect(Collectors.toCollection(BasicEList::new));
	}

	@Override
	public void addCourse(String title) {
		// TODO Auto-generated method stub

	}

	@Override
	public CourseView getCourse(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCourse(String id) {
		// TODO Auto-generated method stub

	}

}
