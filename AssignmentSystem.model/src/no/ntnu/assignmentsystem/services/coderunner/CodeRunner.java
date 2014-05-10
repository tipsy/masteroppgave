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
	private static String compileCommandFormat = "javac -g -d %s -classpath %s %s"; // Placeholders: output directory, classpaths, implementation files (g-flag includes extra debug information)
	private static String runCommandFormat = "java -classpath %s %s"; // Placeholders: classpaths, main class name
	private static String testCommandFormat = "java -classpath %s junit.textui.TestRunner %s"; // Placeholders: classpaths, test class names
	
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
	
	public String runMain(File srcDirectory, File mainImplementationFile, File[] otherImplementationFiles) throws IOException, InterruptedException {
		if (srcDirectory == null || mainImplementationFile == null || otherImplementationFiles == null) {
			throw new IllegalArgumentException();
		}
		
		File[] classPathFiles = {srcDirectory};
		File[] mainImplementationFiles = {mainImplementationFile};
		File[] implementationFiles = Stream.concat(Arrays.stream(mainImplementationFiles), Arrays.stream(otherImplementationFiles)).toArray(File[]::new);
		Process compileImplementationFilesProcess = compileFiles(srcOutputDirectory, classPathFiles, implementationFiles);
		compileImplementationFilesProcess.waitFor();
		
		File[] generatedClassPathFiles = {srcOutputDirectory};
		String mainClassName = convertFilePathToClassName(srcDirectory, mainImplementationFile);
		Process runMainProcess = runMain(generatedClassPathFiles, mainClassName);
		runMainProcess.waitFor();
		
		List<InputStream> inputStreams = Arrays.asList(
			compileImplementationFilesProcess.getErrorStream(),
			compileImplementationFilesProcess.getInputStream(),
			runMainProcess.getErrorStream(),
			runMainProcess.getInputStream()
		);
		InputStream combinedStream = new SequenceInputStream(new Vector<InputStream>(inputStreams).elements());
		return convertInputStreamToString(combinedStream);
	}
	
	public String runTests(File srcDirectory, File testDirectory, File[] implementationFiles, File[] testFiles) throws IOException, InterruptedException {
		if (srcDirectory == null || testDirectory == null || implementationFiles.length < 1 || testFiles.length < 1) {
			throw new IllegalArgumentException();
		}
		
		File[] implementationClassPathFiles = {srcDirectory};
		Process compileImplementationFilesProcess = compileFiles(srcOutputDirectory, implementationClassPathFiles, implementationFiles);
		compileImplementationFilesProcess.waitFor();
		
		File[] testClassPathFiles = {testDirectory, srcOutputDirectory, jUnitFile};
		Process compileTestFilesProcess = compileFiles(testOutputDirectory, testClassPathFiles, testFiles);
		compileTestFilesProcess.waitFor();
		
		List<InputStream> inputStreams = new ArrayList<InputStream>();
		inputStreams.addAll(Arrays.asList(
			compileImplementationFilesProcess.getErrorStream(),
			compileImplementationFilesProcess.getInputStream(),
			compileTestFilesProcess.getErrorStream(),
			compileTestFilesProcess.getInputStream()
		));
		
		String[] testClassNames = Arrays.stream(testFiles).map(
				testFile -> convertFilePathToClassName(testDirectory, testFile)
		).toArray(String[]::new);
		
		for (String testClassName : testClassNames) {
			File[] generatedClassPathFiles = {srcOutputDirectory, testOutputDirectory, jUnitFile};
			Process runTestsProcess = runTests(generatedClassPathFiles, testClassName);
			runTestsProcess.waitFor();
			
			inputStreams.add(runTestsProcess.getErrorStream());
			inputStreams.add(runTestsProcess.getInputStream());
		}
		
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
	
	private Process runTests(File[] classPathFiles, String testClassName) throws IOException {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		
		String command = String.format(testCommandFormat, delimitedClassPaths, testClassName);
		System.out.println(command);
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
