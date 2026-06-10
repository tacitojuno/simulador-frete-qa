package br.edu.ifpb.sistema_entregas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_simulacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double peso;

    private String dimensoes;

    @Column(nullable = false)
    private String cidadeOrigem;

    @Column(nullable = false)
    private String cidadeDestino;

    private Integer prazoEstimadoDias;

    private Double custoTotal;

    private Double taxasAdicionais;

    private String observacoes;

    @Column(nullable = false)
    private LocalDateTime dataSimulacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipoItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModalidadeEnvio modalidadeEnvio;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
