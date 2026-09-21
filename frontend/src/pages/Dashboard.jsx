import { useState, useEffect, useCallback } from "react";
import { fetchExpenses, createExpense, updateExpense, deleteExpense } from "../api/expenseApi";
import { useAuth } from "../context/AuthContext";
import ExpenseForm, { CATEGORIES } from "../components/ExpenseForm";
import ExpenseList from "../components/ExpenseList";

export default function Dashboard() {
	const [expenses, setExpenses] = useState([]);
	const [editingExpense, setEditingExpense] = useState(null);
	const [categoryFilter, setCategoryFilter] = useState("");
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const { logout } = useAuth();

	const loadExpenses = useCallback(async () => {
		setLoading(true);
		setError("");
		try {
			const { data } = await fetchExpenses(categoryFilter ? { category: categoryFilter } : {});
			setExpenses(data);
		} catch {
			setError("Couldn't load expenses. Try refreshing.");
		} finally {
			setLoading(false);
		}
	}, [categoryFilter]);

	useEffect(() => {
		loadExpenses();
	}, [loadExpenses]);

	async function handleSubmit(expense) {
		try {
			if (editingExpense) {
				await updateExpense(editingExpense.id, expense);
				setEditingExpense(null);
			} else {
				await createExpense(expense);
			}
			loadExpenses();
		} catch {
			setError("Couldn't save that expense.");
		}
	}

	async function handleDelete(id) {
		if (!confirm("Delete this expense?")) return;
		try {
			await deleteExpense(id);
			loadExpenses();
		} catch {
			setError("Couldn't delete that expense.");
		}
	}

	return (
		<div className="dashboard">
			<header className="dashboard-header">
				<h1>Expense Tracker</h1>
				<button onClick={logout}>Log out</button>
			</header>

			{error && <p className="error">{error}</p>}

			<ExpenseForm
				editingExpense={editingExpense}
				onSubmit={handleSubmit}
				onCancelEdit={() => setEditingExpense(null)}
			/>

			<div className="filter-row">
				<label>
					Filter by category
					<select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
						<option value="">All</option>
						{CATEGORIES.map((c) => (
							<option key={c} value={c}>
								{c}
							</option>
						))}
					</select>
				</label>
			</div>

			{loading ? (
				<p>Loading...</p>
			) : (
				<ExpenseList expenses={expenses} onEdit={setEditingExpense} onDelete={handleDelete} />
			)}
		</div>
	);
}
