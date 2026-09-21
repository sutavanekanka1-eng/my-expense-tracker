# Expense Tracker (React + Spring Boot + MySQL)

Full-stack expense tracker with JWT auth (access + refresh tokens) and
user-scoped expense CRUD.

```
expense-tracker-project/
├── backend/    Spring Boot 3.5, Java 17, Gradle, MySQL
└── frontend/   React 18 + Vite, react-router, axios
```

## 1. Prerequisites

- Java 17 (`java -version`)
- Node.js 18+ and npm (`node -v`)
- MySQL running locally (or reachable) with a database you can point the
  app at

## 2. Database setup

Create the database (table creation is automatic - see step 3):

```sql
CREATE DATABASE AuthService;
```

## 3. Backend setup

1. Open `backend/app/src/main/resources/application.properties` and set
   your MySQL credentials:

   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD_HERE
   ```

   By default it connects to `127.0.0.1:3306` and database `AuthService`.
   You can override any of these with env vars instead of editing the file:
   `MYSQL_HOST`, `MYSQL_PORT`, `MYSQL_DB`.

2. `spring.jpa.hibernate.ddl-auto=update` is already set, so Hibernate will
   create the `users`, `roles`, `user_roles`, `tokens`, and `expenses`
   tables automatically on first run. No manual migration needed.

3. From the `backend/` directory, run:

   ```bash
   # macOS/Linux
   ./gradlew bootRun

   # Windows
   gradlew.bat bootRun
   ```

   The API starts on **http://localhost:9898**.

4. Quick smoke test once it's up (Windows Command Prompt - escape the
   quotes like this; on macOS/Linux/PowerShell single quotes work fine):

   ```cmd
   curl -X POST http://localhost:9898/auth/v1/signup -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"password\":\"password123\"}"
   ```

   You should get back `{"accessToken":"...","token":"..."}`.

## 4. Frontend setup

From the `frontend/` directory:

```bash
npm install
npm run dev
```

This starts the app on **http://localhost:3000** (must stay on this port -
the backend's CORS config in `SecurityConfig.java` only allows
`http://localhost:3000`; change both together if you need a different port).

Open http://localhost:3000, sign up, and start adding expenses.

## 5. Running both together

Two terminals:

```bash
# terminal 1
cd backend && ./gradlew bootRun

# terminal 2
cd frontend && npm run dev
```

## Notes on the auth flow

- The **access token** lives only in memory on the frontend (a JS variable),
  not localStorage - so it disappears on a hard refresh. The **refresh
  token** is kept in localStorage. On every app mount, `AuthContext`
  proactively calls `/auth/v1/refreshToken` if a refresh token exists, so a
  page reload always ends up with a valid access token before any protected
  page fires its own requests (see `bootstrapping` state in
  `AuthContext.jsx` / `ProtectedRoute.jsx`).
- `SecurityConfig.java` explicitly returns **401** (not Spring Security's
  default 403) for unauthenticated requests, via a custom
  `authenticationEntryPoint`. This matters: the frontend's axios response
  interceptor (`axiosClient.js`) only attempts a silent token refresh when
  it sees a 401. Without this backend change, expired/missing tokens would
  return 403 and the frontend would never know to refresh - this was an
  actual bug caught while testing the app end-to-end, not a hypothetical.
- This in-memory-token approach is a reasonable middle ground for a learning
  project. The more secure version puts the refresh token in an httpOnly
  cookie instead of localStorage (so JS - including any injected via XSS -
  can never read it), but that requires the backend to set/read the cookie
  itself rather than taking the token in the request body as it does now.
  Worth mentioning as a known tradeoff if this comes up in your interview.
- Every expense endpoint resolves the current user from the JWT via
  `SecurityContextHolder` (see `ExpenseService.currentUser()`), never from
  anything the client sends - so there's no way to read or modify another
  user's expenses by guessing an id.
