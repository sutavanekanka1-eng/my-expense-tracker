package org.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Thrown by the service layer when an id doesn't exist for the current
	// user - either it was never theirs, or it doesn't exist at all. We
	// deliberately don't distinguish the two (404 either way) so we don't
	// leak whether another user's expense id exists.
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("error", ex.getMessage()));
	}

	// Triggered by @Valid failures on ExpenseRequestDto - returns
	// { "amount": "Amount must be greater than 0", ... } instead of a stack trace.
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	// Thrown when a query param can't be converted to its target type - most
	// commonly ?category= sent as an empty string (e.g. an "All categories"
	// filter option), which can't convert to the ExpenseCategory enum. Without
	// this handler that blew up as an unhandled 500/whitelabel error, which
	// from the frontend just looked like "the expense list never loads".
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
	}
}
