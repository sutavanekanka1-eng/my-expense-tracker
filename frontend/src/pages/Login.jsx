import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Login() {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");
	const [loading, setLoading] = useState(false);
	const { login } = useAuth();
	const navigate = useNavigate();

	async function handleSubmit(e) {
		e.preventDefault();
		setError("");
		setLoading(true);
		try {
			await login(username, password);
			navigate("/");
		} catch {
			setError("Invalid username or password.");
		} finally {
			setLoading(false);
		}
	}

	return (
		<div className="auth-page">
			<form onSubmit={handleSubmit} className="auth-form">
				<h1>Log in</h1>
				{error && <p className="error">{error}</p>}
				<label>
					Username
					<input value={username} onChange={(e) => setUsername(e.target.value)} required />
				</label>
				<label>
					Password
					<input
						type="password"
						value={password}
						onChange={(e) => setPassword(e.target.value)}
						required
					/>
				</label>
				<button type="submit" disabled={loading}>
					{loading ? "Logging in..." : "Log in"}
				</button>
				<p>
					No account? <Link to="/signup">Sign up</Link>
				</p>
			</form>
		</div>
	);
}
