package no.ntnu.assignmentsystem.editor.jdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.nio.file.Files;

import javax.swing.ProgressMonitorInputStream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public class ProjectManager {
	private static final String srcFolderName = "src";
	private static final String binFolderName = "bin";
	
	private final String projectName;
	
	private IProject _project;
	private IJavaProject _javaProject;
	private IPackageFragmentRoot _srcFolder;
	
	public ProjectManager(String projectName) {
		this.projectName = projectName;
	}
	
	public void updateSourceCode(String sourceCode) throws JavaModelException, CoreException {
		IPackageFragment fragment = getSrcFolder().createPackageFragment("example", true, null);

		String str = "package example;\n" +
				"\n" +
				"import java.util.*;\n" +
				"import java.io.File;\n" +
				"import java.io.IOException;\n" +
				"import java.nio.file.Files;\n" +
				"\n" +
				"public class HelloWorld {\n" +
				"  public static void main(String[] args) {\n" +
				"    System.out.println(\"Hello world\");\n" +
				"    try { Files.write(new File(\"/Users/skohorn/Downloads/test.txt\").toPath(), \"It was run!\".getBytes()); }\n" +
				"    catch (IOException e) { e.printStackTrace(); }\n" +
				"  }\n" +
				"}\n";
		
		fragment.createCompilationUnit("HelloWorld.java", str, false, null);
	}
	
	public String runMain() throws CoreException {
		return launch(getJavaProject(), "RunConfig", "example.HelloWorld");
	}


	// --- Private methods ---
	
	private String launch(IJavaProject javaProject, String configName, String main) throws CoreException {
		DebugPlugin plugin = DebugPlugin.getDefault();
		ILaunchManager launchManager = plugin.getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, configName);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, javaProject.getElementName());
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, main);
		ILaunchConfiguration launchConfiguration = workingCopy.doSave();   
		ILaunch launch = launchConfiguration.launch(ILaunchManager.RUN_MODE, null);
		
		while (!launch.isTerminated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}
		
		String output = "";
		
		IProcess[] processes = launch.getProcesses();
		for (IProcess process : processes) {
			String errorStream = process.getStreamsProxy().getErrorStreamMonitor().getContents();
			if (errorStream != null && errorStream.length() > 0) {
				output += errorStream;
			}
			
			String outputStream = process.getStreamsProxy().getOutputStreamMonitor().getContents();
			if (outputStream != null && outputStream.length() > 0) {
				output += outputStream;
			}
		}
		
		return output;
	}
	
	private IPackageFragmentRoot getSrcFolder() throws CoreException {
		if (_srcFolder == null) {
			IFolder folder = getProject().getFolder(srcFolderName);
			folder.create(true, true, null);
			
			_srcFolder = getJavaProject().getPackageFragmentRoot(folder);
		}
		
		return _srcFolder;
	}
	
	private IJavaProject getJavaProject() throws CoreException {
		if (_javaProject == null) {
			_javaProject = JavaCore.create(getProject());
			
			IClasspathEntry[] buildPath = {
				JavaCore.newSourceEntry(getProject().getFullPath().append(srcFolderName)),
				JavaRuntime.getDefaultJREContainerEntry()
			};
			
			_javaProject.setRawClasspath(buildPath, getProject().getFullPath().append(binFolderName), null);
		}
		
		return _javaProject;
	}
	
	private IProject getProject() throws CoreException {
		if (_project == null) {
			_project = getWorkspaceRoot().getProject(projectName);
			_project.create(null);
			_project.open(null);
	
			IProjectDescription description = _project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
			_project.setDescription(description, null);
		}
		
		return _project;
	}
	
	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
//private String launch(IJavaProject proj, String main) throws CoreException {
//	IVMInstall vm = JavaRuntime.getVMInstall(proj);
//	if (vm == null) { vm = JavaRuntime.getDefaultVMInstall(); }
//	IVMRunner vmr = vm.getVMRunner(ILaunchManager.RUN_MODE);
//	String[] cp = JavaRuntime.computeDefaultRuntimeClassPath(proj);
//	System.out.println("cp.length:" + cp.length);
//	System.out.println("cp[0]:" + cp[0]);
//	VMRunnerConfiguration config = new VMRunnerConfiguration(main, cp);
//	ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);
//	vmr.run(config, launch, null);
//	
//	while (!launch.isTerminated()) {
//		System.out.println("Is it running?");
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException ie) {
//		}
//	}
//    
//	IProcess[] processes = launch.getProcesses();
//	for (IProcess process : processes) {
//		String outputStream = process.getStreamsProxy().getOutputStreamMonitor().getContents();
//		if (outputStream != null && outputStream.length() > 0) {
//			System.out.println("Output stream:" + outputStream);
//		}
//		String errorStream = process.getStreamsProxy().getErrorStreamMonitor().getContents();
//		if (errorStream != null && errorStream.length() > 0) {
//			System.out.println("Error stream:" + errorStream);
//		}
//	}
//}
}
