import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Signup() {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState("");
	const [loading, setLoading] = useState(false);
	const { signup } = useAuth();
	const navigate = useNavigate();

	async function handleSubmit(e) {
		e.preventDefault();
		setError("");
		setLoading(true);
		try {
			await signup(username, password);
			navigate("/");
		} catch (err) {
			if (err.response?.status === 400) {
				setError("That username is already taken.");
			} else {
				setError("Something went wrong. Try again.");
			}
		} finally {
			setLoading(false);
		}
	}

	return (
		<div className="auth-page">
			<form onSubmit={handleSubmit} className="auth-form">
				<h1>Sign up</h1>
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
						minLength={6}
					/>
				</label>
				<button type="submit" disabled={loading}>
					{loading ? "Creating account..." : "Sign up"}
				</button>
				<p>
					Already have an account? <Link to="/login">Log in</Link>
				</p>
			</form>
		</div>
	);
}
