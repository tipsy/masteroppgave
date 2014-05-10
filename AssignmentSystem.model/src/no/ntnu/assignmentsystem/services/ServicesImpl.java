package no.ntnu.assignmentsystem.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.UoD;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;

public class ServicesImpl extends Container implements Services {
	private static String mainCourseId = "tdt4100";
	
//	private ServicesFactory servicesFactory;
	private CodeRunner codeRunner = new CodeRunner();
	
	private ModelLoader modelLoader;
	
	public ServicesImpl(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
		
	    ServicesPackage.eINSTANCE.eClass();
//	    servicesFactory = ServicesFactory.eINSTANCE;
	}

	@Override
	public List<AssignmentView> getAssignments(String userId) {
		return getCourseModel().map(
			course -> course.getAssignments().stream().map(
				AssignmentViewFactory::createCourseView
			).collect(Collectors.toList())
		).orElse(null);
	}

	@Override
	public List<ProblemView> getProblems(String assignmentId) {
		return getAssignmentModel(assignmentId).map(
			assignment -> assignment.getProblems().stream().map(
				ProblemViewFactory::createProblemView
			).collect(Collectors.toList())
		).orElse(null);
	}
	
	@Override
	public ExtendedProblemView getProblem(String assignmentId, String problemId) {
		return getProblemModel(assignmentId, problemId).map(
			ProblemViewFactory::createExtendedProblemView
		).orElse(null);
	}
	
	@Override
	public String runProblem(String assignmentId, String problemId) {
		return getProblemModel(assignmentId, problemId).map(problem -> {
				return problem.toString();
			}
		).orElse(null);
	}
	
	
	// --- Private methods ---
	
	private Optional<Problem> getProblemModel(String assignmentId, String problemId) {
		return getAssignmentModel(assignmentId).flatMap(
			assignment -> assignment.getProblems().stream().filter(
				problem -> problem.getId().equals(problemId)
			).findAny()
		);
	}
	
	private Optional<Assignment> getAssignmentModel(String assignmentId) {
		return getCourseModel().flatMap(
			course -> course.getAssignments().stream().filter(
				assignment -> assignment.getId().equals(assignmentId)
			).findAny()
		);
	}
	
	private Optional<Course> getCourseModel() {
		return getUoD().getCourses().stream().filter(
			course -> course.getId().equals(mainCourseId)
		).findAny();
	}
	
	private UoD getUoD() {
		return modelLoader.getUoD();
	}
}
