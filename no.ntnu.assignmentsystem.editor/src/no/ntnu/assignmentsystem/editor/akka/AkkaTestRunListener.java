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
		System.out.println(session);
	}
	
	public void sessionStarted(ITestRunSession session) {
		System.out.println(session);
	}
	
	public void sessionFinished(ITestRunSession session) {
		System.out.println(session);
	}
	
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		System.out.println(testCaseElement);
	}
	
	public void testCaseStarted(ITestCaseElement testCaseElement) {
		System.out.println(testCaseElement);
	}
}
