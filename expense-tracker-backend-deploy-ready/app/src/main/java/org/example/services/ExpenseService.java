package org.example.services;

import java.time.LocalDate;
import java.util.List;

import org.example.entities.Expense;
import org.example.entities.ExpenseCategory;
import org.example.entities.UserInfo;
import org.example.repository.ExpenseRepository;
import org.example.repository.UserRepository;
import org.example.request.ExpenseRequestDto;
import org.example.response.ExpenseResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private UserRepository userRepository;

	// The username comes from the validated JWT (via SecurityContextHolder),
	// never from anything the client sends in the request body - that's what
	// stops user A from creating/reading/editing user B's expenses.
	private UserInfo currentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserInfo user = userRepository.findByUsername(username);
		if (user == null) {
			throw new IllegalStateException("Authenticated user not found: " + username);
		}
		return user;
	}

	public ExpenseResponseDto createExpense(ExpenseRequestDto request) {
		Expense expense = Expense.builder()
				.title(request.getTitle())
				.amount(request.getAmount())
				.category(request.getCategory())
				.date(request.getDate())
				.description(request.getDescription())
				.userInfo(currentUser())
				.build();
		return ExpenseResponseDto.fromEntity(expenseRepository.save(expense));
	}

	public List<ExpenseResponseDto> getAllExpenses() {
		return expenseRepository.findByUserInfoOrderByDateDesc(currentUser())
				.stream()
				.map(ExpenseResponseDto::fromEntity)
				.toList();
	}

	public List<ExpenseResponseDto> getExpensesByCategory(ExpenseCategory category) {
		return expenseRepository.findByUserInfoAndCategoryOrderByDateDesc(currentUser(), category)
				.stream()
				.map(ExpenseResponseDto::fromEntity)
				.toList();
	}

	public List<ExpenseResponseDto> getExpensesByDateRange(LocalDate start, LocalDate end) {
		return expenseRepository.findByUserInfoAndDateBetweenOrderByDateDesc(currentUser(), start, end)
				.stream()
				.map(ExpenseResponseDto::fromEntity)
				.toList();
	}

	public ExpenseResponseDto getExpenseById(Long id) {
		Expense expense = expenseRepository.findByIdAndUserInfo(id, currentUser())
				.orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
		return ExpenseResponseDto.fromEntity(expense);
	}

	public ExpenseResponseDto updateExpense(Long id, ExpenseRequestDto request) {
		UserInfo user = currentUser();
		Expense expense = expenseRepository.findByIdAndUserInfo(id, user)
				.orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));

		expense.setTitle(request.getTitle());
		expense.setAmount(request.getAmount());
		expense.setCategory(request.getCategory());
		expense.setDate(request.getDate());
		expense.setDescription(request.getDescription());

		return ExpenseResponseDto.fromEntity(expenseRepository.save(expense));
	}

	public void deleteExpense(Long id) {
		UserInfo user = currentUser();
		// Confirm ownership first so a delete against someone else's id
		// fails with a 404-style error instead of silently doing nothing.
		expenseRepository.findByIdAndUserInfo(id, user)
				.orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
		expenseRepository.deleteByIdAndUserInfo(id, user);
	}
}
