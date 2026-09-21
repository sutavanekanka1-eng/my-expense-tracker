package org.example.controller;

import java.time.LocalDate;
import java.util.List;

import org.example.entities.ExpenseCategory;
import org.example.request.ExpenseRequestDto;
import org.example.response.ExpenseResponseDto;
import org.example.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

// Every endpoint here requires auth by default (SecurityConfig only
// permitAll()s /auth/v1/**), so the JwtAuthFilter has already validated
// the token and populated SecurityContextHolder before we get here.
@RestController
@RequestMapping("/expense/v1")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@PostMapping
	public ResponseEntity<ExpenseResponseDto> createExpense(@Valid @RequestBody ExpenseRequestDto request) {
		return new ResponseEntity<>(expenseService.createExpense(request), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<ExpenseResponseDto>> getExpenses(
			// Bound as a raw String, not ExpenseCategory, so a blank/absent value
			// (e.g. an "All categories" filter option posting category=) doesn't
			// throw a type-conversion error - it's just treated as "no filter".
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

		if (category != null && !category.isBlank()) {
			return ResponseEntity.ok(expenseService.getExpensesByCategory(ExpenseCategory.valueOf(category.trim().toUpperCase())));
		}
		if (startDate != null && endDate != null) {
			return ResponseEntity.ok(expenseService.getExpensesByDateRange(startDate, endDate));
		}
		return ResponseEntity.ok(expenseService.getAllExpenses());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id) {
		return ResponseEntity.ok(expenseService.getExpenseById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ExpenseResponseDto> updateExpense(
			@PathVariable Long id, @Valid @RequestBody ExpenseRequestDto request) {
		return ResponseEntity.ok(expenseService.updateExpense(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
		expenseService.deleteExpense(id);
		return ResponseEntity.noContent().build();
	}
}
