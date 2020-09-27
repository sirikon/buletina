package me.sirikon.buletina;

import com.google.inject.Guice;
import me.sirikon.buletina.controllers.HomeController;
import me.sirikon.buletina.di.MainModule;
import io.javalin.Javalin;

public class Buletina {
    public static void main(final String[] args) {
        final var injector = Guice.createInjector(new MainModule());
        final var app = Javalin.create().start(7000);
        app.get("/", ctx -> injector.getInstance(HomeController.class).index(ctx));
    }
}
