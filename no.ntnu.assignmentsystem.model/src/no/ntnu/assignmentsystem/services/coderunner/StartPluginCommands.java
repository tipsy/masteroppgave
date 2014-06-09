package no.ntnu.assignmentsystem.services.coderunner;

import java.io.File;

public class StartPluginCommands {
	private static final String startPluginCommandFormat = "java -jar %s -eLog -nosplash -noExit -application %s"; // Placeholders: launcher, plugin name
	
	private final File launcher;
	private final String pluginName;
	
	public StartPluginCommands(File launcher, String pluginName) {
		this.launcher = launcher;
		this.pluginName = pluginName;
	}
	
	public String getStartPluginCommand(String[] extraParameters) {
		return String.format(startPluginCommandFormat, launcher, pluginName);
	}
}
