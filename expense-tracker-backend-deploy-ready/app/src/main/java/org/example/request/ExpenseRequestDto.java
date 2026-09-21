package org.example.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.example.entities.ExpenseCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequestDto {

	@NotBlank(message = "Title is required")
	private String title;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private BigDecimal amount;

	@NotNull(message = "Category is required")
	private ExpenseCategory category;

	@NotNull(message = "Date is required")
	private LocalDate date;

	private String description;
}
