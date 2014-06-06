package no.ntnu.assignmentsystem.services.akka;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import no.ntnu.assignmentsystem.model.CodeProblem;
import no.ntnu.assignmentsystem.model.ImplementationFile;
import no.ntnu.assignmentsystem.model.Problem;
import no.ntnu.assignmentsystem.model.User;
import no.ntnu.assignmentsystem.services.CodeRunnerHelper;
import no.ntnu.assignmentsystem.services.akka.messages.RunCode;
import no.ntnu.assignmentsystem.services.akka.messages.RunCodeResult;
import no.ntnu.assignmentsystem.services.coderunner.DefaultRuntimeExecutor;
import akka.actor.UntypedActor;

public class WorkspaceActor extends UntypedActor {
	private final User user;
	private final Problem problem;
	
	private final File outputDirectory = new File("../Output/runs/" + UUID.randomUUID().toString());
	private final CodeRunnerHelper codeRunnerHelper = new CodeRunnerHelper(new DefaultRuntimeExecutor(), new File("../Output/libs"), new File(outputDirectory, "target"));
	
	public WorkspaceActor(User user, Problem problem) {
		this.user = user;
		this.problem = problem;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof RunCode) {
			String result = runCode();
			getSender().tell(new RunCodeResult(result), getSelf());
		}
	}
	
	private String runCode() {
		CodeProblem codeProblem = (CodeProblem)problem;
		
		File repoSourceDirectory = new File(codeProblem.getRepoUrl());
		File repoOutputDirectory = new File(outputDirectory, "repo");
		
		copyDirectory(repoSourceDirectory, repoOutputDirectory);
//		replaceModifiedSourceCodeFiles((Student)participant, codeProblem, repoOutputDirectory);
		
		File srcDirectory = new File(repoOutputDirectory, codeProblem.getSrcPath());
		
		File mainImplementationFile = new File(repoOutputDirectory, codeProblem.getMainImplementationFile().getFilePath());
		
		File[] implementationFiles = codeProblem.getSourceCodeFiles().stream().filter(
			sourceCodeFile -> sourceCodeFile instanceof ImplementationFile
		).map(
			sourceCodeFile -> new File(repoOutputDirectory, sourceCodeFile.getFilePath())
		).toArray(File[]::new);
		
		return codeRunnerHelper.runCode(srcDirectory, mainImplementationFile, implementationFiles);
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
	
//	private static void replaceModifiedSourceCodeFiles(Student student, CodeProblem codeProblem, File targetDirectory) {
//		codeProblem.getSourceCodeFiles().stream().forEach(sourceCodeFile -> {
//			student.getSourceCodeFiles().stream().filter(
//				modifiedSourceCodeFile -> modifiedSourceCodeFile.getOriginalSourceCodeFile().getId().equals(sourceCodeFile.getId())
//			).findAny().ifPresent(modifiedSourceCodeFile -> {
//				Path targetPath = new File(targetDirectory, sourceCodeFile.getFilePath()).toPath();
//				
//				try {
//					Files.write(targetPath, modifiedSourceCodeFile.getSourceCode().getBytes());
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			});
//		});
//	}
}
