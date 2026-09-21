import api from "./axiosClient";

export function signup(username, password) {
	// Matches UserInfoDto on the backend.
	return api.post("/auth/v1/signup", { username, password });
}

export function login(username, password) {
	// Matches AuthRequestDto on the backend.
	return api.post("/auth/v1/login", { username, password });
}

export function fetchExpenses({ category, startDate, endDate } = {}) {
	return api.get("/expense/v1", { params: { category, startDate, endDate } });
}

export function createExpense(expense) {
	return api.post("/expense/v1", expense);
}

export function updateExpense(id, expense) {
	return api.put(`/expense/v1/${id}`, expense);
}

export function deleteExpense(id) {
	return api.delete(`/expense/v1/${id}`);
}
