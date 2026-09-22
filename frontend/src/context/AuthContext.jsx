import { createContext, useContext, useState } from "react";
import { login as loginApi, signup as signupApi } from "../api/expenseApi";
import { setAccessToken, setRefreshToken, clearTokens, getRefreshToken } from "../api/axiosClient";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
	// isAuthenticated is derived from whether we have a refresh token stashed,
	// so a page reload doesn't immediately bounce the user to /login - the
	// axios interceptor will silently mint a new access token on first 401.
	const [isAuthenticated, setIsAuthenticated] = useState(!!getRefreshToken());

	async function login(username, password) {
		const { data } = await loginApi(username, password);
		setAccessToken(data.accessToken);
		setRefreshToken(data.token);
		setIsAuthenticated(true);
	}

	async function signup(username, password) {
		const { data } = await signupApi(username, password);
		setAccessToken(data.accessToken);
		setRefreshToken(data.token);
		setIsAuthenticated(true);
	}

	function logout() {
		clearTokens();
		setIsAuthenticated(false);
	}

	return (
		<AuthContext.Provider value={{ isAuthenticated, login, signup, logout }}>
			{children}
		</AuthContext.Provider>
	);
}

export function useAuth() {
	const ctx = useContext(AuthContext);
	if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
	return ctx;
}
