package no.ntnu.assignmentsystem;

import java.io.File;

import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.impl.XmiModelLoader;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesImpl;

public class Main {
	public static void main(String[] args) {
//		try {
//			File from = new File("../no.hal.jex.collection/tests/tictactoe/TicTacToeTest.java");
//			File to = new File("../Output/runs/697b3f47-2c32-4cc9-9b3b-15bd2c24087f/src/tests/tictactoe/TicTacToeTest.java");
////			System.out.println(new File(".").getAbsolutePath());
////			if (to.isFile()) {
////				File toDirectory = to.getParentFile();
////				toDirectory.mkdirs();
//				Files.createDirectories(to.toPath().getParent());
//				Files.copy(from.toPath(), to.toPath());
////			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ModelLoader modelLoader = new XmiModelLoader(new File("model/UoD.xmi"));
		Services services = new ServicesImpl(modelLoader);
//		System.out.println(services.getAssignments("userId"));
//		System.out.println(services.getProblem("userId", "3")); // QuizProblem
//		System.out.println(services.getProblem("10", "4")); // CodeProblem

//		services.updateSourceCodeFile("10", "7", "Code");
//		System.out.println(((CodeProblemView)services.getProblem("10", "4")).getSourceCodeFiles()); // CodeProblem
		System.out.println(services.runCodeProblem("10", "4")); // CodeProblem
//		System.out.println(services.testCodeProblem("10", "4")); // CodeProblem
	}
}
