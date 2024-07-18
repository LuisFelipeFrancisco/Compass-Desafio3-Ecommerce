package com.compass.desafio3.adapters.in.controllers;

import com.compass.desafio3.adapters.out.persistence.UsuarioRepository;
import com.compass.desafio3.config.security.TokenService;
import com.compass.desafio3.domain.models.AuthDTO;
import com.compass.desafio3.domain.models.LoginDTO;
import com.compass.desafio3.domain.models.RegDTO;
import com.compass.desafio3.domain.models.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthDTO dataAuth) {
        var usuarioSenha = new UsernamePasswordAuthenticationToken(dataAuth.email(), dataAuth.senha());
        var auth = this.authManager.authenticate(usuarioSenha);
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginDTO(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody @Valid RegDTO dataReg) {
        if (this.userRepository.findByEmail(dataReg.email()) != null) return ResponseEntity.badRequest().build();

        String encodedPassword = new BCryptPasswordEncoder().encode(dataReg.senha());
        Usuario novoUsuario = new Usuario(dataReg.nome(), dataReg.email(), encodedPassword, dataReg.funcao());

        this.userRepository.save(novoUsuario);

        return ResponseEntity.ok().build();

    }

}
