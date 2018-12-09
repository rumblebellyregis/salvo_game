package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {



    List<GamePlayer> findById(Long id);
}
