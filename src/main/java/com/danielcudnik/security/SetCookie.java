package com.danielcudnik.security;

import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.ui.Model;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Created by Bidzis on 1/18/2017.
 */
@CrossOrigin
@RestController
public class SetCookie extends SpringBootServletInitializer implements WebApplicationInitializer {

    @RequestMapping(value = "/pobierz",method = RequestMethod.GET)
    public String handleRequest (HttpServletResponse response, Model model) {

        Cookie newCookie = new Cookie("testCookie", "testCookieValue");
        newCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(newCookie);
        return "my-page";
    }

}
