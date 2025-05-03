package com.suraj.springsecuritydemo.Controller;

import com.suraj.springsecuritydemo.Dto.SignInDto;
import com.suraj.springsecuritydemo.Dto.SignupDTO;
import com.suraj.springsecuritydemo.Entity.UserEntity;

import com.suraj.springsecuritydemo.Services.CustomUserDetailService;
import com.suraj.springsecuritydemo.Services.UserService;
import com.suraj.springsecuritydemo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private  UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailService userDetailService;




    @PostMapping("/signup")
    public ResponseEntity<?>  signup(@RequestBody SignupDTO signupDTO) {
        try {
            UserEntity user = userService.addUser(signupDTO);
            user.setPassword(null);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("user", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to register user: " + e.getMessage(), e);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInDto signInDto){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity user = (UserEntity) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", jwt);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", e);
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Login failed: " + e.getMessage(), e);
        }
    }


}
