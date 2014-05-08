package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.ModelPackage;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.UoD;
import no.ntnu.assignmentsystem.services.AssignmentView;
import no.ntnu.assignmentsystem.services.ExtendedProblemView;
import no.ntnu.assignmentsystem.services.ProblemView;
import no.ntnu.assignmentsystem.services.Services;
import no.ntnu.assignmentsystem.services.ServicesPackage;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

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
