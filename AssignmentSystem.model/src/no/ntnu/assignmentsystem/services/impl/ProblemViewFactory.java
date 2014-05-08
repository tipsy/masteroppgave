package no.ntnu.assignmentsystem.services.impl;

import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.services.ExtendedProblemView;
import no.ntnu.assignmentsystem.services.ProblemView;
import no.ntnu.assignmentsystem.services.ServicesFactory;

class ProblemViewFactory {
	public static ProblemView createProblemView(Problem problem) {
		ProblemView problemView = factory().createProblemView();
		Mapper.copyAttributes(problem, problemView);
		return problemView;
	}
	
	public static ExtendedProblemView createExtendedProblemView(Problem problem) {
		ExtendedProblemView problemView = (problem instanceof CodeProblem) ? factory().createCodeProblemView() : factory().createQuizProblemView();
		Mapper.copyAttributes(problem, problemView);
		return problemView;
	}
	
	protected static ServicesFactory factory() {
		return ServicesFactory.eINSTANCE;
	}
}

