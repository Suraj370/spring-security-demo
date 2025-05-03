package com.suraj.springsecuritydemo.Services;

import com.suraj.springsecuritydemo.Dto.SignupDTO;
import com.suraj.springsecuritydemo.Entity.UserEntity;
import com.suraj.springsecuritydemo.Exception.RegistrationFailedException;
import com.suraj.springsecuritydemo.Exception.UsernameAlreadyExistsException;
import com.suraj.springsecuritydemo.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Transactional
    public UserEntity addUser(SignupDTO signupDTO){
        if(userRepository.existsByUsername(signupDTO.getUsername())){
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        UserEntity user = new UserEntity();
        user.setUsername(signupDTO.getUsername());
        user.setPassword(encoder.encode(signupDTO.getPassword()));
        try{
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RegistrationFailedException("Failed to register user.", e);
        }
    }
}
