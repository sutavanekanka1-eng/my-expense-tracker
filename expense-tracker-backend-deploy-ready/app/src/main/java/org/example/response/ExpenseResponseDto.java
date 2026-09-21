package org.example.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.example.entities.Expense;
import org.example.entities.ExpenseCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponseDto {

	private Long id;
	private String title;
	private BigDecimal amount;
	private ExpenseCategory category;
	private LocalDate date;
	private String description;

	// Never return the Expense entity directly from a controller - it carries
	// the full UserInfo (including the hashed password) via the @ManyToOne
	// relation, and Jackson would happily serialize all of it.
	public static ExpenseResponseDto fromEntity(Expense expense) {
		return ExpenseResponseDto.builder()
				.id(expense.getId())
				.title(expense.getTitle())
				.amount(expense.getAmount())
				.category(expense.getCategory())
				.date(expense.getDate())
				.description(expense.getDescription())
				.build();
	}
}
