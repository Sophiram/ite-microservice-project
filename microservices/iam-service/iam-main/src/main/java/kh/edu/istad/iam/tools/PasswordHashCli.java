package kh.edu.istad.iam.tools;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordHashCli {
    private PasswordHashCli() { }

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        char[] password;
        if (console != null) {
            password = console.readPassword("Password to hash: ");
        } else {
            System.err.print("Password to hash: ");
            password = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
                    .readLine().toCharArray();
        }
        if (password.length == 0) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        try {
            System.out.println(new BCryptPasswordEncoder().encode(new String(password)));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
