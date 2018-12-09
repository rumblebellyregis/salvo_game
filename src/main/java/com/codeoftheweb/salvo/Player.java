package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class  Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;


    private String passWord;


    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> gameScores;

    public Player() {
    }

    public Player(String user, String passWord) {

        this.userName = user;
        this.passWord=passWord;


    }

    public String getPassWord() {

        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void addSubscription(GamePlayer player) {
        player.setPlayer(this);
        gamePlayers.add(player);

    }

    public String getUserName() {
        return this.userName;
    }

    public void setFirstName(String user) {
        this.userName = user;
    }


    public Long getUserId() {
        return this.id;
    }

    public void setUserId(Long id) {
        this.id = id;
    }

    public void addGamescore(Score player) {
        player.setPlayer(this);
        gameScores.add(player);

    }

    public String toString() {
        return this.userName;
    }

    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }





}
