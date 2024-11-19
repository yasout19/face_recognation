package com.SystemDeSURveillance.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.SystemDeSURveillance.service.ImageConsumer;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/images")
public class ImageController {
    private final ImageConsumer imageConsumer;

    @Autowired
    public ImageController(ImageConsumer imageConsumer) {
        this.imageConsumer = imageConsumer;
    }

    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<byte[]> displayImage() {
        byte[] imageBytes = imageConsumer.getImageBytes();
        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(imageBytes.length));

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}