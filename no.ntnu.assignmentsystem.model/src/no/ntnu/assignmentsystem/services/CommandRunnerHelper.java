package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import no.ntnu.assignmentsystem.services.coderunner.CommandRunner;
import no.ntnu.assignmentsystem.services.coderunner.RunCodeCommands;

public class CommandRunnerHelper {
	private static final String mainDirectoryName = "main";
	private static final String testDirectoryName = "test";
	
	private final File libDirectory;
	private final File outputRootDirectory;
	private final CommandRunner commandRunner;
	
	private RunCodeCommands runCodeCommands;
	
	public CommandRunnerHelper(CommandRunner commandRunner, File libDirectory, File outputRootDirectory) {
		this.libDirectory = libDirectory;
		this.outputRootDirectory = outputRootDirectory;
		this.commandRunner = commandRunner;
		
		runCodeCommands = new RunCodeCommands(createOutputSrcDirectory(), createOutputTestDirectory(), getLibFiles());
	}
	
	public String runCode(File srcDirectory, File mainImplementationFile, File[] implementationFiles) {
		try {
			String[] commands = runCodeCommands.getRunMainCommands(srcDirectory, mainImplementationFile, implementationFiles);
			return commandRunner.runCommandsAndWait(commands);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String runTests(File srcDirectory, File testDirectory, File[] implementationFiles, File[] testFiles) {
		try {
			String[] commands = runCodeCommands.getRunTestsCommands(srcDirectory, testDirectory, implementationFiles, testFiles);
			return commandRunner.runCommandsAndWait(commands);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	// --- Private methods ---
	
	private File[] getLibFiles() {
		try {
			return Files.walk(libDirectory.toPath()).map(
				path -> path.toFile()
			).filter(
				file -> file.getName().endsWith(".jar")
			).toArray(File[]::new);
		} catch (IOException e) {
			return new File[] {};
		}
	}
	
	private File createOutputSrcDirectory() {
		File outputSrcDirectory = new File(outputRootDirectory, mainDirectoryName);
		outputSrcDirectory.mkdirs();
		
		return outputSrcDirectory;
	}
	
	private File createOutputTestDirectory() {
		File outputTestDirectory = new File(outputRootDirectory, testDirectoryName);
		outputTestDirectory.mkdirs();
		
		return outputTestDirectory;
	}
}
