package no.ntnu.assignmentsystem.editor.jdt;

import java.io.IOException;

import no.ntnu.assignmentsystem.editor.akka.AkkaTestRunListener;

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
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

public class WorkspaceManager {
	private static final String srcFolderName = "src";
	private static final String binFolderName = "bin";
	private static final String runMainConfigName = "Run Main";
	private static final String runTestsConfigName = "Run Tests";
	
	private final String projectName;
	
	
	private IProject _project;
	private IJavaProject _javaProject;
	private IPackageFragmentRoot _srcFolder;
	private ILaunchConfiguration _runMainLaunchConfiguration;
	private ILaunchConfiguration _runTestsLaunchConfiguration;
	
	public WorkspaceManager(String projectName) {
		this.projectName = projectName;
		
		JUnitCore.addTestRunListener(new AkkaTestRunListener());
	}
	
	public void updateSourceCode(String packageName, String fileName, String sourceCode) throws JavaModelException, CoreException, IOException {
		IPackageFragment packageFragment = getSrcFolder().createPackageFragment(packageName, true, null);
		packageFragment.createCompilationUnit(fileName, sourceCode, true, null);
	}
	
	public String runMain(String qualifiedClassName) throws CoreException {
		ILaunchConfiguration launchConfiguration = getRunMainLaunchConfiguration(qualifiedClassName);
		return launch(launchConfiguration);
	}
	
	public String runTests(String qualifiedClassName) throws CoreException {
		//JUnitLaunchConfigurationConstants "org.eclipse.jdt.junit.launchconfig"
		//https://github.com/hallvard/jexercise/blob/master/no.hal.jex.ui/src/no/hal/jex/ui/JexManager.java
		ILaunchConfiguration launchConfiguration = getRunTestsLaunchConfiguration(qualifiedClassName);
		return launch(launchConfiguration);
	}


	// --- Private methods ---
	
	private String launch(ILaunchConfiguration launchConfiguration) throws CoreException {
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
	
	private ILaunchConfiguration getRunMainLaunchConfiguration(String qualifiedClassName) throws CoreException {
		if (_runMainLaunchConfiguration == null) {
			_runMainLaunchConfiguration = createLaunchConfiguration(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION, getJavaProject().getElementName(), runMainConfigName, qualifiedClassName);
		}
		
		return _runMainLaunchConfiguration;
	}
	
	private ILaunchConfiguration getRunTestsLaunchConfiguration(String qualifiedClassName) throws CoreException {
		if (_runTestsLaunchConfiguration == null) {
			_runTestsLaunchConfiguration = createLaunchConfiguration("org.eclipse.jdt.junit.launchconfig", getJavaProject().getElementName(), runTestsConfigName, qualifiedClassName);
		}
		
		return _runTestsLaunchConfiguration;
	}
	
	private static ILaunchConfiguration createLaunchConfiguration(String launchConfigurationTypeId, String projectName, String configName, String qualifiedClassName) throws CoreException {
		ILaunchConfigurationType launchConfigurationType = getLaunchManager().getLaunchConfigurationType(launchConfigurationTypeId);
		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, configName);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, qualifiedClassName);
		return workingCopy.doSave();
	}
	
	private static ILaunchManager getLaunchManager() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		return debugPlugin.getLaunchManager();
	}

	private IJavaProject getJavaProject() throws CoreException {
		if (_javaProject == null) {
			_javaProject = JavaCore.create(getProject());
			
			IClasspathEntry[] buildPath = {
				JavaRuntime.getDefaultJREContainerEntry(),
				JavaCore.newContainerEntry(JUnitCore.JUNIT3_CONTAINER_PATH),
//				JavaCore.newLibraryEntry(path, null, null)
				JavaCore.newSourceEntry(getProject().getFullPath().append(srcFolderName))
			};
			
			_javaProject.setRawClasspath(buildPath, getProject().getFullPath().append(binFolderName), null);
		}
		
		return _javaProject;
	}
	
	private IPackageFragmentRoot getSrcFolder() throws CoreException {
		if (_srcFolder == null) {
			IFolder folder = getProject().getFolder(srcFolderName);
			folder.create(true, true, null);
			
			_srcFolder = getJavaProject().getPackageFragmentRoot(folder);
		}
		
		return _srcFolder;
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
}
