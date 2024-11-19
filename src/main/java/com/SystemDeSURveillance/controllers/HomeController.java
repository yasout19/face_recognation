package com.SystemDeSURveillance.controllers;

import org.springframework.stereotype.Controller;
import com.SystemDeSURveillance.service.FeatureConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@Controller
public class HomeController {
    @Autowired
    private FeatureConsumer featureConsumer;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "index";
    }
    @GetMapping("/display")
    public String display() {
        return "image"; // Renvoie vers la page d'affichage
    }
    @GetMapping("/result")
    public String getResult(Model model) {
        String lastMessage = featureConsumer.getLastConsumedMessage();
        model.addAttribute("message", lastMessage);
        return "result";
    }
}

