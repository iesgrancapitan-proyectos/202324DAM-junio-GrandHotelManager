package es.iesgrancapitan.proyectohotelfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase que lee el archivo de configuración config.properties.
 */
public class Config {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getServerIp() {
        return properties.getProperty("server.ip");
    }
}