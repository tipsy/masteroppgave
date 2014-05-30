package no.ntnu.assignmentsystem.services.mapping;

import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.services.ExtendedProblemView;
import no.ntnu.assignmentsystem.services.ProblemView;

public class ProblemViewFactory extends BaseViewFactory {
	public static ProblemView createProblemView(Problem problem) {
		ProblemView problemView = getFactory().createProblemView();
		Mapper.copyAttributes(problem, problemView);
		return problemView;
	}
	
	public static ExtendedProblemView createExtendedProblemView(Problem problem) {
		ExtendedProblemView problemView = (problem instanceof CodeProblem) ? getFactory().createCodeProblemView() : getFactory().createQuizProblemView();
		Mapper.copyAttributes(problem, problemView);
		return problemView;
	}
}
