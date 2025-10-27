package com.processos.controller;

import com.processos.model.Usuario;
import com.processos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session, Model model) {
        // Se já está logado, redirecionar para processos
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/processos";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.autenticar(email, senha);

        if (usuario != null) {
            // Armazenar usuário na sessão
            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("nomeUsuario", usuario.getNome());
            session.setAttribute("emailUsuario", usuario.getEmail());

            // Configurar tempo de sessão (30 minutos)
            session.setMaxInactiveInterval(30 * 60);

            redirectAttributes.addFlashAttribute("sucesso",
                    "Bem-vindo(a), " + usuario.getPrimeiroNome() + "!");
            return "redirect:/processos";
        } else {
            redirectAttributes.addFlashAttribute("erro",
                    "Email ou senha inválidos. Tente novamente.");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/cadastro")
    public String mostrarCadastro(HttpSession session, Model model) {
        // Se já está logado, redirecionar para processos
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/processos";
        }

        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute Usuario usuario,
                            BindingResult result,
                            @RequestParam String confirmarSenha,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // Validar erros de validação
        if (result.hasErrors()) {
            model.addAttribute("erro", "Preencha todos os campos corretamente.");
            return "cadastro";
        }

        // Validar confirmação de senha
        if (!usuario.getSenha().equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não coincidem.");
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }

        try {
            Usuario novoUsuario = usuarioService.cadastrar(usuario);

            // Fazer login automático após cadastro
            session.setAttribute("usuarioLogado", novoUsuario);
            session.setAttribute("nomeUsuario", novoUsuario.getNome());
            session.setAttribute("emailUsuario", novoUsuario.getEmail());
            session.setMaxInactiveInterval(30 * 60);

            redirectAttributes.addFlashAttribute("sucesso",
                    "Cadastro realizado com sucesso! Bem-vindo(a), " + novoUsuario.getPrimeiroNome() + "!");
            return "redirect:/processos";

        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("usuario", usuario);
            return "cadastro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        String nomeUsuario = (String) session.getAttribute("nomeUsuario");

        // Invalidar sessão
        session.invalidate();

        redirectAttributes.addFlashAttribute("sucesso",
                "Logout realizado com sucesso. Até logo!");
        return "redirect:/auth/login";
    }
}

