package no.ntnu.assignmentsystem.model;

import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.impl.ServicesImpl;

public class Main {
	public static void main(String[] args) {
		Services services = new ServicesImpl(new java.io.File("model/UoD.xmi"));
//		System.out.println(services.getAssignments(""));
		System.out.println(services.getProblem("1", "b"));
	}
}
