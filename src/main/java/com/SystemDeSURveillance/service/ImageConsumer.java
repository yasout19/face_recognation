package com.SystemDeSURveillance.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.IOException;

@Service
public class ImageConsumer {

    private byte[] imageBytes;

    @KafkaListener(topics = "my-topic", groupId = "group_id")
    public void consume(String compressedBase64Image) {
        System.out.println("Received compressed image: " + compressedBase64Image);
        try {
            // Decode the Base64 string
            byte[] compressedBytes = Base64.getDecoder().decode(compressedBase64Image);

            // Decompress the bytes using GZIP decompression
            ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gis = new GZIPInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                bos.write(buffer, 0, len);
            }
            gis.close();
            bos.close();

            // Set the decompressed bytes as the image bytes
            imageBytes = bos.toByteArray();
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Failed to decompress Base64 image: " + e.getMessage());
        }
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
}

