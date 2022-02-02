/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package au.com.patientzero.cheeseria;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Properties;
import java.util.function.BiFunction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.patientzero.cheeseria.data.CheesesRepository;
import au.com.patientzero.cheeseria.data.JsonFileCheesesRepository;
import io.javalin.Javalin;

public class App {
  private final Config config;
  private static Logger logger = LoggerFactory.getLogger(App.class);


  public App(Config config) {
    this.config = config;
  }

  public void start () {
    Javalin app = Javalin.create().start(config.server().port());
    app.get("/", ctx -> ctx.result("Hi World"));
    app.get("/shutdown", ctx -> app.close() );
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> app.stop()));

    app.events(event -> {
      event.serverStopping(() -> { logger.info("Server Stopping"); });
      event.serverStopped(() -> { logger.info("Server Stopped");  });
    }
);
  }

  private static Config loadConfig() throws IOException {
    File configFile = WorkingDir.getOrCreateFile("cheeseria.yml", "default_config.yml");
    return new ObjectMapper(new YAMLFactory()).readValue(configFile, Config.class);
  }


  public static void fatal(String msg, Exception e) {
    if (msg != null) System.err.println(msg);
    if (e != null) System.err.print(e);
    System.exit(1);
  }


  public static void main(String[] args) {
    Config config = null;
    try {
      config = loadConfig();
    } catch (IOException ioe) {
      fatal("Cannot load config", ioe);
    }

    assert config != null;

    logger.debug("Port = " + config.server().port());
//loadConfig()
    // try {
    //   CheesesRepository repo = JsonFileCheesesRepository.loadRepository(file);
    // } catch (Exception e) {
    //   fatal("Failed to load cheeses", e);
    // }
    // new App(loadConfig("cheeseria.yml")).start();
  }  
}

record Config( ServerConfig server ) {}
record DataConfig(String path) {}
record ServerConfig (int port) {}
