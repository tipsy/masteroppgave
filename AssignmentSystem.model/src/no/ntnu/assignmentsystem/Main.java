package no.ntnu.assignmentsystem;

import java.io.File;

import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoaderImpl;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesImpl;

public class Main {
	public static void main(String[] args) {
		ModelLoader modelLoader = new XmiModelLoaderImpl(new File("model/UoD.xmi"));
		Services services = new ServicesImpl(modelLoader);
		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
//		System.out.println(services.runCodeProblem("userId", "4")); // CodeProblem
//		services.updateSourceCodeFile("10", "5", "Code");
	}
}
