package me.sirikon.buletina.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import me.sirikon.buletina.configuration.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class Templates {

  private static final List<String> AVAILABLE_TEMPLATES = List.of(
      "index.html",
      "verification_email_sent.html",
      "subscription_confirmed.html",
      "email/confirmation.html",
      "email/confirmation.txt"
  );

  private final Configuration configuration;
  private final MustacheFactory mustacheFactory;

  private Map<String, Mustache> templates;

  @Inject
  public Templates(final Configuration configuration) {
    this.configuration = configuration;
    this.mustacheFactory = new DefaultMustacheFactory();
    initialize();
  }

  private void initialize() {
    if (configuration.getDebugTemplates()) { return; }
    templates = new HashMap<>();
    AVAILABLE_TEMPLATES.forEach((t) -> {
      templates.put(t, buildTemplate(t));
    });
  }

  public String render(final String templateName, final Object ctx) {
    final var writer = new StringWriter();
    getTemplate(templateName).execute(writer, ctx);
    return writer.toString();
  }

  private Mustache getTemplate(final String templateName) {
    return configuration.getDebugTemplates()
        ? buildTemplate(templateName)
        : templates.get(templateName);
  }

  private Mustache buildTemplate(final String templateName) {
    final InputStream tplStream;

    final var possibleFileInDisk = new File("templates/" + templateName);
    if (possibleFileInDisk.exists()) {
      try {
        tplStream = new FileInputStream(possibleFileInDisk);
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Template '" + templateName + "' could not be obtained from disk");
      }
    } else {
      tplStream = getClass().getClassLoader().getResourceAsStream("templates/" + templateName);
    }

    if (tplStream == null) {
      throw new RuntimeException("Template '" + templateName + "' not found");
    }

    return mustacheFactory.compile(new InputStreamReader(tplStream), templateName);
  }

}
