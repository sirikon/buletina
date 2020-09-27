package me.sirikon.buletina.controllers;

import io.javalin.http.Context;
import me.sirikon.buletina.services.TemplateService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController {

  private final TemplateService templateService;

  @Inject
  public HomeController(final TemplateService templateService) {
    this.templateService = templateService;
  }

  public void index(final Context ctx) {
    ctx.html(templateService.index("YolO!"));
  }

}
