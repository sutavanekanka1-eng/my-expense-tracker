import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Port 3000 matches the CORS allowlist in SecurityConfig.java
// (config.setAllowedOrigins(List.of("http://localhost:3000"))).
// If you change this port, update that too.
export default defineConfig({
	plugins: [react()],
	server: {
		port: 3000,
	},
});
