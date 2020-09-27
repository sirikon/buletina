package me.sirikon.buletina.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

public class TemplateService {

  private final Mustache indexTemplate;

  @Inject
  public TemplateService() {
    final var indexStream = getClass().getClassLoader().getResourceAsStream("templates/index.html");
    if (indexStream == null) {
      throw new RuntimeException("Template not found");
    }

    final var mustacheFactory = new DefaultMustacheFactory();
    indexTemplate = mustacheFactory.compile(new InputStreamReader(indexStream), "index");
  }

  public String index(final String message) {
    final var writer = new StringWriter();
    indexTemplate.execute(writer, Map.of("message", message));
    return writer.toString();
  }

}
