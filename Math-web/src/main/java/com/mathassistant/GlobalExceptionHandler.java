package com.mathassistant;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Catches uncaught exceptions and returns the index view with an error message
 * instead of Spring Boot's Whitelabel error page.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public String handleAllExceptions(Exception ex, Model model) {
		// minimal info for the user; full stack trace is logged to console
		System.err.println("Unhandled exception: " + ex.getMessage());
		ex.printStackTrace();

		model.addAttribute("error", "Unexpected error: " + (ex.getMessage() == null ? "see server log" : ex.getMessage()));
		model.addAttribute("svg", "");
		model.addAttribute("n", "8");
		model.addAttribute("selectedOption", "fib");
		return "index"; // render the index template so user can retry
	}
}
