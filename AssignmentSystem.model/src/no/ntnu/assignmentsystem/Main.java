package no.ntnu.assignmentsystem;

import java.io.File;

import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.CodeProblemView;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesImpl;

public class Main {
	public static void main(String[] args) {
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		Services services = new ServicesImpl(modelLoader);
//		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
		System.out.println(services.getProblem("10", "4")); // CodeProblem

		services.updateSourceCodeFile("10", "7", "Code");
		System.out.println(((CodeProblemView)services.getProblem("10", "4")).getSourceCodeFiles()); // CodeProblem
//		System.out.println(services.runCodeProblem("userId", "4")); // CodeProblem
//		System.out.println(services.testCodeProblem("userId", "4")); // CodeProblem
	}
}
