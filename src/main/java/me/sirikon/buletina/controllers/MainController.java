package me.sirikon.buletina.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.javalin.http.Context;
import me.sirikon.buletina.configuration.Configuration;
import me.sirikon.buletina.services.TemplateService;
import org.apache.commons.validator.routines.EmailValidator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Singleton
public class MainController {

  private static final String JWT_ISSUER = "buletina";
  private static final String JWT_EMAIL_KEY = "email";

  private final Configuration configuration;
  private final TemplateService templateService;

  @Inject
  public MainController(
      final Configuration configuration,
      final TemplateService templateService) {
    this.configuration = configuration;
    this.templateService = templateService;
  }

  public void home(final Context ctx) {
    renderHome(ctx, "", "");
  }

  public void subscribe(final Context ctx) {
    final var emailValidator = ctx.formParam("email", String.class)
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

    final var email = emailValidator.get();
    final var subscriptionToken = createSubscriptionToken(email);
    final var confirmationUrl = "http://localhost:7000/confirm_subscription/" + urlEncode(subscriptionToken);
    ctx.result(confirmationUrl);
  }

  public void confirmSubscription(final Context ctx) {
    final var token = urlDecode(ctx.pathParam("token"));
    final var email = verifySubscriptionToken(token);
    ctx.result(email);
  }

  private void renderHome(final Context ctx, final String email, final String emailError) {
    ctx.html(templateService.render("index.html", Map.of(
        "email", email,
        "email_error", emailError
    )));
  }

  private String createSubscriptionToken(final String email) {
    return JWT.create()
        .withIssuer(JWT_ISSUER)
        .withClaim(JWT_EMAIL_KEY, email)
        .sign(getJWTAlgorithm());
  }

  private String verifySubscriptionToken(final String token) {
    return JWT.require(getJWTAlgorithm())
        .withIssuer(JWT_ISSUER)
        .build().verify(token)
        .getClaim(JWT_EMAIL_KEY).asString();
  }

  private Algorithm getJWTAlgorithm() {
    return Algorithm.HMAC512(configuration.getJwtSecret());
  }

  private static boolean isValidEmail(final String email) {
    return EmailValidator.getInstance(false, true).isValid(email);
  }

  private static String urlEncode(final String data) {
    try {
      return URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  private static String urlDecode(final String data) {
    try {
      return URLDecoder.decode(data, StandardCharsets.UTF_8.toString());
    } catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

}