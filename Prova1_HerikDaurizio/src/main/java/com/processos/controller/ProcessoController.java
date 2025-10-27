package com.processos.controller;

import com.processos.model.Processo;
import com.processos.service.ProcessoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/processos")
public class ProcessoController {

    @Autowired
    private ProcessoService processoService;

    // LISTAR COM FILTRO DE SESSÃO
    @GetMapping("")
    public String listarProcessos(Model model,
                                  @RequestParam(value = "tema", required = false) String tema,
                                  @RequestParam(value = "busca", required = false) String busca,
                                  HttpSession session) {
        try {
            // Se veio busca do formulário, salvar na sessão
            if (busca != null && !busca.trim().isEmpty()) {
                session.setAttribute("filtroBusca", busca);
            }

            // Recuperar filtro da sessão se não veio busca nova
            String filtroAtivo = (String) session.getAttribute("filtroBusca");
            if (busca == null && filtroAtivo != null) {
                busca = filtroAtivo;
            }

            // Lista de processos "abertos" (não concluídos)
            List<Processo> processos = processoService.listarAbertos();

            // Aplicar filtro de busca se existir
            if (busca != null && !busca.trim().isEmpty()) {
                String buscaLower = busca.toLowerCase();
                processos = processos.stream()
                        .filter(p -> p.getTitulo().toLowerCase().contains(buscaLower) ||
                                (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(buscaLower)) ||
                                p.getFeitoPor().toLowerCase().contains(buscaLower) ||
                                p.getParaQuem().toLowerCase().contains(buscaLower))
                        .collect(Collectors.toList());
                model.addAttribute("busca", busca);
            }

            model.addAttribute("processos", processos);
            model.addAttribute("tema", tema);

            // Adicionar informações do usuário logado
            model.addAttribute("nomeUsuario", session.getAttribute("nomeUsuario"));

            return "processos/lista";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao listar processos: " + e.getMessage());
            return "processos/lista";
        }
    }

    // LIMPAR FILTRO DA SESSÃO
    @GetMapping("/limpar-filtro")
    public String limparFiltro(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("filtroBusca");
        redirectAttributes.addFlashAttribute("sucesso", "Filtro removido com sucesso!");
        return "redirect:/processos";
    }

    // NOVO FORMULÁRIO
    @GetMapping("/novo")
    public String novoProcesso(Model model,
                               @RequestParam(value = "tema", required = false) String tema) {
        model.addAttribute("processo", new Processo());
        model.addAttribute("tema", tema);
        return "processos/formulario";
    }

    // SALVAR/ATUALIZAR
    @PostMapping("/salvar")
    public String salvarProcesso(@Valid @ModelAttribute Processo processo,
                                 BindingResult result,
                                 @RequestParam(value = "tema", required = false) String tema,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", "Preencha todos os campos obrigatórios corretamente.");
            model.addAttribute("tema", tema);
            return "processos/formulario";
        }

        try {
            if (processo.getId() == null) {
                processoService.cadastrarProcesso(processo);
                redirectAttributes.addFlashAttribute("sucesso", "Processo criado com sucesso!");
            } else {
                processoService.atualizar(processo.getId(), processo); // CORRIGIDO
                redirectAttributes.addFlashAttribute("sucesso", "Processo atualizado com sucesso!");
            }
            return "redirect:/processos" + (tema != null ? "?tema=" + tema : "");
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("tema", tema);
            return "processos/formulario";
        }
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editarProcesso(@PathVariable Long id,
                                 @RequestParam(value = "tema", required = false) String tema,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Processo processo = processoService.buscarPorId(id);
            model.addAttribute("processo", processo);
            model.addAttribute("tema", tema);
            return "processos/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Processo não encontrado.");
            return "redirect:/processos";
        }
    }

    // DELETAR
    @GetMapping("/deletar/{id}")
    public String deletarProcesso(@PathVariable Long id,
                                  @RequestParam(value = "tema", required = false) String tema,
                                  RedirectAttributes redirectAttributes) {
        try {
            processoService.deletar(id); // CORRIGIDO
            redirectAttributes.addFlashAttribute("sucesso", "Processo deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao deletar: " + e.getMessage());
        }
        return "redirect:/processos" + (tema != null ? "?tema=" + tema : "");
    }

    // CONCLUIR/REABRIR
    @PostMapping("/concluir/{id}")
    public String concluirProcesso(@PathVariable Long id,
                                   @RequestParam(value = "tema", required = false) String tema,
                                   RedirectAttributes redirectAttributes) {
        try {
            Processo processo = processoService.buscarPorId(id);
            processo.setConcluido(!processo.isConcluido());
            processoService.atualizar(id, processo); // CORRIGIDO

            String mensagem = processo.isConcluido() ?
                    "Processo marcado como concluído!" : "Processo reaberto!";
            redirectAttributes.addFlashAttribute("sucesso", mensagem);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro: " + e.getMessage());
        }
        return "redirect:/processos" + (tema != null ? "?tema=" + tema : "");
    }
}
