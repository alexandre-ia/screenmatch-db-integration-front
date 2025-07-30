package br.com.alura.screenmatch.service;


import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> obterTodasAsSeries() {
        return convertDados(repository.findAll());

             }


    public List<SerieDTO> obterTop5Series() {
        return  convertDados(repository.findTop5ByOrderByAvaliacaoDesc());

    }

    private List<SerieDTO> convertDados(List<Serie> serie) {
        return serie.stream()
                .map(s-> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporada(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse())).collect(Collectors.toList());

    }

    public List<SerieDTO> obterLancamento() {
        return convertDados(repository.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterPorId(Long id) {
       Optional <Serie> serie = repository.findById(id);
       if (serie.isPresent()) {
           Serie s = serie.get();
          return  new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporada(), s.getAvaliacao(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
       }
        return null;
    }
}
