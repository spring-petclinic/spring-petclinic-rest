package org.springframework.samples.petclinic.rest.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.security.RegisterService;
import org.springframework.samples.petclinic.security.UserDtoSecurity;
import org.springframework.samples.petclinic.security.jwt.JwtUtil;
import org.springframework.samples.petclinic.security.model.ErrorRes;
import org.springframework.samples.petclinic.security.model.LoginReq;
import org.springframework.samples.petclinic.security.model.LoginRes;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/auth")
@CrossOrigin(exposedHeaders = "errors, content-type")
public class AuthController {

    @Autowired
    RegisterService registerService;

    private final AuthenticationManager authenticationManager;


    private JwtUtil jwtUtil;
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginReq loginReq)  {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
            String email = authentication.getName();
            String password = "";

            String roles = 
            authentication.getAuthorities()
                                    .stream()
                                    .map(r -> r.getAuthority())
                                    .collect(Collectors.toList())
                                    .toString();
                                    
            String token = jwtUtil.createToken(email, roles);
            LoginRes loginRes = new LoginRes(email,token);

            return ResponseEntity.ok(loginRes);

        }catch (BadCredentialsException e){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST,"Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (Exception e){
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody UserDtoSecurity userDto) {
        registerService.registerNewUserAccount(userDto);
        return ResponseEntity.ok(userDto);
    }

}