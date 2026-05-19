package com.example.miniapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class ApiController {

    @Autowired
    private VisitRepository repo;

    @Autowired
    private StringRedisTemplate redis;

    @GetMapping("/")
    public Map<String, Object> root() {
        Long count = redis.opsForValue().increment("hits");
        Map<String, Object> out = new HashMap<>();
        out.put("message", "mini-app alive");
        out.put("redis_hits", count);
        out.put("postgres_visits", repo.count());
        return out;
    }

    @PostMapping("/visits")
    public Visit add(@RequestParam String note) {
        return repo.save(new Visit(note));
    }

    @GetMapping("/visits")
    public List<Visit> list() {
        return repo.findAll();
    }

    private static final List<String> FACTS = List.of(
        "Octopuses have three hearts, nine brains, and blue blood.",
        "Bananas are berries, but strawberries are not.",
        "Honey never spoils. Archaeologists found 3,000-year-old honey in Egyptian tombs that was still edible.",
        "A day on Venus is longer than a year on Venus. It rotates slower than it orbits the Sun.",
        "Sharks existed before trees. Sharks: ~450M years ago. Trees: ~350M years ago.",
        "Wombat poop is cube-shaped.",
        "Cleopatra lived closer in time to the Moon landing than to the building of the Great Pyramid.",
        "The Eiffel Tower can grow up to 15 cm taller in summer due to thermal expansion.",
        "Cows have best friends and get stressed when separated from them.",
        "There are more possible chess games than atoms in the observable universe.",
        "A group of flamingos is called a 'flamboyance'.",
        "Your stomach gets a new lining every 3-4 days, otherwise it would digest itself.",
        "The shortest war in history lasted 38 minutes — Anglo-Zanzibar War, 1896.",
        "Hot water freezes faster than cold water under certain conditions (Mpemba effect).",
        "Sea otters hold hands while sleeping so they don't drift apart."
    );

    @GetMapping("/random")
    public Map<String, String> random() {
        String fact = FACTS.get(ThreadLocalRandom.current().nextInt(FACTS.size()));
        return Map.of("fact", fact);
    }
}
