package kh.edu.istad.iam;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IamServiceApplication {

    static {
        loadDotenv();
    }

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }

    private static void loadDotenv() {
        String[] potentialPaths = {
                ".env",
                "../.env",
                "microservices/iam-service/.env",
                "../../microservices/iam-service/.env",
                "D:/MicroserviceProject/istad-ite-project/microservices/iam-service/.env"
        };
        for (String path : potentialPaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    for (String line : lines) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eq = line.indexOf('=');
                        if (eq > 0) {
                            String key = line.substring(0, eq).trim();
                            String value = line.substring(eq + 1).trim();
                            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                                value = value.substring(1, value.length() - 1);
                            }
                            if (System.getProperty(key) == null && System.getenv(key) == null) {
                                System.setProperty(key, value);
                            }
                            // Map Spring Mail properties if specified in .env
                            if ("MAIL_HOST".equals(key)) System.setProperty("SPRING_MAIL_HOST", value);
                            if ("MAIL_PORT".equals(key)) System.setProperty("SPRING_MAIL_PORT", value);
                            if ("MAIL_USERNAME".equals(key)) System.setProperty("SPRING_MAIL_USERNAME", value);
                            if ("MAIL_PASSWORD".equals(key)) System.setProperty("SPRING_MAIL_PASSWORD", value);
                        }
                    }
                    System.out.println("[IamService] Loaded environment configuration from: " + file.getAbsolutePath());
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }
}
