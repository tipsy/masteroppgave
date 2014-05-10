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
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeRunner {
	private static final String compileCommandFormat = "javac -g -d %s -classpath %s %s"; // Placeholders: output directory, classpaths, implementation files (g-flag includes extra debug information)
	private static final String runCommandFormat = "java -classpath %s %s"; // Placeholders: classpaths, main class name
	private static final String testCommandFormat = "java -classpath %s junit.textui.TestRunner %s"; // Placeholders: classpaths, test class names
	
	private static final int normalExitCode = 0;
	
	private final RuntimeExecutor executor;
	private final File srcOutputDirectory;
	private final File testOutputDirectory;
	private final File jUnitFile;
	
	public CodeRunner(RuntimeExecutor executor, File srcOutputDirectory, File testOutputDirectory, File jUnitFile) {
		if (executor == null || srcOutputDirectory == null || testOutputDirectory == null || jUnitFile == null) {
			throw new IllegalArgumentException();
		}
		
		this.executor = executor;
		this.srcOutputDirectory = srcOutputDirectory;
		this.testOutputDirectory = testOutputDirectory;
		this.jUnitFile = jUnitFile;
	}
	
	public String runMain(File srcDirectory, File mainImplementationFile, File[] otherImplementationFiles) throws Exception {
		if (srcDirectory == null || mainImplementationFile == null || otherImplementationFiles == null) {
			throw new IllegalArgumentException();
		}
		
		File[] compileImplementationFilesClassPathFiles = {srcDirectory};
		File[] runMainClassPathFiles = {srcOutputDirectory};
		
		File[] allImplementationFiles = Stream.concat(Stream.of(mainImplementationFile), Arrays.stream(otherImplementationFiles)).toArray(File[]::new);
		
		String mainClassName = convertFilePathToClassName(srcDirectory, mainImplementationFile);
		
		List<Callable<Process>> commands = Arrays.asList(
			(Callable<Process>) () -> compileFiles(srcOutputDirectory, compileImplementationFilesClassPathFiles, allImplementationFiles),
			(Callable<Process>) () -> runMain(runMainClassPathFiles, mainClassName)
		);
		
		return runCommands(commands);
	}
	
	public String runTests(File srcDirectory, File testDirectory, File[] implementationFiles, File[] testFiles) throws Exception {
		if (srcDirectory == null || testDirectory == null || implementationFiles.length < 1 || testFiles.length < 1) {
			throw new IllegalArgumentException();
		}
		
		File[] compileImplementationFilesClassPathFiles = {srcDirectory};
		File[] compileTestFilesClassPathFiles = {testDirectory, srcOutputDirectory, jUnitFile};
		File[] runTestsClassPathFiles = {srcOutputDirectory, testOutputDirectory, jUnitFile};
		
		List<Callable<Process>> commands = Arrays.asList(
			(Callable<Process>) () -> compileFiles(srcOutputDirectory, compileImplementationFilesClassPathFiles, implementationFiles),
			(Callable<Process>) () -> compileFiles(testOutputDirectory, compileTestFilesClassPathFiles, testFiles)
		);
		
		Arrays.stream(testFiles).map(
			testFile -> convertFilePathToClassName(testDirectory, testFile)
		).forEach(testClassName -> {
			commands.add((Callable<Process>) () -> runTests(runTestsClassPathFiles, testClassName));
		});
		
		return runCommands(commands);
	}
	
	
	// --- Private methods ---
	
	private static String runCommands(List<Callable<Process>> commands) throws Exception {
		List<Process> processes = new ArrayList<>();
		for (Callable<Process> callable : commands) {
			Process process = callable.call();
			processes.add(process);
			
			if (process.waitFor() != normalExitCode) {
				break;
			}
		}
		
		return getStringFromProcesses(processes);
	}
	
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
	
	private Process runTests(File[] classPathFiles, String testClassName) throws IOException {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		
		String command = String.format(testCommandFormat, delimitedClassPaths, testClassName);
		
		return executor.exec(command);
	}
	
	private static String getStringFromProcesses(List<Process> processes) throws IOException {
		List<InputStream> inputStreams = processes.stream().flatMap(
			process -> Arrays.asList(process.getErrorStream(), process.getInputStream()).stream()
		).collect(Collectors.toList());
		
		InputStream combinedStream = new SequenceInputStream(new Vector<InputStream>(inputStreams).elements());
		return convertInputStreamToString(combinedStream);
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		return reader.lines().reduce("", (concatenatedText, nextLine) -> concatenatedText + System.lineSeparator() + nextLine);
	}
	
	private static String getDelimitedClassPaths(File[] classPathFiles) {
		return Arrays.stream(classPathFiles).map(
			file -> file.getAbsolutePath()
		).reduce("", (concatenatedText, nextString) -> concatenatedText + ":" + nextString);
	}
	
	private static String getDelimitedSourceCodeFilePaths(File[] sourceCodeFiles) {
		return Arrays.stream(sourceCodeFiles).map(
			file -> file.getAbsolutePath()
		).reduce("", (concatenatedText, nextString) -> concatenatedText + " " + nextString);
	}
	
	private static String convertFilePathToClassName(File rootFile, File sourceCodeFile) {
		// TODO: Make a more dynamic solution?
		// Read file from disk, find package and class name
		// Possible solutions:
		// - com.sun.javadoc (http://www.egtry.com/java/doclet/extract)
		// - javax.tools (http://www.beyondlinux.com/2011/07/20/3-steps-to-dynamically-compile-instantiate-and-run-a-java-class/)
		
		String relativePath = rootFile.toURI().relativize(sourceCodeFile.toURI()).getPath();
		return relativePath.replace(".java", "").replace("/", ".");
	}
}
