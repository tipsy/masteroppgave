import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
//        File modelData = new File("../no.ntnu.assignmentsystem.model/model/UoD.xmi");
//        ModelLoader modelLoader = new XmiModelLoader(modelData);
//        Utility.services = new MainServices(modelLoader);
    }

    @Override
    public void onStop(Application app) {
        System.out.println("Application shutdown...");
    }

}