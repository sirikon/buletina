package me.sirikon.buletina.controllers;

import io.javalin.http.Context;
import me.sirikon.buletina.services.Barker;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController {

  private final Barker barker;

  @Inject
  public HomeController(final Barker barker) {
    this.barker = barker;
  }

  public void index(final Context ctx) {
    ctx.result(barker.bark());
  }

}
