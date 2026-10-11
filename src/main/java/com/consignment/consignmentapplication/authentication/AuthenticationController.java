package com.consignment.consignmentapplication.authentication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository rolerepo;

    @Autowired
    PasswordEncoder pwdencoder;

    @Autowired
    AuthenticationManager userAuthenticationManager;

    @Autowired
    JwtUtils jwtutils;

    // REGISTER - POST http://localhost:8080/api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username already taken!");
        }

        Set<Role> roleSet = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (String role : user.getRoles()) {
                roleSet.add(findOrCreateRole("admin".equalsIgnoreCase(role) ? Roles.ROLE_ADMIN : Roles.ROLE_USER));
            }
        } else {
            // no role sent by the client -> USER by default
            roleSet.add(findOrCreateRole(Roles.ROLE_USER));
        }

        Users newUser = new Users(user.getUsername(), pwdencoder.encode(user.getPassword()));
        newUser.setRoles(roleSet);
        userRepo.save(newUser);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    // LOGIN - POST http://localhost:8080/api/auth/signin
    @PostMapping({ "/signin", "/login" })
    public ResponseEntity<?> signin(@RequestBody LoginDto loginUser) {
        Authentication authentication;
        try {
            authentication = userAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtutils.generateJwtToken(authentication);

        UserDeatailsImpl userdetailsimpl = (UserDeatailsImpl) authentication.getPrincipal();
        List<String> roles = userdetailsimpl.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new ResponseEntity<>(new JwtResponse(jwt, userdetailsimpl.getUsername(), roles), HttpStatus.OK);
    }

    private Role findOrCreateRole(Roles name) {
        return rolerepo.findByName(name).orElseGet(() -> rolerepo.save(new Role(name)));
    }
}
