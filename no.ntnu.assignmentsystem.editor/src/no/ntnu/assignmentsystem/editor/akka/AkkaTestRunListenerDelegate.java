package no.ntnu.assignmentsystem.editor.akka;

import java.util.List;

import no.ntnu.assignmentsystem.editor.akka.messages.PluginTestResult;

public interface AkkaTestRunListenerDelegate {
	void testRunCompleted(AkkaTestRunListener listener, List<PluginTestResult> testResults);
}
