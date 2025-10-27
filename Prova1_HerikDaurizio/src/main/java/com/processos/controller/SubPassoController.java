package com.processos.controller;

import com.processos.model.SubPasso;
import com.processos.model.Processo;
import com.processos.service.SubPassoService;
import com.processos.service.ProcessoService;
import jakarta.servlet.http.HttpSession; // ← ADICIONAR ESTE IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/subpassos")
public class SubPassoController {

    @Autowired
    private SubPassoService subPassoService;

    @Autowired
    private ProcessoService processoService;

    // Listar subpassos de um processo
    @GetMapping("/processo/{processoId}")
    public String listarPorProcesso(@PathVariable Long processoId,
                                    @RequestParam(required = false) String tema,
                                    HttpSession session, // ← ADICIONAR PARÂMETRO
                                    Model model) throws Exception {
        Processo processo = processoService.buscarPorId(processoId);
        List<SubPasso> subPassos = subPassoService.listarPorProcesso(processoId);

        model.addAttribute("processo", processo);
        model.addAttribute("subPassos", subPassos);
        model.addAttribute("tema", tema);
        model.addAttribute("nomeUsuario", session.getAttribute("nomeUsuario")); // ← ADICIONAR LINHA

        return "subpassos/lista";
    }

    // Formulário para novo subpasso
    @GetMapping("/novo/{processoId}")
    public String novoSubPasso(@PathVariable Long processoId,
                               @RequestParam(required = false) String tema,
                               Model model) throws Exception {
        Processo processo = processoService.buscarPorId(processoId);
        SubPasso subPasso = new SubPasso();
        subPasso.setProcesso(processo);

        model.addAttribute("subPasso", subPasso);
        model.addAttribute("tema", tema);
        return "subpassos/formulario";
    }

    // Salvar novo subpasso
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute SubPasso subPasso,
                         @RequestParam(required = false) String tema,
                         RedirectAttributes redirectAttributes) throws Exception {
        subPassoService.criarSubPasso(subPasso);
        redirectAttributes.addFlashAttribute("sucesso", "Subpasso criado com sucesso!");
        return "redirect:/subpassos/processo/" + subPasso.getProcesso().getId() + "?tema=" + (tema != null ? tema : "");
    }

    // Formulário para editar subpasso
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         @RequestParam(required = false) String tema,
                         Model model) throws Exception {
        SubPasso subPasso = subPassoService.buscarPorId(id);
        model.addAttribute("subPasso", subPasso);
        model.addAttribute("tema", tema);
        return "subpassos/formulario";
    }

    // Atualizar subpasso
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute SubPasso subPassoAtualizado,
                            @RequestParam(required = false) String tema,
                            RedirectAttributes redirectAttributes) throws Exception {
        SubPasso subPasso = subPassoService.buscarPorId(id);

        // Atualizar campos
        subPasso.setDescricao(subPassoAtualizado.getDescricao());
        subPasso.setOrdem(subPassoAtualizado.getOrdem());
        subPasso.setObrigatorio(subPassoAtualizado.isObrigatorio());
        subPasso.setConcluido(subPassoAtualizado.isConcluido());

        subPassoService.criarSubPasso(subPasso);
        redirectAttributes.addFlashAttribute("sucesso", "Subpasso atualizado com sucesso!");
        return "redirect:/subpassos/processo/" + subPasso.getProcesso().getId() + "?tema=" + (tema != null ? tema : "");
    }

    // Deletar subpasso
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id,
                          @RequestParam(required = false) String tema,
                          RedirectAttributes redirectAttributes) throws Exception {
        SubPasso subPasso = subPassoService.buscarPorId(id);
        Long processoId = subPasso.getProcesso().getId();

        subPassoService.deletar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Subpasso deletado com sucesso!");
        return "redirect:/subpassos/processo/" + processoId + "?tema=" + (tema != null ? tema : "");
    }

    // Marcar como concluído
    @PostMapping("/concluir/{id}")
    public String concluir(@PathVariable Long id,
                           @RequestParam(required = false) String tema,
                           RedirectAttributes redirectAttributes) throws Exception {
        SubPasso subPasso = subPassoService.marcarComoConcluido(id);
        redirectAttributes.addFlashAttribute("sucesso", "Subpasso marcado como concluído!");
        return "redirect:/subpassos/processo/" + subPasso.getProcesso().getId() + "?tema=" + (tema != null ? tema : "");
    }

    // Desmarcar conclusão
    @PostMapping("/desconcluir/{id}")
    public String desconcluir(@PathVariable Long id,
                              @RequestParam(required = false) String tema,
                              RedirectAttributes redirectAttributes) throws Exception {
        SubPasso subPasso = subPassoService.desmarcarConclusao(id);
        redirectAttributes.addFlashAttribute("sucesso", "Subpasso desmarcado como concluído!");
        return "redirect:/subpassos/processo/" + subPasso.getProcesso().getId() + "?tema=" + (tema != null ? tema : "");
    }
}
