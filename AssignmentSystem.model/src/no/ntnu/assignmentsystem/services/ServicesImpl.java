package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.ntnu.assignmentsystem.model.Assignment;
import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.Course;
import no.ntnu.assignmentsystem.model.ImplementationFile;
import no.ntnu.assignmentsystem.model.ModelFactory;
import no.ntnu.assignmentsystem.model.ModelLoader;
import no.ntnu.assignmentsystem.model.ModifiedSourceCodeFile;
import no.ntnu.assignmentsystem.model.Participant;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.SourceCodeFile;
import no.ntnu.assignmentsystem.model.Student;
import no.ntnu.assignmentsystem.model.TestFile;
import no.ntnu.assignmentsystem.model.UoD;
import no.ntnu.assignmentsystem.model.User;
import no.ntnu.assignmentsystem.services.coderunner.CodeRunner;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import no.ntnu.assignmentsystem.services.mapping.AssignmentViewFactory;
import no.ntnu.assignmentsystem.services.mapping.ProblemViewFactory;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;
import org.eclipse.emf.ecore.resource.Resource;

public class ServicesImpl extends Container implements Services {
	private static final String COURSE_ID = "1";

	private final ModelLoader modelLoader;
	private final ServicesPackage servicesPackage;
	
	public ServicesImpl(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
		
		// Initialize services package
	    servicesPackage = ServicesPackage.eINSTANCE;
	    servicesPackage.eClass();
	}
	
	@Override
	public List<AssignmentView> getAssignments(String userId) {
		return getCourseModel(COURSE_ID).map(
			course -> course.getAssignments().stream().map(
					AssignmentViewFactory::createAssignmentView
			).collect(Collectors.toList())
		).orElse(null);
	}
	
	@Override
	public List<ProblemView> getProblems(String userId, String assignmentId) {
		return getAssignmentModel(assignmentId).map(
				assignment -> assignment.getProblems().stream().map(
					ProblemViewFactory::createProblemView
				).collect(Collectors.toList())
			).orElse(null);
	}
	
	@Override
	public ExtendedProblemView getProblem(String userId, String problemId) {
		return getProblemModel(problemId).map(
			ProblemViewFactory::createExtendedProblemView
		).orElse(null);
	}
	
	@Override
	public String runCodeProblem(String userId, String problemId) {
		return getProblemModel(problemId).map(problem -> {
			CodeProblem codeProblem = (CodeProblem)problem;
			
			File rootDirectory = new File(codeProblem.getRepoUrl());
			File srcDirectory = new File(rootDirectory, codeProblem.getSrcPath());
//			File testDirectory = new File(rootDirectory, codeProblem.getTestPath());
			
			File mainImplementationFile = new File(srcDirectory, codeProblem.getMainImplementationFile().getFilePath());
			
//			File[] implementationFiles = codeProblem.getSourceCodeFiles().stream().filter(
//				sourceCodeFile -> sourceCodeFile instanceof ImplementationFile
//			).map(
//				sourceCodeFile -> new File(srcDirectory, sourceCodeFile.getFilePath())
//			).toArray(File[]::new);
//			
//			File[] testFiles = codeProblem.getSourceCodeFiles().stream().filter(
//				sourceCodeFile -> sourceCodeFile instanceof TestFile
//			).map(
//				sourceCodeFile -> new File(testDirectory, sourceCodeFile.getFilePath())
//			).toArray(File[]::new);
			
			try {
				CodeRunner codeRunner = new CodeRunner(new DefaultRuntimeExecutor(), new File("../Test/bin/main"), new File("../Test/bin/test"), new File("../Test/junit.jar"));
				
				return codeRunner.runMain(srcDirectory, mainImplementationFile, new File[] {});
//				return codeRunner.runTests(srcDirectory, testDirectory, implementationFiles, testFiles);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	).orElse(null);
	}

	@Override
	public String testCodeProblem(String userId, String problemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateSourceCodeFile(String userId, String fileId, String sourceCode) {
		getParticipantModel(COURSE_ID, userId).ifPresent(participant -> {
			Student student = (Student)participant;
			
			ModifiedSourceCodeFile modifiedSourceCodeFile = student.getSourceCodeFiles().stream().filter(
				sourceCodeFile -> sourceCodeFile.getOriginalSourceCodeFile().getId().equals(fileId)
			).findAny().orElse(null);
			
			if (modifiedSourceCodeFile == null) {
				modifiedSourceCodeFile = getModelFactory().createModifiedSourceCodeFile();
				modifiedSourceCodeFile.setOriginalSourceCodeFile(getSourceCodeFileModel(fileId).get());
				student.getSourceCodeFiles().add(modifiedSourceCodeFile);
			}
			
			modifiedSourceCodeFile.setSourceCode(sourceCode);
			
			System.out.println(modifiedSourceCodeFile);
		});
	}
	
	
	// --- Private methods ---
	
	private Optional<? extends SourceCodeFile> getSourceCodeFileModel(String fileId) {
		return Optional.ofNullable((SourceCodeFile)getResource().getEObject(fileId));
	}
	
	private Optional<? extends Problem> getProblemModel(String problemId) {
		return Optional.ofNullable((Problem)getResource().getEObject(problemId));
	}
	
	private Optional<Assignment> getAssignmentModel(String assignmentId) {
		return Optional.ofNullable((Assignment)getResource().getEObject(assignmentId));
	}
	
	private Optional<Course> getCourseModel(String courseId) {
		return Optional.ofNullable((Course)getResource().getEObject(courseId));
	}

	private Optional<? extends Participant> getParticipantModel(String courseId, String userId) {
		return getCourseModel(courseId).flatMap(
			course -> course.getParticipants().stream().filter(
				participant -> getUserModel(userId).equals(Optional.ofNullable(participant.getUser()))
			).findAny()
		);
	}
	
	private Optional<User> getUserModel(String userId) {
		return Optional.ofNullable((User)getResource().getEObject(userId));
	}
	
	private UoD getUoD() {
		return modelLoader.getUoD();
	}
	
	private Resource getResource() {
		return modelLoader.getResource();
	}
	
	private ModelFactory getModelFactory() {
		return modelLoader.getFactory();
	}
}
