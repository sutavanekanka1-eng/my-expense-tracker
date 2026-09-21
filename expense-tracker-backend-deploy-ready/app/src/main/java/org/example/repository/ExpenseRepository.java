package org.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.entities.Expense;
import org.example.entities.ExpenseCategory;
import org.example.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	// Scoped-to-user queries. Every read/write in the service layer must
	// go through one of these (or check ownership manually) so one user
	// can never see or modify another user's expenses.
	List<Expense> findByUserInfoOrderByDateDesc(UserInfo userInfo);

	List<Expense> findByUserInfoAndCategoryOrderByDateDesc(UserInfo userInfo, ExpenseCategory category);

	List<Expense> findByUserInfoAndDateBetweenOrderByDateDesc(UserInfo userInfo, LocalDate start, LocalDate end);

	Optional<Expense> findByIdAndUserInfo(Long id, UserInfo userInfo);

	void deleteByIdAndUserInfo(Long id, UserInfo userInfo);
}
