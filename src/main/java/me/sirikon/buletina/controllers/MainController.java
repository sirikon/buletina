package me.sirikon.buletina.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.javalin.http.Context;
import me.sirikon.buletina.configuration.Configuration;
import me.sirikon.buletina.services.Database;
import me.sirikon.buletina.services.Mailer;
import me.sirikon.buletina.services.Templates;
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

  private static final String ALPHANUMERIC_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final String JWT_ISSUER = "buletina";
  private static final String JWT_EMAIL_KEY = "email";

  private final Configuration configuration;
  private final Templates templates;
  private final Database database;
  private final Mailer mailer;

  @Inject
  public MainController(
      final Configuration configuration,
      final Templates templates,
      final Database database,
      final Mailer mailer) {
    this.configuration = configuration;
    this.templates = templates;
    this.database = database;
    this.mailer = mailer;
  }

  public void home(final Context ctx) { renderHome(ctx, "", ""); }

  public void subscribe(final Context ctx) {
    final var emailValidator = ctx.formParam("email", String.class)
        .check(e -> !e.isEmpty(), "Email can't be empty")
        .check(MainController::isValidEmail, "Email is invalid");

    if (emailValidator.hasError()) {
      renderHome(ctx, emailValidator.getValue(), emailValidator.errors().get(emailValidator.getKey()).get(0));
      return;
    }

    if (emailValidator.getOrNull() == null) {
      renderHome(ctx, "", "Email can't be empty");
      return;
    }

    final var email = emailValidator.get();
    final var subscriptionToken = createSubscriptionToken(email);
    final var confirmationUrl = buildConfirmationUrl(subscriptionToken);

    mailer.sendEmail(
        email,
        "Confirm Subscription",
        buildConfirmationEmailText(confirmationUrl),
        buildConfirmationEmailHTML(confirmationUrl));

    renderVerificationEmailSent(ctx);
  }

  public void confirmSubscription(final Context ctx) {
    final var token = urlDecode(ctx.pathParam("token"));
    final var email = verifySubscriptionToken(token);
    database.insertSubscription(email, generateCancellationToken());
    renderSubscriptionConfirmed(ctx);
  }

  private void renderHome(final Context ctx, final String email, final String emailError) {
    ctx.html(templates.render("index.html", Map.of(
        "email", email,
        "email_error", emailError
    )));
  }

  private void renderVerificationEmailSent(final Context ctx) {
    ctx.html(templates.render("verification_email_sent.html", null));
  }

  private void renderSubscriptionConfirmed(final Context ctx) {
    ctx.html(templates.render("subscription_confirmed.html", null));
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

  private Algorithm getJWTAlgorithm() { return Algorithm.HMAC512(configuration.getJwtSecret()); }

  private String buildConfirmationUrl(final String token) {
    return configuration.getBaseURL() + "/confirm_subscription/" + urlEncode(token);
  }

  private String buildConfirmationEmailHTML(final String confirmationUrl) {
    return templates.render("email/confirmation.html", Map.of("confirmation_url", confirmationUrl));
  }

  private String buildConfirmationEmailText(final String confirmationUrl) {
    return templates.render("email/confirmation.txt", Map.of("confirmation_url", confirmationUrl));
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

  private static String generateCancellationToken() {
    final var stringBuilder = new StringBuilder();
    var count = 64;
    while (count-- != 0) {
      final var charPos = (int)Math.floor(Math.random()*ALPHANUMERIC_CHARS.length());
      stringBuilder.append(ALPHANUMERIC_CHARS.charAt(charPos));
    }
    return stringBuilder.toString();
  }

}
