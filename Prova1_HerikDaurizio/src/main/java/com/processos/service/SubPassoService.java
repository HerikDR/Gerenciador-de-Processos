package com.processos.service;

import com.processos.model.Processo;
import com.processos.model.SubPasso;
import com.processos.repository.SubPassoRepository;
import com.processos.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubPassoService {

    @Autowired
    private SubPassoRepository subPassoRepository;

    @Autowired
    private ProcessoRepository processoRepository;

    // Criar novo SubPasso
    public SubPasso criarSubPasso(SubPasso subPasso) throws Exception {
        if (subPasso.getProcesso() == null || subPasso.getProcesso().getId() == null) {
            throw new Exception("Processo é obrigatório para criar um sub-passo");
        }

        if (subPasso.getOrdem() == null) {
            Integer proximaOrdem = obterProximaOrdem(subPasso.getProcesso().getId());
            subPasso.setOrdem(proximaOrdem);
        }

        return subPassoRepository.save(subPasso);
    }

    // Listar sub-passos de um processo
    public List<SubPasso> listarPorProcesso(Long processoId) {
        return subPassoRepository.findByProcessoIdOrderByOrdem(processoId);
    }

    // Buscar sub-passo por ID
    public SubPasso buscarPorId(Long id) throws Exception {
        return subPassoRepository.findById(id)
                .orElseThrow(() -> new Exception("Sub-passo não encontrado"));
    }

    // Marcar sub-passo como concluído
    public SubPasso marcarComoConcluido(Long id) throws Exception {
        SubPasso subPasso = buscarPorId(id);
        subPasso.marcarComoConcluido();
        SubPasso atualizado = subPassoRepository.save(subPasso);
        verificarConclusaoProcesso(subPasso.getProcesso().getId());

        return atualizado;
    }

    // Desmarcar conclusão do sub-passo
    public SubPasso desmarcarConclusao(Long id) throws Exception {
        SubPasso subPasso = buscarPorId(id);
        subPasso.desmarcarConclusao();
        SubPasso atualizado = subPassoRepository.save(subPasso);
        desmarcarConclusaoProcesso(subPasso.getProcesso().getId());
        return atualizado;
    }

    // Deletar sub-passo
    public void deletar(Long id) throws Exception {
        SubPasso subPasso = buscarPorId(id);
        subPassoRepository.delete(subPasso);
    }

    // Verificar se todos os sub-passos obrigatórios foram concluídos
    public boolean todosObrigatoriosConcluidos(Long processoId) {
        return subPassoRepository.todosObrigatoriosConcluidos(processoId);
    }

    // Obter próxima ordem para o processo
    private Integer obterProximaOrdem(Long processoId) {
        List<SubPasso> subPassos = subPassoRepository.findByProcessoIdOrderByOrdem(processoId);
        if (subPassos.isEmpty()) {
            return 1;
        }
        return subPassos.get(subPassos.size() - 1).getOrdem() + 1;
    }

    // Verificar se o processo pode ser marcado como concluído
    private void verificarConclusaoProcesso(Long processoId) {
        try {
            if (todosObrigatoriosConcluidos(processoId)) {
                Processo processo = processoRepository.findById(processoId)
                        .orElseThrow(() -> new Exception("Processo não encontrado"));
                if (!processo.isConcluido()) {
                    processo.setConcluido(true);
                    processoRepository.save(processo);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar conclusão do processo: " + e.getMessage());
        }
    }

    // Desmarcar conclusão do processo se há sub-passos pendentes
    private void desmarcarConclusaoProcesso(Long processoId) {
        try {
            Processo processo = processoRepository.findById(processoId)
                    .orElseThrow(() -> new Exception("Processo não encontrado"));
            if (processo.isConcluido()) {
                processo.setConcluido(false);
                processoRepository.save(processo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao desmarcar conclusão do processo: " + e.getMessage());
        }
    }
}

