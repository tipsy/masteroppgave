package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ImplementationFile;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.SourceCodeFile;
import no.ntnu.assignmentsystem.model.TestFile;
import no.ntnu.assignmentsystem.model.UoD;
import no.ntnu.assignmentsystem.services.coderunner.CodeRunner;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import no.ntnu.assignmentsystem.services.mapping.AssignmentViewFactory;
import no.ntnu.assignmentsystem.services.mapping.ProblemViewFactory;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;

public class ServicesImpl extends Container implements Services {
	private static final String mainCourseId = "tdt4100";
	
//	private ServicesFactory servicesFactory;
	
	private final ModelLoader modelLoader;
	
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
				CodeProblem codeProblem = (CodeProblem)problem;
				
				File rootDirectory = new File(codeProblem.getRepoUrl());
				File srcDirectory = new File(rootDirectory, codeProblem.getSrcPath());
//				File testDirectory = new File(rootDirectory, codeProblem.getTestPath());
				
				File mainImplementationFile = new File(srcDirectory, codeProblem.getMainImplementationFile().getFilePath());
				
//				File[] implementationFiles = codeProblem.getSourceCodeFiles().stream().filter(
//					sourceCodeFile -> sourceCodeFile instanceof ImplementationFile
//				).map(
//					sourceCodeFile -> new File(srcDirectory, sourceCodeFile.getFilePath())
//				).toArray(File[]::new);
//				
//				File[] testFiles = codeProblem.getSourceCodeFiles().stream().filter(
//					sourceCodeFile -> sourceCodeFile instanceof TestFile
//				).map(
//					sourceCodeFile -> new File(testDirectory, sourceCodeFile.getFilePath())
//				).toArray(File[]::new);
				
				try {
					CodeRunner codeRunner = new CodeRunner(new DefaultRuntimeExecutor(), new File("../Test/bin/main"), new File("../Test/bin/test"), new File("../Test/junit.jar"));
					
					return codeRunner.runMain(srcDirectory, mainImplementationFile, new File[] {});
//					return codeRunner.runTests(srcDirectory, testDirectory, implementationFiles, testFiles);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
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
