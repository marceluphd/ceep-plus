package br.com.alura.ceep.model;

import java.util.Collections;
import java.util.Map;

public class Erro {

    private Map<String, String> erros;


    public Map<String, String> getErros() {
        return Collections.unmodifiableMap(erros);
    }

    public void setErros(Map<String, String> erros) {
        this.erros = erros;
    }
}
