package no.ntnu.assignmentsystem.editor.akka;

public class Bootstrap {
	public static void bootstrap() {
		System.out.println("Boostrapping...");
		akka.Main.main(new String[] { Master.class.getName() });
	}
}
