package kh.edu.istad.platform.authserver.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Spring Web MVC: a public landing endpoint that lists the useful URLs. */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "Spring Authorization Server");
        body.put("discovery", "/.well-known/openid-configuration");
        body.put("authorize", "/oauth2/authorize");
        body.put("token", "/oauth2/token");
        body.put("jwks", "/oauth2/jwks");
        body.put("userinfo", "/userinfo");
        return body;
    }
}
