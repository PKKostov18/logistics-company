package com.logistics.company.Controller;// package com.logistics.company.controller; // Уверете се, че пакетът е правилен

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // <-- Това е КЛЮЧОВОТО!
public class MainController {

    /**
     * Този метод казва на Spring:
     * Когато някой отвори http://localhost:8080/
     * върни му файла /src/main/resources/templates/index.html
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // <-- Трябва да съвпада с името на файла (index.html)
    }
}