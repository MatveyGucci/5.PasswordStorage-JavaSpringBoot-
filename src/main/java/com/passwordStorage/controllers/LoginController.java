package com.passwordStorage.controllers;

import com.passwordStorage.models.Storage;
import com.passwordStorage.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import services.AEAD;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class LoginController {
    @Autowired
    private PostRepository postRepository;

    public PostRepository getPostRepository() {
        return postRepository;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    @GetMapping("/login")
    public String getMapping (Model model)
    {
        return "login";
    }

    @PostMapping("/login")
    public String home(Model model, @RequestParam("logLogin") String login, @RequestParam("logPassword") String password) throws Exception {
        ArrayList<Storage> findAll = (ArrayList<Storage>) postRepository.findAll();
        System.out.println(login);
        String salt ="";
        System.out.println(findAll);
        boolean flagSalty = false;
        for (Storage obj: findAll) {
            if ((obj.getLogin().equals(login))){
                salt = obj.getSalt();
                System.out.println("founded");
                break;
            }
            else{
                System.out.println("notFounded");
            }
        }
        byte[] salty = Base64.getDecoder().decode(salt);
        boolean flagPass = false;
        StringBuilder hashPass = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salty);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        hashedPassword = Base64.getEncoder().encode(hashedPassword);
        for (byte b : hashedPassword) {
            hashPass.append((char) b);
        }
        System.out.println(hashPass);
        Storage storage = null;
        for (Storage object: findAll) {
            if (Objects.equals(object.getLogin(), login)){
                if (Objects.equals(object.getPassword(), hashPass.toString())){
                    flagPass = true;
                    storage = object;
                }
            }
        }
        if (storage!=null) {
            StringBuilder stringBuilder = new StringBuilder();
            String keySpec = Files.readString(Path.of("src/main/resources/IVector"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(keySpec), "AES");
            SecretKey key = secretKeySpec;
            stringBuilder.append(AEAD.decrypt(Base64.getDecoder().decode(storage.getName()), key, Base64.getDecoder().decode(storage.getIV())));
            stringBuilder.append("       ");
            stringBuilder.append(AEAD.decrypt(Base64.getDecoder().decode(storage.getSecondName()), key, Base64.getDecoder().decode(storage.getIV())));
            stringBuilder.append("       ");
            stringBuilder.append(AEAD.decrypt(Base64.getDecoder().decode(storage.getEmail()), key, Base64.getDecoder().decode(storage.getIV())));


            if (flagPass) {
                model.addAttribute("storage", stringBuilder.toString());
                return "successfulLogin";
            } else {
                return "login";
            }
        }
        return "login";
    }
}
