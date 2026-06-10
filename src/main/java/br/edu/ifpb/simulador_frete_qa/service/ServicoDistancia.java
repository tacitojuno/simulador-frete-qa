package br.edu.ifpb.simulador_frete_qa.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.text.Normalizer;

@Service
public class ServicoDistancia {

    //Simulação de API de distância entre cidades (poderia ser substituída por uma integração real, como Google Maps API)
    private final Map<String, Double> tabelaRotas;

    public ServicoDistancia() {
        tabelaRotas = new HashMap<>();
        tabelaRotas.put("CAMPINA GRANDE-JOAO PESSOA", 130.0);
        tabelaRotas.put("CAMPINA GRANDE-RECIFE", 200.0);
        tabelaRotas.put("SAO PAULO-RIO DE JANEIRO", 430.0);
        //Adicionar mais rotas conforme necessário... (Provavelmente será substituído por uma base de dados real ou API externa)
    }

    public Double buscarDistancia(String cidadeOrigem, String cidadeDestino){
        String origemLimpa = removerAcentos(cidadeOrigem).toUpperCase();
        String destinoLimpo = removerAcentos(cidadeDestino).toUpperCase();

        String rotaNormal = origemLimpa + "-" + destinoLimpo;
        String rotaInvertida = destinoLimpo + "-" + origemLimpa;

        if(tabelaRotas.containsKey(rotaNormal)){
            return tabelaRotas.get(rotaNormal);
        } else if(tabelaRotas.containsKey(rotaInvertida)){
            return tabelaRotas.get(rotaInvertida);
        }

        throw new IllegalArgumentException("Rota não atendida pela transportadora.");
    }

    private String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
