package no.ntnu.assignmentsystem;

import java.io.File;
import java.util.UUID;

import org.eclipse.emf.ecore.util.EcoreUtil;

import antlr.ByteBuffer;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesImpl;

public class Main {
	public static void main(String[] args) {
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		Services services = new ServicesImpl(modelLoader);
//		System.out.println(services.getAssignments(""));
		System.out.println(services.getProblem("userId", "3")); // QuizProblem
		System.out.println(services.runCodeProblem("userId", "4")); // CodeProblem
	}
}
