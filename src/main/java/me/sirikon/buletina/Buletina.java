package me.sirikon.buletina;

import com.google.inject.Guice;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import me.sirikon.buletina.controllers.HomeController;
import me.sirikon.buletina.di.MainModule;

import java.io.File;

import static io.javalin.apibuilder.ApiBuilder.get;

public class Buletina {
    public static void main(final String[] args) {
        final var injector = Guice.createInjector(new MainModule());

        final var app = Javalin.create((c) -> {
            if (new File("./static").isDirectory()) {
                c.addStaticFiles("/", "./static", Location.EXTERNAL);
            }
            c.addStaticFiles("/", "static", Location.CLASSPATH);
        });

        app.routes(() -> {
            get("/", ctx -> injector.getInstance(HomeController.class).index(ctx));
        });

        app.start(7000);
    }
}
