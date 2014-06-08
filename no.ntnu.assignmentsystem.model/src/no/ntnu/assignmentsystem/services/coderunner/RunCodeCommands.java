package no.ntnu.assignmentsystem.services.coderunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;

public class RunCodeCommands {
	private static final String compileCommandFormat = "javac -g -d %s -classpath %s %s"; // Placeholders: output directory, classpaths, implementation files (g-flag includes extra debug information)
	private static final String runCommandFormat = "java -classpath %s %s"; // Placeholders: classpaths, main class name
	private static final String testCommandFormat = "java -classpath %s junit.textui.TestRunner %s"; // Placeholders: classpaths, test class names
	
	private final File srcOutputDirectory;
	private final File testOutputDirectory;
	private final File[] libFiles;
	
	public RunCodeCommands(File srcOutputDirectory, File testOutputDirectory, File[] libFiles) {
		if (srcOutputDirectory == null || testOutputDirectory == null || libFiles == null) {
			throw new IllegalArgumentException();
		}
		
		this.srcOutputDirectory = srcOutputDirectory;
		this.testOutputDirectory = testOutputDirectory;
		this.libFiles = libFiles;
	}
	
	public String[] runMain(File srcDirectory, File mainImplementationFile, File[] otherImplementationFiles) throws Exception {
		if (srcDirectory == null || mainImplementationFile == null || otherImplementationFiles == null) {
			throw new IllegalArgumentException();
		}
		
		File[] compileImplementationFilesClassPathFiles = {srcDirectory};
		File[] runMainClassPathFiles = {srcOutputDirectory};
		
		File[] allImplementationFiles = Stream.concat(Stream.of(mainImplementationFile), Arrays.stream(otherImplementationFiles)).toArray(File[]::new);
		
		String mainClassName = convertFilePathToClassName(srcDirectory, mainImplementationFile);
		
		String[] commands = {
			compileFiles(srcOutputDirectory, compileImplementationFilesClassPathFiles, allImplementationFiles),
			runMain(runMainClassPathFiles, mainClassName)
		};
		
		return commands;
	}
	
	public String[] runTests(File srcDirectory, File testDirectory, File[] implementationFiles, File[] testFiles) throws Exception {
		if (srcDirectory == null || testDirectory == null || implementationFiles.length < 1 || testFiles.length < 1) {
			throw new IllegalArgumentException();
		}
		
		File[] compileImplementationFilesClassPathFiles = {srcDirectory};
		File[] compileTestFilesClassPathFiles = Stream.concat(Stream.of(testDirectory, srcOutputDirectory), Arrays.stream(libFiles)).toArray(File[]::new);
		File[] runTestsClassPathFiles = Stream.concat(Stream.of(srcOutputDirectory, testOutputDirectory), Arrays.stream(libFiles)).toArray(File[]::new);
		
		String[] testClassNames = Arrays.stream(testFiles).map(
			testFile -> convertFilePathToClassName(testDirectory, testFile)
		).toArray(String[]::new);
		
		String[] compilationCommands = {
			compileFiles(srcOutputDirectory, compileImplementationFilesClassPathFiles, implementationFiles),
			compileFiles(testOutputDirectory, compileTestFilesClassPathFiles, testFiles)
		};
		String[] runTestsCommands = Arrays.stream(testClassNames).map(
			testClassName -> runTests(runTestsClassPathFiles, testClassName)
		).toArray(String[]::new);
		String[] commands = Stream.concat(Arrays.stream(compilationCommands), Arrays.stream(runTestsCommands)).toArray(String[]::new);
		
		return commands;
	}
	
	
	// --- Private methods ---
	
	private String compileFiles(File outputDirectory, File[] classPathFiles, File[] sourceCodeFiles) {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		String delimitedSourceCodeFilePaths = getDelimitedSourceCodeFilePaths(sourceCodeFiles);
		
		return String.format(compileCommandFormat, outputDirectory.getAbsolutePath(), delimitedClassPaths, delimitedSourceCodeFilePaths);
	}
	
	private String runMain(File[] classPathFiles, String mainClassName) {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		
		return String.format(runCommandFormat, delimitedClassPaths, mainClassName);
	}
	
	private String runTests(File[] classPathFiles, String testClassName) {
		String delimitedClassPaths = getDelimitedClassPaths(classPathFiles);
		
		return String.format(testCommandFormat, delimitedClassPaths, testClassName);
	}
	
	private static String getStringFromInputStream(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		return reader.lines().reduce("", (concatenatedText, nextLine) -> concatenatedText + System.lineSeparator() + nextLine);
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
	
	private static String convertFilePathToClassName(File rootDirectory, File sourceCodeFile) {
		// TODO: Make a more dynamic solution?
		// Read file from disk, find package and class name
		// Possible solutions:
		// - com.sun.javadoc (http://www.egtry.com/java/doclet/extract)
		// - javax.tools (http://www.beyondlinux.com/2011/07/20/3-steps-to-dynamically-compile-instantiate-and-run-a-java-class/)
		
		String relativePath = rootDirectory.toURI().relativize(sourceCodeFile.toURI()).getPath();
		return relativePath.replace(".java", "").replace("/", ".");
	}
}
