package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import no.ntnu.assignmentsystem.model.User;
import no.ntnu.assignmentsystem.services.akka.WorkspaceActor;
import no.ntnu.assignmentsystem.services.coderunner.CommandRunner;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import no.ntnu.assignmentsystem.services.mapping.AssignmentViewFactory;
import no.ntnu.assignmentsystem.services.mapping.ProblemViewFactory;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MainServices extends Container implements Services {
	private static final String COURSE_ID = "1";

	private final ModelLoader modelLoader;
	private final ServicesPackage servicesPackage;
	
	private final File outputDirectory = new File("../Output/runs/" + UUID.randomUUID().toString());
	private final CommandRunner commandRunner = new CommandRunner(new DefaultRuntimeExecutor());
	private final CommandRunnerHelper codeRunnerHelper = new CommandRunnerHelper(commandRunner, new File("../Output/lib"), new File(outputDirectory, "target"));
	
	private final ActorSystem actorSystem = ActorSystem.create("AssignmentSystem");
	
	public MainServices(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
		
		// Initialize services package
	    servicesPackage = ServicesPackage.eINSTANCE;
	    servicesPackage.eClass();
	    
//		ActorSelection selection = system.actorSelection("akka.tcp://bundle-734-ActorSystem@127.0.0.1:2552/user/editor");
//		Future<ActorRef> future = selection.resolveOne(Timeout.intToTimeout(5));
//		selection.tell(new UpdateSourceCode(), ActorRef.noSender());
//		system.shutdown();
	}
	
	@Override
	public ActorRef createWorkspace(String userId, String problemId) {
		// TODO: Create a unique name for the actor (to be able to reuse it later)
		return actorSystem.actorOf(Props.create(WorkspaceActor.class, this, userId, problemId));
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
		return getParticipantModel(COURSE_ID, userId).flatMap(
			participant -> (Optional<List<ProblemView>>)getAssignmentModel(assignmentId).map(
				assignment -> assignment.getProblems().stream().map(
					problem -> ProblemViewFactory.createProblemView((Student)participant, problem)
				).collect(Collectors.toList())
			)
		).orElse(null);
	}
	
	@Override
	public ExtendedProblemView getProblem(String userId, String problemId) {
		return (ExtendedProblemView)getParticipantModel(COURSE_ID, userId).flatMap(
			participant -> getProblemModel(problemId).map(
				problem -> ProblemViewFactory.createExtendedProblemView((Student)participant, problem)
			)
		).orElse(null);
	}
	
	@Override
	public String runCodeProblem(String userId, String problemId) {
		return getParticipantModel(COURSE_ID, userId).flatMap(
			participant -> (Optional<String>)getProblemModel(problemId).map(problem -> {
				CodeProblem codeProblem = (CodeProblem)problem;
				
				File repoSourceDirectory = new File(codeProblem.getRepoUrl());
				File repoOutputDirectory = new File(outputDirectory, "repo");
				
				copyDirectory(repoSourceDirectory, repoOutputDirectory);
				replaceModifiedSourceCodeFiles((Student)participant, codeProblem, repoOutputDirectory);
				
				File srcDirectory = new File(repoOutputDirectory, codeProblem.getSrcPath());
				
				File mainImplementationFile = new File(repoOutputDirectory, codeProblem.getMainImplementationFile().getFilePath());
				
				File[] implementationFiles = codeProblem.getSourceCodeFiles().stream().filter(
					sourceCodeFile -> sourceCodeFile instanceof ImplementationFile
				).map(
					sourceCodeFile -> new File(repoOutputDirectory, sourceCodeFile.getFilePath())
				).toArray(File[]::new);
				
				return codeRunnerHelper.runCode(srcDirectory, mainImplementationFile, implementationFiles);
			})
		).orElse(null);
	}

	@Override
	public String testCodeProblem(String userId, String problemId) {
		return getParticipantModel(COURSE_ID, userId).flatMap(
			participant -> (Optional<String>)getProblemModel(problemId).map(problem -> {
				CodeProblem codeProblem = (CodeProblem)problem;
				
				File repoSourceDirectory = new File(codeProblem.getRepoUrl());
				File repoOutputDirectory = new File(outputDirectory, "repo");
				
				copyDirectory(repoSourceDirectory, repoOutputDirectory);
				replaceModifiedSourceCodeFiles((Student)participant, codeProblem, repoOutputDirectory);
				
				File srcDirectory = new File(repoOutputDirectory, codeProblem.getSrcPath());
				File testDirectory = new File(repoOutputDirectory, codeProblem.getTestPath());
				
				File[] implementationFiles = codeProblem.getSourceCodeFiles().stream().filter(
					sourceCodeFile -> sourceCodeFile instanceof ImplementationFile
				).map(
					sourceCodeFile -> new File(repoOutputDirectory, sourceCodeFile.getFilePath())
				).toArray(File[]::new);
				
				File[] testFiles = codeProblem.getSourceCodeFiles().stream().filter(
					sourceCodeFile -> sourceCodeFile instanceof TestFile
				).map(
					sourceCodeFile -> new File(repoOutputDirectory, sourceCodeFile.getFilePath())
				).toArray(File[]::new);
				
				return codeRunnerHelper.runTests(srcDirectory, testDirectory, implementationFiles, testFiles);
			})
		).orElse(null);
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
		});
	}
	
	@Override
	public List<LeaderboardEntryView> getOverallLeaderboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LeaderboardEntryView> getAssignmentLeaderboard(String assignmentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AchievementView> getAchievements(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// --- Private methods ---
	
	private Optional<? extends SourceCodeFile> getSourceCodeFileModel(String fileId) {
		return Optional.ofNullable((SourceCodeFile)findObject(fileId));
	}
	
	private Optional<? extends Problem> getProblemModel(String problemId) {
		return Optional.ofNullable((Problem)findObject(problemId));
	}
	
	private Optional<Assignment> getAssignmentModel(String assignmentId) {
		return Optional.ofNullable((Assignment)findObject(assignmentId));
	}
	
	private Optional<Course> getCourseModel(String courseId) {
		return Optional.ofNullable((Course)findObject(courseId));
	}

	private Optional<? extends Participant> getParticipantModel(String courseId, String userId) {
		return getCourseModel(courseId).flatMap(
			course -> course.getParticipants().stream().filter(
				participant -> getUserModel(userId).equals(Optional.ofNullable(participant.getUser()))
			).findAny()
		);
	}
	
	private Optional<User> getUserModel(String userId) {
		return Optional.ofNullable((User)findObject(userId));
	}
	
	private EObject findObject(String id) {
		return modelLoader.findObject(id);
	}
	
	private ModelFactory getModelFactory() {
		return modelLoader.getFactory();
	}
	
	private static void copyDirectory(File sourceDirectory, File targetDirectory) {
		try {
			Files.walk(sourceDirectory.toPath()).forEach(path -> {
				String relativePathString = sourceDirectory.toURI().relativize(path.toUri()).getPath();
				Path outputPath = targetDirectory.toPath().resolve(relativePathString);
				
				try {
					Files.createDirectories(outputPath.getParent());
					Files.copy(path, outputPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void replaceModifiedSourceCodeFiles(Student student, CodeProblem codeProblem, File targetDirectory) {
		codeProblem.getSourceCodeFiles().stream().forEach(sourceCodeFile -> {
			student.getSourceCodeFiles().stream().filter(
				modifiedSourceCodeFile -> modifiedSourceCodeFile.getOriginalSourceCodeFile().getId().equals(sourceCodeFile.getId())
			).findAny().ifPresent(modifiedSourceCodeFile -> {
				Path targetPath = new File(targetDirectory, sourceCodeFile.getFilePath()).toPath();
				
				try {
					Files.write(targetPath, modifiedSourceCodeFile.getSourceCode().getBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		});
	}
}
