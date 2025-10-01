package com.processos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processo")
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String titulo;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String descricao;

    @NotNull(message = "Data de início é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "Data de término é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_termino", nullable = false)
    private LocalDate dataTermino;

    @NotNull(message = "Prioridade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @NotBlank(message = "Responsável é obrigatório")
    @Size(max = 50, message = "Responsável deve ter no máximo 50 caracteres")
    @Column(name = "feito_por", nullable = false, length = 50)
    private String feitoPor;

    @NotBlank(message = "Destinatário é obrigatório")
    @Size(max = 50, message = "Destinatário deve ter no máximo 50 caracteres")
    @Column(name = "para_quem", nullable = false, length = 50)
    private String paraQuem;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "concluido")
    private boolean concluido = false;

    // Relacionamento um para muitos em sub passos
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("ordem ASC")
    private List<SubPasso> subPassos = new ArrayList<>();

    // Prioridade
    public enum Prioridade {
        OCIOSO("Ocioso"),
        BAIXA("Baixa"),
        MEDIA("Média"),
        ALTA("Alta"),
        URGENTE("Urgente");

        private final String descricao;

        Prioridade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Construtor padrão
    public Processo() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Getters e Setters completos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataTermino() { return dataTermino; }
    public void setDataTermino(LocalDate dataTermino) { this.dataTermino = dataTermino; }

    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }

    public String getFeitoPor() { return feitoPor; }
    public void setFeitoPor(String feitoPor) { this.feitoPor = feitoPor; }

    public String getParaQuem() { return paraQuem; }
    public void setParaQuem(String paraQuem) { this.paraQuem = paraQuem; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

    public List<SubPasso> getSubPassos() { return subPassos; }
    public void setSubPassos(List<SubPasso> subPassos) { this.subPassos = subPassos; }

    // Métodos para SubPassos
    public void adicionarSubPasso(SubPasso subPasso) {
        subPasso.setProcesso(this);
        if (subPasso.getOrdem() == null) {
            subPasso.setOrdem(subPassos.size() + 1);
        }
        this.subPassos.add(subPasso);
    }

    public void removerSubPasso(SubPasso subPasso) {
        this.subPassos.remove(subPasso);
        subPasso.setProcesso(null);
    }

    // Métodos utilitários
    public void definirDataInicioComoAgora() {
        this.dataInicio = LocalDate.now();
    }

    public String getPrioridadeDescricao() {
        return prioridade != null ? prioridade.getDescricao() : "";
    }

    public boolean isAtrasado() {
        return !concluido && LocalDate.now().isAfter(dataTermino);
    }

    public long getDiasRestantes() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dataTermino);
    }

    // Métodos de verificação de conclusão
    public boolean todosObrigatoriosConcluidos() {
        return subPassos.stream()
                .filter(SubPasso::isObrigatorio)
                .allMatch(SubPasso::isConcluido);
    }

    public int calcularProgresso() {
        if (subPassos.isEmpty()) return 0;
        long concluidos = subPassos.stream().mapToLong(sp -> sp.isConcluido() ? 1 : 0).sum();
        return (int) ((concluidos * 100) / subPassos.size());
    }

    public long contarObrigatoriosPendentes() {
        return subPassos.stream()
                .filter(SubPasso::isObrigatorio)
                .filter(sp -> !sp.isConcluido())
                .count();
    }
}