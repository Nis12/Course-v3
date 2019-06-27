package Services.Core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ExtractProperties {

    private Properties properties;
    
    public ExtractProperties() {
        try {
            File file = new File("src/main/resources/properties.properties");
            properties = new Properties();
            properties.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPortForTCP() {
        return Integer.parseInt(properties.getProperty("PortForTCP"));
    }

    public int getPortForUDP() {
        return Integer.parseInt(properties.getProperty("PortForUDP"));
    }

    public int getTimeoutConnectionMillis() {
        return Integer.parseInt(properties.getProperty("TimeoutConnectionMillis"));
    }
}
