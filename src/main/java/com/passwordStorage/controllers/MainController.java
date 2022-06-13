package com.passwordStorage.controllers;

import com.passwordStorage.models.Storage;
import com.passwordStorage.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import services.AEAD;

import java.util.ArrayList;

@Controller
//@RequestMapping("/home")
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title","Home page");
        return "home";
    }
    @Autowired
    private PostRepository postRepository;
    public void sendAllToHome(){
        AEAD aead = new AEAD();
        ArrayList<Storage> findAll = (ArrayList<Storage>) postRepository.findAll();
    }
}
