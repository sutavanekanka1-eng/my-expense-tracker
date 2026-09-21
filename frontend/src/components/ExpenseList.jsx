export default function ExpenseList({ expenses, onEdit, onDelete }) {
	const total = expenses.reduce((sum, e) => sum + Number(e.amount), 0);

	if (expenses.length === 0) {
		return <p className="empty-state">No expenses yet. Add your first one above.</p>;
	}

	return (
		<div>
			<table className="expense-table">
				<thead>
					<tr>
						<th>Date</th>
						<th>Title</th>
						<th>Category</th>
						<th>Amount</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					{expenses.map((expense) => (
						<tr key={expense.id}>
							<td>{expense.date}</td>
							<td>
								{expense.title}
								{expense.description && <div className="description">{expense.description}</div>}
							</td>
							<td>
								<span className="category-badge">{expense.category}</span>
							</td>
							<td>₹{Number(expense.amount).toFixed(2)}</td>
							<td className="row-actions">
								<button onClick={() => onEdit(expense)}>Edit</button>
								<button onClick={() => onDelete(expense.id)} className="danger">
									Delete
								</button>
							</td>
						</tr>
					))}
				</tbody>
			</table>
			<p className="total">Total: ₹{total.toFixed(2)}</p>
		</div>
	);
}
