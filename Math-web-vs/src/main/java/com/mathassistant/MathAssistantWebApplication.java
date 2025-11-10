package com.mathassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import java.net.ServerSocket;

@SpringBootApplication
@Controller
public class MathAssistantWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(MathAssistantWebApplication.class);

    
    @Value("${server.port}")
    private int serverPort;

    @PostConstruct
    public void onStart() {
        logger.info("MathAssistant starting — listening on http://localhost:" + serverPort + " (server.address: 0.0.0.0 if configured)");
    }

    public static void main(String[] args) {
       
        System.setProperty("java.awt.headless", "true");

       
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            System.setProperty("server.port", envPort);
            logger.info("Using PORT from environment: " + envPort);
        } else {
            try (ServerSocket ss = new ServerSocket(0)) {
                int freePort = ss.getLocalPort();
                System.setProperty("server.port", Integer.toString(freePort));
                logger.info("No PORT env set — selected free port " + freePort + " for server.port");
            } catch (IOException e) {
               
                System.setProperty("server.port", "8080");
                logger.warn("Failed to find a free port, falling back to 8080", e);
            }
        }

        SpringApplication.run(MathAssistantWebApplication.class, args);
    }

        @GetMapping("/")
        public String home() {
             
                return "index";
        }

        @GetMapping("/fibonacci-page")
        public String fibonacciPage() {
          
            return "fibonacci";
        }

    @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Map<String, Object> calc(@RequestParam double a,
                                    @RequestParam double b,
                                    @RequestParam String op) {
        double res;
        switch (op) {
            case "add":
                res = a + b;
                break;
            case "sub":
                res = a - b;
                break;
            case "mul":
                res = a * b;
                break;
            case "div":
                res = (b != 0) ? a / b : Double.NaN;
                break;
            default:
                res = 0.0;
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("result", res);
        return map;
    }

    @GetMapping(value = "/fibonacci", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] fib(@RequestParam(required = false, defaultValue = "8") int n,
                                    @RequestParam(required = false, defaultValue = "600") int size) throws IOException {

    n = Math.max(1, Math.min(n, 1000));
        int sz = Math.max(100, Math.min(size, 2000));
        FibonacciCurve gen = new FibonacciCurve();
        return gen.generateCurve(n, sz);
    }
}

