package no.ntnu.assignmentsystem.model;

import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.impl.ServicesImpl;

public class Main {
	public static void main(String[] args) {
		Services services = new ServicesImpl();
		System.out.println(services.getCourseServices().getCourses());
	}
}
