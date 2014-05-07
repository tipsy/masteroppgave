package no.ntnu.assignmentsystem.services.impl;

import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.services.CodeProblemView;
import no.ntnu.assignmentsystem.services.ExtendedProblemView;
import no.ntnu.assignmentsystem.services.ProblemView;
import no.ntnu.assignmentsystem.services.QuizProblemView;

class ProblemViewFactory {
	public static ProblemView createProblemView(Problem problem) {
		ProblemView problemView = new ProblemViewImpl();
		problemView.setId(problem.getId());
		problemView.setTitle(problem.getTitle());
		return problemView;
	}
	
	public static ExtendedProblemView createExtendedProblemView(Problem problem) {
		ExtendedProblemView problemView = (problem instanceof CodeProblem) ? createCodeProblemView(problem) : createQuizProblemView(problem);
		
		problemView.setId(problem.getId());
		problemView.setTitle(problem.getTitle());
		
		return problemView;
	}
	
	private static CodeProblemView createCodeProblemView(Problem problem) {
		CodeProblemView problemView = new CodeProblemViewImpl();
		// TOOD: Set attributes
		return problemView;
	}
	
	private static QuizProblemView createQuizProblemView(Problem problem) {
		QuizProblemView problemView = new QuizProblemViewImpl();
		// TOOD: Set attributes
		return problemView;
	}
}

