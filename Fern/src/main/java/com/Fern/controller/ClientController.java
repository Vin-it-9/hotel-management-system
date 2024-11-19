package com.Fern.controller;

import com.Fern.entity.Image;
import com.Fern.entity.User;
import com.Fern.repository.UserRepo;
import com.Fern.service.ImageService;
import com.Fern.service.ImageServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;

import java.util.Base64;


@Controller
public class ClientController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ImageServiceImpl imageServiceImpl;


    @GetMapping("/setImage")
    public String setImageInSession(Principal principal, HttpSession session) throws IOException, SQLException {
        if (principal != null) {

            String email = principal.getName();

            Image userImage = imageServiceImpl.findByUserEmail(email);
            if (userImage != null && userImage.getImage() != null) {
                byte[] imageBytes = userImage.getImage().getBytes(1, (int) userImage.getImage().length());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                session.setAttribute("userImage", base64Image);
            }
        }
        return "redirect:/";
    }


    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImageFromSession(HttpSession session) throws IOException, SQLException {

        String base64Image = (String) session.getAttribute("userImage");

        if (base64Image != null) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/add")
    public ModelAndView addImage(){
        return new ModelAndView("addimage");
    }

    @PostMapping("/add")
    public String addImagePost(@RequestParam("image") MultipartFile file, Principal principal, HttpSession session) throws IOException, SerialException, SQLException {
        String email = principal.getName();
        User user = userRepo.getUserByEmail(email);
        byte[] bytes = file.getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);

        Image existingImage = imageServiceImpl.findByUserEmail(email);

        if (existingImage != null) {
            existingImage.setImage(blob);
            imageServiceImpl.update(existingImage);
        } else {
            Image newImage = new Image();
            newImage.setImage(blob);
            newImage.setUser(user);
            imageService.create(newImage);
        }

        byte[] imageBytes = blob.getBytes(1, (int) blob.length());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        session.setAttribute("userImage", base64Image);

        return "redirect:/editProfile";
    }






}
