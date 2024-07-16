package com.miniato.security.service;

import com.miniato.security.dto.JoinDTO;
import com.miniato.security.entity.User;
import com.miniato.security.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinprocess(JoinDTO joinDTO){
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean result = userRepository.existsByUsername(username);

        if(result){
            return;
        }
        User user = new User(null, username, password, "ROLE_ADMIN");
        userRepository.save(user);
    }
}
