import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";
import { login as loginApi, signup as signupApi } from "../api/expenseApi";
import { setAccessToken, setRefreshToken, clearTokens, getRefreshToken } from "../api/axiosClient";

const AuthContext = createContext(null);
const BASE_URL = "http://localhost:9898";

export function AuthProvider({ children }) {
	// isAuthenticated is derived from whether we have a refresh token stashed,
	// so a page reload doesn't immediately bounce the user to /login.
	const [isAuthenticated, setIsAuthenticated] = useState(!!getRefreshToken());
	// True while we're re-deriving an access token from the refresh token on
	// first mount. Protected pages should wait for this before firing their
	// own requests, or they'll go out with no Authorization header and fail.
	const [bootstrapping, setBootstrapping] = useState(!!getRefreshToken());

	useEffect(() => {
		const refreshToken = getRefreshToken();
		if (!refreshToken) {
			setBootstrapping(false);
			return;
		}
		axios
			.post(`${BASE_URL}/auth/v1/refreshToken`, { token: refreshToken })
			.then(({ data }) => {
				setAccessToken(data.accessToken);
			})
			.catch(() => {
				// Refresh token is invalid/expired - log the user out cleanly
				// rather than leaving them "authenticated" with no working token.
				clearTokens();
				setIsAuthenticated(false);
			})
			.finally(() => setBootstrapping(false));
	}, []);

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
		<AuthContext.Provider value={{ isAuthenticated, bootstrapping, login, signup, logout }}>
			{children}
		</AuthContext.Provider>
	);
}

export function useAuth() {
	const ctx = useContext(AuthContext);
	if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
	return ctx;
}
