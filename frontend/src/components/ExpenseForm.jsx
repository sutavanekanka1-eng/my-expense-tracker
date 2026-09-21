import { useState, useEffect } from "react";

const CATEGORIES = [
	"FOOD",
	"TRAVEL",
	"SHOPPING",
	"BILLS",
	"ENTERTAINMENT",
	"HEALTH",
	"RENT",
	"GROCERIES",
	"OTHER",
];

const EMPTY_FORM = {
	title: "",
	amount: "",
	category: "FOOD",
	date: new Date().toISOString().slice(0, 10),
	description: "",
};

// Same component handles both "create new" and "edit existing" - pass an
// `editingExpense` to prefill the form, or null for a blank create form.
export default function ExpenseForm({ editingExpense, onSubmit, onCancelEdit }) {
	const [form, setForm] = useState(EMPTY_FORM);

	useEffect(() => {
		if (editingExpense) {
			setForm({
				title: editingExpense.title,
				amount: editingExpense.amount,
				category: editingExpense.category,
				date: editingExpense.date,
				description: editingExpense.description || "",
			});
		} else {
			setForm(EMPTY_FORM);
		}
	}, [editingExpense]);

	function handleChange(e) {
		const { name, value } = e.target;
		setForm((prev) => ({ ...prev, [name]: value }));
	}

	function handleSubmit(e) {
		e.preventDefault();
		onSubmit({ ...form, amount: parseFloat(form.amount) });
		if (!editingExpense) setForm(EMPTY_FORM);
	}

	return (
		<form onSubmit={handleSubmit} className="expense-form">
			<h2>{editingExpense ? "Edit expense" : "Add expense"}</h2>
			<div className="form-row">
				<label>
					Title
					<input name="title" value={form.title} onChange={handleChange} required />
				</label>
				<label>
					Amount
					<input
						name="amount"
						type="number"
						step="0.01"
						min="0.01"
						value={form.amount}
						onChange={handleChange}
						required
					/>
				</label>
			</div>
			<div className="form-row">
				<label>
					Category
					<select name="category" value={form.category} onChange={handleChange}>
						{CATEGORIES.map((c) => (
							<option key={c} value={c}>
								{c}
							</option>
						))}
					</select>
				</label>
				<label>
					Date
					<input name="date" type="date" value={form.date} onChange={handleChange} required />
				</label>
			</div>
			<label>
				Description (optional)
				<input name="description" value={form.description} onChange={handleChange} />
			</label>
			<div className="form-actions">
				<button type="submit">{editingExpense ? "Save changes" : "Add expense"}</button>
				{editingExpense && (
					<button type="button" onClick={onCancelEdit}>
						Cancel
					</button>
				)}
			</div>
		</form>
	);
}

export { CATEGORIES };
