package controllers;

import global.Global;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import static play.data.Form.*;

public class LoginController extends Controller {

    public static Result login() {
        return ok(
                views.html.sessionManagement.login.render(form(Login.class))
        );
    }

    public static Result logout() {
        session().clear();
        return ok(
                views.html.sessionManagement.loggedOut.render()
        );
    }

    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(views.html.sessionManagement.login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
//            session("userid", Global.services.authenticate(loginForm.get().username, loginForm.get().password));

            return redirect(
                    routes.MyProgressController.serveStudentProgress()
            );
        }
    }

    public static class Login {
        public String username;
        public String password;

        //joda, denne blir kallet... det er bare play-magi.
        public String validate() {
//            if (Global.services.authenticate(username, password) == null) {
//                return "Wrong username or password";
//            }
            return null;
        }
    }
}

