package me.sirikon.buletina.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

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
public class TemplateService {

  private static final List<String> AVAILABLE_TEMPLATES = List.of(
      "index.html"
  );

  private final Map<String, Mustache> templates;

  @Inject
  public TemplateService() {
    templates = new HashMap<>();
    final var mustacheFactory = new DefaultMustacheFactory();
    AVAILABLE_TEMPLATES.forEach((t) -> {
      final InputStream tplStream;

      final var possibleFileInDisk = new File("templates/" + t);
      if (possibleFileInDisk.exists()) {
        try {
          tplStream = new FileInputStream(possibleFileInDisk);
        } catch (FileNotFoundException e) {
          throw new RuntimeException(t);
        }
      } else {
        tplStream = getClass().getClassLoader().getResourceAsStream("templates/" + t);
      }

      if (tplStream == null) {
        throw new RuntimeException("Template '" + t + "' not found");
      }

      templates.put(t, mustacheFactory.compile(new InputStreamReader(tplStream), t));
    });
  }

  public String render(final String templateName, final Object ctx) {
    final var writer = new StringWriter();
    templates.get(templateName).execute(writer, ctx);
    return writer.toString();
  }

}
