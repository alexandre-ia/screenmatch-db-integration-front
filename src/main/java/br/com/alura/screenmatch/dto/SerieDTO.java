package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;


import java.util.ArrayList;
import java.util.List;

public record SerieDTO( Long id,
                        String titulo,
                        Integer totalTemporada,
                        Double avaliacao,
                        Categoria genero,
                        String atores,
                        String poster,
                         String sinopse) {



}
