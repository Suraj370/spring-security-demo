# Spring Security with JWT

The provided code implements a Spring Security configuration using JWT (JSON Web Token) for stateless authentication. The /api/auth/signup and /api/auth/signin endpoints are designed for user registration and login, respectively. Here's how Spring Security, along with the custom JWT filter, handles these endpoints.

## Key Components
- SecurityConfig: Configures Spring Security, defines the security filter chain, and sets up the authentication manager. 

- JwtAuthFilter: A custom filter that intercepts requests to validate JWT tokens and authenticate users.

- CustomUserDetailService: Loads user details for authentication.
- JwtUtil: Utility class (not shown) for generating, validating, and extracting information from JWT tokens.
- Controllers: Handle /api/auth/signup and /api/auth/signin endpoints.
- User Entity: Contains username and password.

# Step-by-Step Internal Working of Spring Security with JWT

## 1. Security Configuration (SecurityConfig)

### Annotations
- `@Configuration` and `@EnableWebSecurity` enable Spring Security and allow customization.

### SecurityFilterChain
- Disables CSRF (`csrf.disable()`): Since this is a stateless API using JWT, CSRF protection is not needed.
- Sets `SessionCreationPolicy.STATELESS`: No HTTP sessions are used; authentication is handled via JWT.
- Permits `/api/auth/**` endpoints (`permitAll()`): Allows unauthenticated access to `/api/auth/signup` and `/api/auth/signin`.
- Requires authentication for all other requests (`anyRequest().authenticated()`).
- Adds `JwtAuthFilter` before `UsernamePasswordAuthenticationFilter` to process JWT tokens in incoming requests.

### AuthenticationManager
- Configures the `AuthenticationManager` to use `CustomUserDetailService` for user details and `BCryptPasswordEncoder` for password hashing.

### PasswordEncoder
- Uses `BCryptPasswordEncoder` to securely hash passwords.

## 2. JWT Authentication Filter (JwtAuthFilter)

- Extends `OncePerRequestFilter` to ensure the filter runs once per request.

### Logic
- Extracts the `Authorization` header from the request.
- If the header starts with `Bearer `, extracts the JWT token.
- Uses `JwtUtil` to extract the username from the token and validate it.
- If the token is valid, loads user details via `CustomUserDetailService`.
- Creates a `UsernamePasswordAuthenticationToken` and sets it in the `SecurityContextHolder` for authenticated access.
- Passes the request to the next filter in the chain.

## 3. Handling `/api/auth/signup`

### Request
- A POST request to `/api/auth/signup` with a JSON payload containing `username` and `password`.

### Flow
1. The request reaches the `SecurityFilterChain`.
2. Since `/api/auth/signup` matches `/api/auth/**`, Spring Security allows the request (unauthenticated access via `permitAll()`).
3. The `RegistrationController` (assumed) handles the request:
    - Validates the input.
    - Hashes the password using `BCryptPasswordEncoder`.
    - Saves the user to the database (via a repository).
    - (Optional) Generates a JWT token and returns it in the response.

## 4. Handling `/api/auth/signin`

### Request
- A POST request to `/api/auth/signin` with a JSON payload containing `username` and `password`.

### Flow
1. The request reaches the `SecurityFilterChain`.
2. Since `/api/auth/signin` matches `/api/auth/**`, Spring Security allows the request (unauthenticated access via `permitAll()`).
3. The `LoginController` (assumed) handles the request:
    - Uses the `AuthenticationManager` to authenticate the user by calling `authenticate(username, password)`.
    - The `AuthenticationManager` delegates to `CustomUserDetailService` to load the user by username.
    - Compares the provided password with the stored hashed password using `BCryptPasswordEncoder`.
    - If authentication succeeds, `JwtUtil` generates a JWT token.
    - Returns the token in the response.
4. The client stores the token (e.g., in local storage) for future requests.

## 5. Accessing Protected Endpoints

### Request
- A request to a protected endpoint (e.g., `/api/protected`) with the `Authorization` header: `Bearer <JWT_TOKEN>`.

### Flow
1. The request reaches the `SecurityFilterChain`.
2. Since the endpoint is not under `/api/auth/**`, Spring Security requires authentication (`anyRequest().authenticated()`).
3. The `JwtAuthFilter` intercepts the request:
    - Extracts the JWT token from the `Authorization` header.
    - Validates the token using `JwtUtil`.
    - Loads user details via `CustomUserDetailService`.
    - Sets the authentication in the `SecurityContextHolder`.
4. If the token is valid, the request proceeds to the controller.
5. If the token is invalid or missing, Spring Security returns a 401 Unauthorized response.


