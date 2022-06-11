package com.passwordStorage.controllers;

import com.passwordStorage.models.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.passwordStorage.repo.PostRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

@Controller
public class SignUpController {
    String salt;
    public PostRepository getPostRepository() {
        return postRepository;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    @GetMapping("/signup")
    public String getMapping(Model model)
    {
        return "signup";
    }
    @Autowired
    private PostRepository postRepository;
    @PostMapping("/signup")
    public String signUpMain(Model model,@RequestParam("signLogin") String login, @RequestParam("signPassword") String password, @RequestParam("signName") String name, @RequestParam("signSecondName") String secondName, @RequestParam("signMail") String mail) throws NoSuchAlgorithmException {

        byte[]salt = saltMaker();
        String hashedPassword = hashPass(password, salt);
        String salty = Base64.getEncoder().encodeToString(salt);
        Storage storage = new Storage(login,hashedPassword,name,secondName,mail,salty);

        postRepository.save(storage);
        return "login";

    }
    public String hashPass (String password, byte[] salty) throws NoSuchAlgorithmException {
        StringBuilder hashPass = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salty);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        hashedPassword = Base64.getEncoder().encode(hashedPassword);
        for (byte b : hashedPassword) {
            hashPass.append((char) b);
        }
        return hashPass.toString();
    }
    public byte[] saltMaker()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
