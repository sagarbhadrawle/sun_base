package com.sunBase.CustomersManagement.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.sunBase.CustomersManagement.config.JwtToken;
import com.sunBase.CustomersManagement.config.SecurityDetails;
import com.sunBase.CustomersManagement.model.LoginRequest;
import com.sunBase.CustomersManagement.model.Users;
import com.sunBase.CustomersManagement.service.UserService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Users> getProfile() {
        String username = getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users userProfile = userService.findByEmail(username).get();
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUserHandler(@RequestBody Users user) throws Exception {
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }else {
        	Users saveduser= userService.saveUser(user);    	
        	return new ResponseEntity<>(saveduser, HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> logInUserHandler(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String jwt = JwtToken.generateToken(loginRequest.getUsername(), authentication.getAuthorities());

            response.setHeader(SecurityDetails.JWT_HEADER, "Bearer " + jwt);
            
            

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
    
    @PostMapping("/logout")
    public ResponseEntity<?> logOutUserHandler(HttpServletResponse response) {
        response.setHeader(SecurityDetails.JWT_HEADER, null);
        return ResponseEntity.ok("Logout successful");
    }

}

