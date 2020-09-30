package me.sirikon.buletina.controllers;

import io.javalin.http.Context;
import me.sirikon.buletina.services.TemplateService;
import org.apache.commons.validator.routines.EmailValidator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Objects;

@Singleton
public class MainController {

  private final TemplateService templateService;

  @Inject
  public MainController(final TemplateService templateService) {
    this.templateService = templateService;
  }

  public void home(final Context ctx) {
    renderHome(ctx, "", "");
  }

  public void subscribe(final Context ctx) {
    final var emailValidator = ctx.formParam("email", String.class)
        .check(Objects::nonNull)
        .check(e -> !e.isEmpty(), "Email can't be empty")
        .check(MainController::isValidEmail, "Email is invalid");

    if (emailValidator.hasError()) {
      renderHome(ctx, emailValidator.getValue(), emailValidator.errors().get("email").get(0));
      return;
    }

    if (emailValidator.getOrNull() == null) {
      renderHome(ctx, "", "Email can't be empty");
      return;
    }

    renderHome(ctx, emailValidator.get(), "");
  }

  private void renderHome(final Context ctx, final String email, final String emailError) {
    ctx.html(templateService.render("index.html", Map.of(
        "email", email,
        "email_error", emailError
    )));
  }

  private static boolean isValidEmail(final String email) {
    return EmailValidator.getInstance(false, true).isValid(email);
  }

}
