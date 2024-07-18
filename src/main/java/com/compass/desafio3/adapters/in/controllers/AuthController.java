package com.compass.desafio3.adapters.in.controllers;

import com.compass.desafio3.adapters.out.persistence.UsuarioRepository;
import com.compass.desafio3.application.services.EmailService;
import com.compass.desafio3.config.security.TokenService;
import com.compass.desafio3.domain.models.*;
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

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

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

    @PostMapping("/solicitar-reset-senha")
    public ResponseEntity solicitarResetSenha(@RequestBody @Valid PasswordResetRequestDTO request) {
        Usuario usuario = userRepository.findByEmail(request.email());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        String token = UUID.randomUUID().toString();
        usuario.setTokenResetSenha(token);
        usuario.setTokenExpiraEm(LocalDateTime.now().plusMinutes(15)); // Expira em 15 minutos

        userRepository.save(usuario);

        String mensagem = "Olá,\n\n";
        mensagem += "Você solicitou o reset de senha. Use o seguinte token para continuar: " + token + "\n\n";
        mensagem += "Este token expira em 15 minutos.\n\n";
        mensagem += "Se você não solicitou esta alteração, por favor ignore este e-mail.\n\n";
        mensagem += "Atenciosamente,\n";
        mensagem += "Equipe de Suporte";

        emailService.sendEmail(usuario.getEmail(), "Reset de Senha", mensagem);

        return ResponseEntity.ok("Instruções para resetar a senha foram enviadas para seu e-mail.");
    }

    @PostMapping("/resetar-senha")
    public ResponseEntity resetarSenha(@RequestBody @Valid PasswordResetDTO dataReset) {
        Usuario usuario = userRepository.findByTokenResetSenha(dataReset.token());
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }

        if (usuario.getTokenExpiraEm().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expirado.");
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(dataReset.newPassword());
        usuario.setSenha(encodedPassword);
        usuario.setTokenResetSenha(null); // Limpa o token após o uso
        usuario.setTokenExpiraEm(null);
        userRepository.save(usuario);

        return ResponseEntity.ok("Senha resetada com sucesso.");
    }

}
