package com.processos.controller;

import com.processos.model.Processo;
import com.processos.service.ProcessoService;
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

    // LISTAR
    @GetMapping("")
    public String listarProcessos(Model model,
                                  @RequestParam(value = "tema", required = false) String tema,
                                  @RequestParam(value = "busca", required = false) String busca) {
        try {
            // lista "abertos"
            List<Processo> processos = processoService.listarAbertos();

            // filtro por busca
            if (busca != null && !busca.trim().isEmpty()) {
                processos = processos.stream()
                        .filter(p ->
                                (p.getTitulo() != null && p.getTitulo().toLowerCase().contains(busca.toLowerCase())) ||
                                        (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(busca.toLowerCase()))
                        )
                        .collect(Collectors.toList());
            }

            model.addAttribute("processos", processos);
            model.addAttribute("tema", tema);
            model.addAttribute("busca", busca);
            return "processos/lista";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "processos/lista";
        }
    }

    @GetMapping("/novo")
    public String novo(Model model, @RequestParam(value = "tema", required = false) String tema) {
        model.addAttribute("processo", new Processo());
        model.addAttribute("tema", tema);
        return "processos/formulario";
    }

    // POST
    @PostMapping("")
    public String salvar(@Valid @ModelAttribute("processo") Processo processo,
                         BindingResult result,
                         RedirectAttributes ra,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }

        try {
            processoService.cadastrarProcesso(processo);
            ra.addFlashAttribute("sucesso", "Processo criado com sucesso!");
            return "redirect:/processos";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }
    }

    // POST
    @PostMapping("/salvar")
    public String salvarProcessoCompat(@Valid @ModelAttribute("processo") Processo processo,
                                       BindingResult result,
                                       RedirectAttributes ra,
                                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }

        try {
            processoService.cadastrarProcesso(processo);
            ra.addFlashAttribute("sucesso", "Processo criado com sucesso!");
            return "redirect:/processos";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model,
                         @RequestParam(value = "tema", required = false) String tema) {
        try {
            Processo processo = processoService.buscarPorId(id);
            model.addAttribute("processo", processo);
            model.addAttribute("tema", tema);
            return "processos/formulario";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "processos/lista";
        }
    }

    // ATUALIZAR
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("processo") Processo processo,
                            BindingResult result,
                            RedirectAttributes ra,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }

        try {
            processoService.atualizar(id, processo);
            ra.addFlashAttribute("sucesso", "Processo atualizado com sucesso!");
            return "redirect:/processos";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("processo", processo);
            return "processos/formulario";
        }
    }

    // DELETAR
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            processoService.deletar(id);
            ra.addFlashAttribute("sucesso", "Processo deletado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/processos";
    }
}