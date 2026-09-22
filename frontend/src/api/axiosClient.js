import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:9898";

// Access token lives only in memory (a module-level variable), never in
// localStorage - if this tab reloads, it's gone and the app re-derives a
// fresh one from the refresh token cookie flow below. See setAccessToken().
let accessToken = null;

export function setAccessToken(token) {
	accessToken = token;
}

export function getAccessToken() {
	return accessToken;
}

// The refresh token itself is stored in localStorage for now, since the
// backend's /auth/v1/refreshToken endpoint expects it in the request body
// rather than reading it from an httpOnly cookie. Swapping this for a real
// httpOnly cookie later would mean changing the backend to set/read the
// cookie itself - worth mentioning as a "next step" if this comes up.
const REFRESH_TOKEN_KEY = "expense_tracker_refresh_token";

export function setRefreshToken(token) {
	localStorage.setItem(REFRESH_TOKEN_KEY, token);
}

export function getRefreshToken() {
	return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function clearTokens() {
	accessToken = null;
	localStorage.removeItem(REFRESH_TOKEN_KEY);
}

const api = axios.create({ baseURL: BASE_URL });

// Attach the current access token to every outgoing request.
api.interceptors.request.use((config) => {
	if (accessToken) {
		config.headers.Authorization = `Bearer ${accessToken}`;
	}
	return config;
});

// If a request comes back 401 (expired/invalid access token), try refreshing
// once and replay the original request. Only ever retried once per request
// (the _retry flag) so a genuinely bad refresh token can't loop forever.
let refreshPromise = null;

api.interceptors.response.use(
	(response) => response,
	async (error) => {
		const originalRequest = error.config;
		const status = error.response?.status;

		const isAuthEndpoint = originalRequest?.url?.includes("/auth/v1/");
		if ((status !== 401 && status !== 403)|| originalRequest._retry || isAuthEndpoint) {
			return Promise.reject(error);
		}
		originalRequest._retry = true;

		try {
			// Multiple simultaneous 401s should share one refresh call,
			// not fire a refresh request per failed request.
			if (!refreshPromise) {
				refreshPromise = axios
					.post(`${BASE_URL}/auth/v1/refreshToken`, { token: getRefreshToken() })
					.finally(() => {
						refreshPromise = null;
					});
			}
			const { data } = await refreshPromise;
			setAccessToken(data.accessToken);
			originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
			return api(originalRequest);
		} catch (refreshError) {
			clearTokens();
			window.location.href = "/login";
			return Promise.reject(refreshError);
		}
	}
);

export default api;
