package no.ntnu.assignmentsystem.editor.akka;

import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;

public class AkkaTestRunListener extends TestRunListener {
//	private final ActorRef editorActor;
	
	public AkkaTestRunListener() {
//		this.editorActor = editorActor;
	}
	
	public void sessionLaunched(ITestRunSession session) {
		System.out.println("TestRunListener[1]: " + session);
	}
	
	public void sessionStarted(ITestRunSession session) {
		System.out.println("TestRunListener[2]: " + session);
	}
	
	public void sessionFinished(ITestRunSession session) {
		System.out.println("TestRunListener[3]: " + session);
	}
	
	public void testCaseStarted(ITestCaseElement testCaseElement) {
		System.out.println("TestRunListener[4]: " + testCaseElement);
	}
	
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		System.out.println("TestRunListener[5]: " + testCaseElement);
	}
}
