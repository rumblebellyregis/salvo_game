package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> playerShips;
    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> playerSalvos;
    public GamePlayer() { }

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getPlayerShips() {
        return playerShips;
    }

    public void setPlayerShips(Set<Ship> playerShips) {
        this.playerShips = playerShips;
    }

    public Set<Salvo> getPlayerSalvos() {
        return playerSalvos;
    }

    public void setPlayerSalvos(Set<Salvo> playerSalvos) {
        this.playerSalvos = playerSalvos;
    }

    public void addSubscription(Ship gamePlayer) {
        gamePlayer.setGamePlayer(this);
        playerShips.add(gamePlayer);

    }

    public void addSubscription(Salvo gamePlayer) {
        gamePlayer.setGamePlayer(this);
        playerSalvos.add(gamePlayer);

    }

    public Set<Ship> playerShips() {
        return playerShips;
    }
}
