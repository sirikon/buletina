package me.sirikon.buletina.di;

import com.google.inject.AbstractModule;
import me.sirikon.buletina.controllers.HomeController;
import me.sirikon.buletina.services.Barker;
import me.sirikon.buletina.services.DogBarker;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HomeController.class);
        bind(Barker.class).to(DogBarker.class);
    }
}
