package no.ntnu.assignmentsystem.services;

import java.util.List;
import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.UoD;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;

public class ServicesImpl extends Container implements Services {
//	private ServicesFactory servicesFactory;
	
	private static String mainCourseId = "tdt4100";
	
	private ModelLoader modelLoader;
	
	public ServicesImpl(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
		
	    ServicesPackage.eINSTANCE.eClass();
//	    servicesFactory = ServicesFactory.eINSTANCE;
	}

	@Override
	public List<AssignmentView> getAssignments(String userId) {
		return getCourseModel().getAssignments().stream().map(
			AssignmentViewFactory::createCourseView
		).collect(Collectors.toList());
	}

	@Override
	public List<ProblemView> getProblems(String assignmentId) {
		return getAssignmentModel(assignmentId).getProblems().stream().map(
			ProblemViewFactory::createProblemView
		).collect(Collectors.toList());
	}
	
	@Override
	public ExtendedProblemView getProblem(String assignmentId, String problemId) {
		Problem problem = getProblemModel(assignmentId, problemId);
		return ProblemViewFactory.createExtendedProblemView(problem);
	}
	
	private Problem getProblemModel(String assignmentId, String problemId) {
		return getAssignmentModel(assignmentId).getProblems().stream().filter(
			problem -> problem.getId().equals(problemId)
		).findAny().get();
	}
	
	private Assignment getAssignmentModel(String assignmentId) {
		return getCourseModel().getAssignments().stream().filter(
			assignment -> assignment.getId().equals(assignmentId)
		).findAny().get();
	}

	private Course getCourseModel() {
		return getUoD().getCourses().stream().filter(
			course -> course.getId().equals(mainCourseId)
		).findAny().get();
	}
	
	private UoD getUoD() {
		return modelLoader.getUoD();
	}
}
