package com.suraj.springsecuritydemo.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hello")
public class UserController {

    @GetMapping("/")
    public ResponseEntity<?> Hello(){
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello world");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
