package com.processos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sub_passo")
public class SubPasso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título do sub-passo é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Size(max = 300, message = "Descrição deve ter no máximo 300 caracteres")
    @Column(length = 300)
    private String descricao;

    @Column(nullable = false)
    private boolean obrigatorio = true;

    @Column(nullable = false)
    private boolean concluido = false;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Min(value = 1, message = "Ordem deve ser maior que zero")
    @Column(nullable = false)
    private Integer ordem;

    // Relacionamento ManyToOne com Processo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    // Construtor padrão
    public SubPasso() {
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor com parâmetros básicos
    public SubPasso(String titulo, boolean obrigatorio, Integer ordem) {
        this();
        this.titulo = titulo;
        this.obrigatorio = obrigatorio;
        this.ordem = ordem;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isObrigatorio() { return obrigatorio; }
    public void setObrigatorio(boolean obrigatorio) { this.obrigatorio = obrigatorio; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
        if (concluido && dataConclusao == null) {
            this.dataConclusao = LocalDateTime.now();
        } else if (!concluido) {
            this.dataConclusao = null;
        }
    }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public Processo getProcesso() { return processo; }
    public void setProcesso(Processo processo) { this.processo = processo; }

    // Métodos utilitários
    public String getStatusDescricao() {
        if (concluido) {
            return "Concluído";
        } else if (obrigatorio) {
            return "Pendente (Obrigatório)";
        } else {
            return "Pendente (Opcional)";
        }
    }

    public String getTipoDescricao() {
        return obrigatorio ? "Obrigatório" : "Opcional";
    }

    // Método para marcar como concluído
    public void marcarComoConcluido() {
        this.concluido = true;
        this.dataConclusao = LocalDateTime.now();
    }

    // Método para desmarcar conclusão
    public void desmarcarConclusao() {
        this.concluido = false;
        this.dataConclusao = null;
    }
}