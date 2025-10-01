package com.processos.service;

import com.processos.model.Processo;
import com.processos.model.SubPasso;
import com.processos.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessoService {

    @Autowired
    private ProcessoRepository processoRepository;

    public Processo cadastrarProcesso(Processo processo) throws Exception {
        if (processo.getDataInicio().isEqual(processo.getDataTermino())) {
            throw new Exception("A data de início não pode ser igual à data de término.");
        }

        if (processo.getSubPassos() != null && !processo.getSubPassos().isEmpty()) {
            for (SubPasso subPasso : processo.getSubPassos()) {
                subPasso.setProcesso(processo);
                if (subPasso.getDataCriacao() == null) {
                    subPasso.setDataCriacao(LocalDateTime.now());
                }
            }
        }

        return processoRepository.save(processo);
    }

    public List<Processo> listarAbertos() {
        return processoRepository.findAll();
    }

    public Processo buscarPorId(Long id) throws Exception {
        return processoRepository.findById(id)
                .orElseThrow(() -> new Exception("Processo não encontrado"));
    }

    public Processo atualizar(Long id, Processo processoAtualizado) throws Exception {
        Processo existente = buscarPorId(id);
        existente.setTitulo(processoAtualizado.getTitulo());
        existente.setDescricao(processoAtualizado.getDescricao());
        existente.setDataInicio(processoAtualizado.getDataInicio());
        existente.setDataTermino(processoAtualizado.getDataTermino());
        existente.setPrioridade(processoAtualizado.getPrioridade());
        existente.setFeitoPor(processoAtualizado.getFeitoPor());
        existente.setParaQuem(processoAtualizado.getParaQuem());
        existente.setConcluido(processoAtualizado.isConcluido());

        // Atualizar os subpassos
        existente.getSubPassos().clear();
        if (processoAtualizado.getSubPassos() != null && !processoAtualizado.getSubPassos().isEmpty()) {
            for (SubPasso subPasso : processoAtualizado.getSubPassos()) {
                subPasso.setProcesso(existente);
                if (subPasso.getDataCriacao() == null) {
                    subPasso.setDataCriacao(LocalDateTime.now());
                }
                existente.getSubPassos().add(subPasso);
            }
        }

        return processoRepository.save(existente);
    }

    public void deletar(Long id) throws Exception {
        Processo existente = buscarPorId(id);
        processoRepository.delete(existente);
    }

    public List<Processo> buscarPorTitulo(String titulo) {
        return processoRepository.findByTituloContainingIgnoreCase(titulo);
    }
}