import no.ntnu.assignmentsystem.editor.akka.Bootstrap;
import akka.actor.UntypedActor;


public class Main {

	public static void main(String[] args) {
		System.out.println("Starting from Main...");
		Bootstrap.bootstrap();
	}
	
	static class Test extends UntypedActor {

		@Override
		public void onReceive(Object arg0) throws Exception {
			
		}
		
	}
}
