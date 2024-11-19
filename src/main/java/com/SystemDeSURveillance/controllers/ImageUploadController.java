package com.SystemDeSURveillance.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.SystemDeSURveillance.service.FeatureConsumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPOutputStream;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private FeatureConsumer featureConsumer;
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("image") MultipartFile file) {
        try {
            // Convert the image to a byte array
            byte[] imageBytes = file.getBytes();

            // Compress the image using GZIP compression
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzipOS = new GZIPOutputStream(bos);
            gzipOS.write(imageBytes);
            gzipOS.close();
            byte[] compressedBytes = bos.toByteArray();

            // Convert the compressed image to a Base64 string
            String base64Image = Base64.getEncoder().encodeToString(compressedBytes);

            // Send the image to Kafka
            kafkaTemplate.send("my-topic", base64Image);
            CountDownLatch latch = new CountDownLatch(1);
            featureConsumer.setLatch(latch);

            // Wait for the message to be consumed
            try {
                // Wait for the message to be consumed
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Restore the interrupt status
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image processing was interrupted");
            }
            String message = featureConsumer.getLastConsumedMessage();
            return ResponseEntity.ok(message != null ? message : "No message consumed yet");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed");
        }
    }


}
