package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private Integer turn;
    private ArrayList<String> salvoLocation;
    public Salvo() { }

    public Salvo(GamePlayer gamePlayer,Integer turn,ArrayList<String> salvoLocation) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocation = salvoLocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public ArrayList<String> getSalvoLocation() {
        return salvoLocation;
    }

    public void setSalvoLocation(ArrayList<String> salvoLocation) {
        this.salvoLocation = salvoLocation;
    }


}
