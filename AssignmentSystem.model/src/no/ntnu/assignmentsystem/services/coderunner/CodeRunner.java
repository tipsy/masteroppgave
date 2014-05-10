package no.ntnu.assignmentsystem.services.coderunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

public class CodeRunner {
	private static String compileCommandFormat = "javac -g -d %s -classpath %s %s"; // output directory, classpaths, implementation files (g-flag includes extra debug information)
	private static String runCommandFormat = "java -classpath %s %s"; // classpaths, class name
	private static String testCommandFormat = "java -classpath %s junit.textui.TestRunner %s"; // classpaths, test files
	
	private RuntimeExecutor executor;
	private File srcOutputDirectory;
	private File testOutputDirectory;
	private File jUnitFile;
	
	public CodeRunner(RuntimeExecutor executor, File srcOutputDirectory, File testOutputDirectory, File jUnitFile) {
		if (executor == null || srcOutputDirectory == null || testOutputDirectory == null || jUnitFile == null) {
			throw new IllegalArgumentException();
		}
		
		this.executor = executor;
		this.srcOutputDirectory = srcOutputDirectory;
		this.testOutputDirectory = testOutputDirectory;
		this.jUnitFile = jUnitFile;
	}
	
	public String runMain(File srcRoot, File mainImplementationFile, File[] otherImplementationFiles, String mainClassName) throws IOException {
		if (srcRoot == null || mainImplementationFile == null || otherImplementationFiles == null || mainClassName == null) {
			throw new IllegalArgumentException();
		}
		
		File[] classPathFiles = {srcRoot};
		File[] mainImplementationFiles = {mainImplementationFile};
		File[] implementationFiles = Stream.concat(Arrays.stream(mainImplementationFiles), Arrays.stream(otherImplementationFiles)).toArray(File[]::new);
		Process compileImplementationFilesProcess = compileFiles(srcOutputDirectory, classPathFiles, implementationFiles);
		
		File[] generatedClassPathFiles = {srcOutputDirectory};
		Process runMainProcess = runMain(generatedClassPathFiles, mainClassName);
		
		List<InputStream> inputStreams = Arrays.asList(
			compileImplementationFilesProcess.getErrorStream(),
			compileImplementationFilesProcess.getInputStream(),
			runMainProcess.getErrorStream(),
			runMainProcess.getInputStream()
		);
		InputStream combinedStream = new SequenceInputStream(new Vector<InputStream>(inputStreams).elements());
		return convertInputStreamToString(combinedStream);
	}
	
	public String runTests(File srcRoot, File testRoot, File[] implementationFiles, File[] testFiles, String[] testClassNames) throws IOException {
		if (srcRoot == null || testRoot == null || implementationFiles.length < 1 || testFiles.length < 1 || testClassNames.length < 1) {
			throw new IllegalArgumentException();
		}
		
		File[] classPathFiles = {srcRoot, testRoot, jUnitFile};
		Process compileImplementationFilesProcess = compileFiles(srcOutputDirectory, classPathFiles, implementationFiles);
		Process compileTestFilesProcess = compileFiles(testOutputDirectory, classPathFiles, testFiles);
		
		File[] generatedTestFiles = {srcOutputDirectory, testOutputDirectory, jUnitFile};
		Process runTestsProcess = runTests(generatedTestFiles, testClassNames);
		
		List<InputStream> inputStreams = Arrays.asList(
			compileImplementationFilesProcess.getErrorStream(),
			compileImplementationFilesProcess.getInputStream(),
			compileTestFilesProcess.getErrorStream(),
			compileTestFilesProcess.getInputStream(),
			runTestsProcess.getErrorStream(),
			runTestsProcess.getInputStream()
		);
		InputStream combinedStream = new SequenceInputStream(new Vector<InputStream>(inputStreams).elements());
		return convertInputStreamToString(combinedStream);
	}
	
	
	// --- Private methods ---
	
	private Process compileFiles(File outputDirectory, File[] classPathFiles, File[] sourceCodeFiles) throws IOException {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		String delimitedSourceCodeFilePaths = getDelimitedSourceCodeFilePaths(sourceCodeFiles);
		
		String command = String.format(compileCommandFormat, outputDirectory.getAbsolutePath(), delimitedClassPaths, delimitedSourceCodeFilePaths);
		
		return executor.exec(command);
	}
	
	private Process runMain(File[] classPathFiles, String mainClassName) throws IOException {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		
		String command = String.format(runCommandFormat, delimitedClassPaths, mainClassName);
		
		return executor.exec(command);
	}
	
	private Process runTests(File[] classPathFiles, String[] testClassNames) throws IOException {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		String delimitedTestClassNames = String.join(" ", testClassNames);
		
		String command = String.format(testCommandFormat, delimitedClassPaths, delimitedTestClassNames);

		return executor.exec(command);
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
	    ArrayList<String> output = new ArrayList<>();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.add(line);
	    }
	    reader.close();
	    
	    return String.join(System.lineSeparator(), output);
	}
	
	private static String getDelimitedClassPaths(File[] classPathFiles) {
		String[] classPaths = Arrays.stream(classPathFiles).map(
			file -> file.getAbsolutePath()
		).toArray(String[]::new);
		
		return String.join(":", classPaths);
	}
	
	private static String getDelimitedSourceCodeFilePaths(File[] sourceCodeFiles) {
		String[] sourceCodeFilePaths = Arrays.stream(sourceCodeFiles).map(
			file -> file.getAbsolutePath()
		).toArray(String[]::new);
		
		return String.join(" ", sourceCodeFilePaths);
	}
}
