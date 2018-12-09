package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class  Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private Date created;

    public Game() {
        created=new Date();
    }

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> gameScores;


    public Date getcreateDate() {
        return this.created;
    }

    public void setcreateDate(Date date) {
        this.created = new Date();
    }

    public Long getMatchId() {
        return this.id;
    }

    public void setMatchId(Long id) {
        this.id = id;
    }




    public void addGamePlayer(GamePlayer game) {
        game.setGame(this);
        gamePlayers.add(game);
    }

    public void addGamescore(Score game) {
        game.setGame(this);
        gameScores.add(game);
    }

}
