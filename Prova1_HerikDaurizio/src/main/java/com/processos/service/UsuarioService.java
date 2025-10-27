package com.processos.service;

import com.processos.model.Usuario;
import com.processos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElse(null);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }

        return null;
    }


    public Usuario cadastrar(Usuario usuario) throws Exception {
        // Validar se email já existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("Email já cadastrado no sistema");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new Exception("Nome é obrigatório");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new Exception("Email é obrigatório");
        }

        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new Exception("Senha deve ter no mínimo 6 caracteres");
        }

        // Salvar usuário
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) throws Exception {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }
}
