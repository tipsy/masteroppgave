package no.ntnu.assignmentsystem.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import no.ntnu.assignmentsystem.services.coderunner.CodeRunner;
import no.ntnu.assignmentsystem.services.coderunner.RuntimeExecutor;

public class CodeRunnerHelper {
	private static final String mainDirectoryName = "main";
	private static final String testDirectoryName = "test";
	
	private final RuntimeExecutor runtimeExecutor;
	private final File libDirectory;
	private final File outputRootDirectory;
	
	private CodeRunner codeRunner;
	
	public CodeRunnerHelper(RuntimeExecutor runtimeExecutor, File libDirectory, File outputRootDirectory) {
		this.runtimeExecutor = runtimeExecutor;
		this.libDirectory = libDirectory;
		this.outputRootDirectory = outputRootDirectory;
	}
	
	public String runCode(File srcDirectory, File mainImplementationFile, File[] implementationFiles) {
		try {
			return getCodeRunner().runMain(srcDirectory, mainImplementationFile, implementationFiles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String runTests(File srcDirectory, File testDirectory, File[] implementationFiles, File[] testFiles) {
		try {
			return getCodeRunner().runTests(srcDirectory, testDirectory, implementationFiles, testFiles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	// --- Private methods ---
	
	private CodeRunner getCodeRunner() {
		if (codeRunner == null) {
			codeRunner = new CodeRunner(runtimeExecutor, createOutputSrcDirectory(), createOutputTestDirectory(), getLibFiles());
		}
		return codeRunner;
	}
	
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
