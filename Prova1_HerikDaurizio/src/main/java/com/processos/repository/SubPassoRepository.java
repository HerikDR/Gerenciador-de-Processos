package com.processos.repository;

import com.processos.model.SubPasso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubPassoRepository extends JpaRepository<SubPasso, Long> {  // Adicione <SubPasso, Long> aqui

    List<SubPasso> findByProcessoIdOrderByOrdem(Long processoId);

    List<SubPasso> findByProcessoIdAndObrigatorioTrueOrderByOrdem(Long processoId);

    List<SubPasso> findByProcessoIdAndConcluidoFalseOrderByOrdem(Long processoId);

    @Query("SELECT COUNT(sp) FROM SubPasso sp WHERE sp.processo.id = :processoId AND sp.obrigatorio = true AND sp.concluido = false")
    Long contarObrigatoriosPendentes(@Param("processoId") Long processoId);

    @Query("SELECT CASE WHEN COUNT(sp) = 0 THEN true ELSE false END FROM SubPasso sp WHERE sp.processo.id = :processoId AND sp.obrigatorio = true AND sp.concluido = false")
    boolean todosObrigatoriosConcluidos(@Param("processoId") Long processoId);
}