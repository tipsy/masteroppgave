package no.ntnu.assignmentsystem.editor.akka;

public class Bootstrap {
	public static void bootstrap() {
		akka.Main.main(new String[] { Master.class.getName() });
	}
}
