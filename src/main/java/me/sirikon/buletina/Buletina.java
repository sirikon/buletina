package me.sirikon.buletina;

import com.google.inject.Guice;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import me.sirikon.buletina.controllers.MainController;
import me.sirikon.buletina.di.MainModule;

import java.io.File;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Buletina {

    public static void main(final String[] args) {

        final var app = Javalin.create((c) -> {
            if (new File("./static").isDirectory()) {
                c.addStaticFiles("/", "./static", Location.EXTERNAL);
            }
            c.addStaticFiles("/", "static", Location.CLASSPATH);
        });

        final var injector = Guice.createInjector(new MainModule());
        final var mainController = injector.getInstance(MainController.class);
        app.routes(() -> {
            get("/", mainController::home);
            post("/subscribe", mainController::subscribe);
            get("/confirm_subscription/:token", mainController::confirmSubscription);
        });

        app.start(7000);
    }

}
