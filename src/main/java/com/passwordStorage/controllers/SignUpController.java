package com.passwordStorage.controllers;

import com.passwordStorage.models.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.passwordStorage.repo.PostRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import services.AEAD;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Controller
public class SignUpController {
    public PostRepository getPostRepository() {
        return postRepository;
    }

    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/signup")
    public String getMapping(Model model) {
        return "signup";
    }

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/signup")
    public String signUpMain(ModelMap model, @RequestParam("signLogin") String login, @RequestParam("signPassword") String password, @RequestParam("signName") String name, @RequestParam("signSecondName") String secondName, @RequestParam("signMail") String mail) throws Exception {
        if (!passChecker(password).equals("")) {
            model.addAttribute("error", passChecker(password));
            return "signup";
        }
        byte[] IV = IVMaker();
        byte[] salt = saltMaker();
        String hashedPassword = hashPass(password, salt);
        String IVlty = Base64.getEncoder().encodeToString(IV);
        String salty = Base64.getEncoder().encodeToString(salt);
        Storage storage = new Storage(login, hashedPassword, serviceManager(name,IV), serviceManager(secondName,IV), serviceManager(mail,IV), salty, IVlty);

        postRepository.save(storage);
        return "login";

    }
    public byte[] IVMaker()
    {
        byte[] IV = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        return IV;
    }
    public String serviceManager(String string, byte[] IV) throws Exception {
        AEAD service = new AEAD();
        String keySpec = Files.readString(Path.of("src/main/resources/IVector"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(keySpec),"AES");
        SecretKey key = secretKeySpec;



        byte[] cipherText = AEAD.encrypt(string.getBytes(), key, IV);

        return Base64.getEncoder().encodeToString(cipherText);
    }



    public String passChecker(String password) {
        String nums = "0123456789";
        String symbols = " !\"#$%&'()*+,-./:;<=>?@[]^_`{|}~";
        StringBuilder checkResult = new StringBuilder("");
        boolean lowCase = false;
        boolean upCase = false;
        boolean withoutNum = false;
        boolean withoutSymbols = false;
        if (password.length() <= 12) {
            checkResult.append("Please increase password length");
        }
        if (password.toLowerCase(Locale.ROOT).equals(password)) {
            lowCase = true;
            checkResult.append("Please add capital letters" + "\n");
        }
        for (int i = 0; i < nums.length(); i++) {
            if (password.contains(String.valueOf(nums.charAt(i)))) {
                withoutNum = true;
            }
        }
        if (!withoutNum) {
            checkResult.append("Please add numbers" + "\n");
        }
        if (password.toUpperCase(Locale.ROOT).equals(password)) {
            upCase = true;
            checkResult.append("Please add small letters" + "\n");
        }
        for (int i = 0; i < symbols.length(); i++) {
            if (password.contains(String.valueOf(symbols.charAt(i)))) {
                withoutSymbols = true;

            }
        }
        if (!withoutSymbols) {
            checkResult.append("Please add symbols" + "\n");
        }
        return checkResult.toString();
    }

    public String hashPass(String password, byte[] salty) throws NoSuchAlgorithmException {
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


    public byte[] saltMaker() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

}
