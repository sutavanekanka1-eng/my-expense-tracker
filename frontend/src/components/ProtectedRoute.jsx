import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children }) {
	const { isAuthenticated, bootstrapping } = useAuth();

	if (bootstrapping) {
		return <p style={{ textAlign: "center", marginTop: "3rem" }}>Loading...</p>;
	}
	if (!isAuthenticated) {
		return <Navigate to="/login" replace />;
	}
	return children;
}
