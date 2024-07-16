package com.miniato.security.controller;

import com.miniato.security.dto.JoinDTO;
import com.miniato.security.service.JoinService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService){
        this.joinService = joinService;
    }

    @PostMapping("/join")
    public String joinprocess(JoinDTO joinDTO){
        joinService.joinprocess(joinDTO);
        return "ok";
    }
}
