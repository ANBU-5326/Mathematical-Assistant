package com.mathassistant;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;

/*
  Replaced @RestController-only class with an MVC @Controller that:
  - serves the index view (GET /)
  - handles the form POST /fibonacci-curve and returns the index view with model.svg
  - preserves the REST endpoint /api/fibonacci that returns raw SVG (ResponseBody)
*/
@Controller
public class FibonacciController {

    private final FibonacciCurve generator = new FibonacciCurve();

    // Serve the index page (your form). Keeps previous behaviour: index expects model attributes.
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // provide defaults used by the view
        model.addAttribute("svg", "");
        model.addAttribute("svgData", "");
        model.addAttribute("n", "8");
        model.addAttribute("selectedOption", "fib");
        return "index";
    }

    // REST endpoint: return raw SVG (for direct fetch). Kept as /api/fibonacci for compatibility.
    @GetMapping(value = "/api/fibonacci", produces = "image/svg+xml")
    @ResponseBody
    public ResponseEntity<String> getFibonacci(
            @RequestParam(value = "n", defaultValue = "6") int n
    ) {
        try {
            String svg = generator.generateSvg(n);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("image/svg+xml"));
            headers.setCacheControl("no-cache, no-store, must-revalidate");

            return new ResponseEntity<>(svg, headers, HttpStatus.OK);

        } catch (Exception ex) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body("Error generating SVG: " + ex.getMessage());
        }
    }

    // --------- NEW: image endpoint used by fibonacci.html <img src="/fibonacci?..."> ----------
    // Serve generated curve bytes (SVG) so <img src="/fibonacci?n=6&size=800"> works unchanged.
    @GetMapping(value = "/fibonacci", produces = "image/svg+xml")
    @ResponseBody
    public ResponseEntity<byte[]> getFibonacciImage(
            @RequestParam(value = "n", required = false, defaultValue = "6") int n,
            @RequestParam(value = "size", required = false, defaultValue = "800") int size
    ) {
        try {
            // FibonacciCurve.generateCurve(n,size) returns UTF-8 SVG bytes (backwards-compatible)
            byte[] svgBytes = generator.generateCurve(n, size);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("image/svg+xml"));
            headers.setCacheControl("no-cache, no-store, must-revalidate");

            return new ResponseEntity<>(svgBytes, headers, HttpStatus.OK);
        } catch (Exception ex) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            String msg = "Error generating curve: " + ex.getMessage();
            return new ResponseEntity<>(msg.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Web form POST handler: exactly the behaviour your page expects — returns "index" view
    // with model attribute "svg" containing the generated SVG string.
    @PostMapping("/fibonacci-curve")
    public String drawFibCurve(@RequestParam(value = "n", required = false) String nStr,
                               @RequestParam(value = "option", required = false, defaultValue = "fib") String option,
                               Model model) {
        String svg = "";
        String svgData = "";
        String error = "";
        int n = 0;

        try {
            if (nStr == null || nStr.trim().isEmpty()) {
                error = "Please enter n.";
            } else {
                try {
                    n = Integer.parseInt(nStr.trim());
                } catch (NumberFormatException ex) {
                    throw new NumberFormatException("Invalid n format (use numbers only).");
                }

                if (n < 1) {
                    error = "n must be at least 1.";
                } else if (n > 50) {
                    error = "n too large (max 50).";
                } else {
                    // generate raw svg
                    svg = generator.generateSvg(n);

                    // SANITIZE for inline embedding: remove XML declaration and DOCTYPE if present
                    if (svg != null && !svg.isEmpty()) {
                        try {
                            svg = svg.replaceFirst("^\\s*<\\?xml[^>]*\\?>\\s*", "");
                            svg = svg.replaceFirst("^\\s*<!DOCTYPE[^>]*>\\s*", "");
                            svg = svg.trim();

                            int start = svg.indexOf("<svg");
                            int end = svg.lastIndexOf("</svg>");
                            if (start >= 0 && end >= 0 && end > start) {
                                svg = svg.substring(start, end + "</svg>".length()).trim();
                            } else if (start >= 0) {
                                svg = svg.substring(start).trim();
                            }

                        } catch (Exception ex) {
                            System.err.println("[FibonacciController] SVG sanitization failed: " + ex.getMessage());
                            ex.printStackTrace();
                        }

                        // create base64 data-URI fallback for <img>
                        try {
                            byte[] bytes = svg.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                            String b64 = Base64.getEncoder().encodeToString(bytes);
                            svgData = "data:image/svg+xml;base64," + b64;
                        } catch (Exception ex) {
                            System.err.println("[FibonacciController] SVG data URI creation failed: " + ex.getMessage());
                        }

                        // NEW: build a URL-encoded data: URI and wrap with an <object> for reliable embedding
                        try {
                            // Use UTF-8 percent-encoding for the SVG payload to avoid base64 size & encoding issues
                            String urlEncoded = java.net.URLEncoder.encode(svg, "UTF-8")
                                                           .replaceAll("\\+", "%20"); // keep spaces as %20
                            String dataUri = "data:image/svg+xml;utf8," + urlEncoded;
                            // object will render the svg document reliably inside the page
                            String objectEmbed = "<object type='image/svg+xml' data='" + dataUri +
                                                 "' style='width:100%;height:100%;display:block;border:0'></object>";
                            svg = objectEmbed;
                            System.out.println("[FibonacciController] Embedded SVG via <object> dataURI created. length=" + dataUri.length());
                        } catch (Exception ex) {
                            System.err.println("[FibonacciController] SVG object-embed creation failed: " + ex.getMessage());
                            // fallback: keep raw svg in model.svg (we already have svg content)
                        }
                    } else {
                        System.out.println("[FibonacciController] generator returned empty SVG for n=" + n);
                    }
                }
            }
        } catch (NumberFormatException e) {
            error = "Invalid n format (use numbers only).";
        } catch (Exception e) {
            error = "Unexpected error: " + e.getMessage();
            System.err.println("Fibonacci error: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("error", error);
        model.addAttribute("svg", svg);       // svg now contains <object ...>dataURI</object> (or raw svg if embed failed)
        model.addAttribute("svgData", svgData); // base64 fallback for <img> if needed
        model.addAttribute("n", nStr != null ? nStr.trim() : "");
        model.addAttribute("selectedOption", option);
        return "index";
    }

    // Add explicit /error mapping to show the index page instead of Whitelabel error
    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        model.addAttribute("svg", "");
        model.addAttribute("svgData", "");
        model.addAttribute("n", "8");
        model.addAttribute("selectedOption", "fib");
        return "index";
    }
}