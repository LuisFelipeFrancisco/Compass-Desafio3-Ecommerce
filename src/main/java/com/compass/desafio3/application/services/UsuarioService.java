package com.compass.desafio3.application.services;

import com.compass.desafio3.application.exceptions.UsuarioNotFoundException;
import com.compass.desafio3.adapters.out.persistence.UsuarioRepository;
import com.compass.desafio3.domain.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario criarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obterUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com id: " + id));
    }

    public Usuario atualizarUsuario(Long id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNome(usuario.getNome());
                    u.setEmail(usuario.getEmail());
                    u.setFuncao(usuario.getFuncao());
                    //TODO Criptografar senha antes de salvar
                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com id: " + id));
    }

    public void excluirUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

}