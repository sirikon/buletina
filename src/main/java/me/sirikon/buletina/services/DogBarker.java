package me.sirikon.buletina.services;

import javax.inject.Inject;

public class DogBarker implements Barker {

    @Inject
    DogBarker() {}

    @Override
    public String bark() {
        return "WOF WOF";
    }
}
