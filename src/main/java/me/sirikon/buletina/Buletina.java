package me.sirikon.buletina;

import com.google.inject.Guice;
import com.google.inject.ProvisionException;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import me.sirikon.buletina.configuration.Configuration;
import me.sirikon.buletina.controllers.MainController;
import me.sirikon.buletina.di.MainModule;
import me.sirikon.buletina.errors.InitializationError;

import java.io.File;
import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Buletina {

    public static void start() {
        final var injector = Guice.createInjector(new MainModule());
        final var configuration = injector.getInstance(Configuration.class);

        final var app = Javalin.create((c) -> {
            if (new File("./static").isDirectory()) {
                c.addStaticFiles("/", "./static", Location.EXTERNAL);
            }
            c.addStaticFiles("/", "static", Location.CLASSPATH);
        });


        final var mainController = injector.getInstance(MainController.class);
        app.routes(() -> {
            get("/", mainController::home);
            post("/subscribe", mainController::subscribe);
            get("/confirm_subscription/:token", mainController::confirmSubscription);
        });

        app.start(configuration.getPort());
    }

    public static void main(final String[] args) {
        try {
            try { start(); } catch (final ProvisionException err) { throw err.getCause(); }
        } catch (final InitializationError err) {
            System.err.println(err.getMessage());
            err.getCause().printStackTrace();
            System.exit(1);
        } catch (final Throwable t) {
            System.err.println(t.getMessage());
            System.err.println(Arrays.toString(t.getStackTrace()));
            System.exit(1);
        }
    }

}
