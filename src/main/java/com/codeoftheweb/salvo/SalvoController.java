package com.codeoftheweb.salvo;




import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.*;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository repo;
    @Autowired
    private GamePlayerRepository repo2;
    @Autowired
    private PlayerRepository repo3;
    @Autowired
    private ShipRepository repo4;
    @Autowired
    private SalvoRepository repo5;
    @Autowired
    private ScoreRepository repo6;
    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public List getGames() {
        List<Game> games = repo.findAll();
        List mainList = new ArrayList();
        for (Game game : games) {
            Map<String, Object> gameList = new LinkedHashMap<String, Object>();
            gameList.put("id",game.getMatchId());
            gameList.put("date",game.getcreateDate());


            Set<GamePlayer> gamePlayers = game.gamePlayers;
            List playerlist = new ArrayList();
            for (GamePlayer gamer : gamePlayers) {

                playerlist.add(makeGamePlayerDto(gamer));
                gameList.put("players",playerlist);

            }
            mainList.add(gameList);
        }


        return mainList;
    }

    private Object makeGamePlayerDto(GamePlayer gamer) {
        Map<String, Object> gamerList = new LinkedHashMap<String, Object>();
        gamerList.put("id",gamer.getId());
        gamerList.put("player",makePlayerDto(gamer.getPlayer()));

        return gamerList;
    }

    private Object makePlayerDto(Player player) {
        Map<String, Object> playerList = new LinkedHashMap<String, Object>();
        playerList.put("id",player.getUserId());
        playerList.put("name",player.getUserName());
        return playerList;
    }


    @RequestMapping("/gameview/{id}")

    public Map<String, Object> findGamePlayer(Authentication authentication,@PathVariable Long id) {
        Player player2=CurrentPlayer(authentication);
        if (player2==null){
            return null;
        }

        Map<String, Object> gamePlayerDto = new LinkedHashMap<String, Object>();
        Map<String, Object> youDto = new LinkedHashMap<String, Object>();
        Map<String, Object> oppDto = new LinkedHashMap<String, Object>();
        Optional<GamePlayer> gameplayer;
        gameplayer = Optional.of(repo2.findOne(id));
        Player playerGame=gameplayer.get().getPlayer();
        if(player2 != playerGame){
            return null;
        }
        Game game = gameplayer.get().getGame();
        gamePlayerDto.put("id",game.getMatchId());
        gamePlayerDto.put("date",game.getcreateDate());
        Set<Ship> ships = gameplayer.get().playerShips;
        List shipList = new ArrayList();
        for (Ship ship : ships) {
            shipList.add(makeShipDto(ship));
            ;
        }
        gamePlayerDto.put("ships",shipList);
        Set<GamePlayer> gamePlayers = game.gamePlayers;
        List gamePlayerList = new ArrayList();
        Map<String, Object> playerList = new LinkedHashMap<String, Object>();
        for (GamePlayer gps : gamePlayers) {

            if (gps.getId() == id) {
                youDto.put("id",gps.getId());
                Player player = gps.getPlayer();
                youDto.put("you",makePlayerDto(player));
                List salvoList = new ArrayList();
                Set<Salvo> salvos = gps.playerSalvos;
                for (Salvo salvo : salvos) {
                    salvoList.add(makeSalvoDto(salvo));
                }
                youDto.put("salvo",salvoList);
                Set<Score> scores = player.gameScores;
                ;
                youDto.put("score",(makeScoreDto(scores)));
            } else {
                oppDto.put("id",gps.getId());
                Player player = gps.getPlayer();
                oppDto.put("opppenent",makePlayerDto(player));
                List salvoList = new ArrayList();
                Set<Salvo> salvos = gps.playerSalvos;
                for (Salvo salvo : salvos) {
                    salvoList.add(makeSalvoDto(salvo));
                }
                oppDto.put("salvo",salvoList);
                Set<Score> scores = player.gameScores;
                ;
                oppDto.put("score",(makeScoreDto(scores)));
            }
        }
        playerList.put("you",youDto);
        playerList.put("opp",oppDto);
        gamePlayerList.add(playerList);
        gamePlayerDto.put("players",playerList);

        return gamePlayerDto;
    }

    private Object makeScoreDto(Set<Score> scores) {
        Map<String, Object> scoreList = new LinkedHashMap<String, Object>();
        Double total = 0.0;
        Integer win = 0;
        Integer lose = 0;
        Integer draw = 0;

        if (scores.size() == 0) {
            scoreList.put("total",total);
            scoreList.put("win",win);
            scoreList.put("draw",draw);
            scoreList.put("loser",lose);


        } else {
            for (Score score : scores) {


                total = total + score.getScore();
                if (score.getScore() == 1.0) {
                    win = win + 1;
                } else if (score.getScore() == 0.5) {
                    draw = draw + 1;
                } else {
                    lose = lose + 1;
                }

                scoreList.put("total",total);
                scoreList.put("win",win);
                scoreList.put("draw",draw);
                scoreList.put("loser",lose);


            }

        }
        return scoreList;
    }

    private Map<String, Object> makeSalvoDto(Salvo salvo) {
        Map<String, Object> salvoDto = new LinkedHashMap<String, Object>();
        salvoDto.put("locations",salvo.getSalvoLocation());
        salvoDto.put("turn",salvo.getTurn());
        salvoDto.put("salvoowner",salvo.getGamePlayer().getId());
        return salvoDto;
    }

    private Map<String, Object> makeShipDto(Ship ship) {
        Map<String, Object> shipDto = new LinkedHashMap<String, Object>();
        shipDto.put("id",ship.getId());
        shipDto.put("type",ship.getShipType());
        shipDto.put("locations",ship.getLocation());
        return shipDto;

    }

    @RequestMapping("/leaderboard")
    public Map<String, Object> getGameResults() {
        List<Player> players = repo3.findAll();
        Map<String, Object> wholeList = new LinkedHashMap<String, Object>();
        List<Map<String, Object>> playersall = new ArrayList<>();

        for (Player player : players) {
            Map<String, Object> playerList = new LinkedHashMap<String, Object>();
            playerList.put("name",player.getUserName());

            Set<Score> scores = player.gameScores;
            Double total = 0.0;
            Integer win = 0;
            Integer lose = 0;
            Integer draw = 0;

            if (scores.size() == 0) {
                playerList.put("total",total);
                playerList.put("win",win);
                playerList.put("draw",draw);
                playerList.put("loser",lose);


            } else {
                for (Score score : scores) {


                    total = total + score.getScore();
                    if (score.getScore() == 1.0) {
                        win = win + 1;
                    } else if (score.getScore() == 0.5) {
                        draw = draw + 1;
                    } else {
                        lose = lose + 1;
                    }

                    playerList.put("total",total);
                    playerList.put("win",win);
                    playerList.put("draw",draw);
                    playerList.put("loser",lose);


                }
            }
            playersall.add(playerList);

        }
        Collections.sort(playersall,new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> m1,Map<String, Object> m2) {
                return ((Double) m2.get("total")).compareTo((Double) m1.get("total"));
            }
        });

        wholeList.put("player",playersall);
        return wholeList;
    }

    public Player CurrentPlayer(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return repo3.findByUserName(authentication.getName());
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public String getAll(Authentication authentication) {
        if (authentication == null) {
            return "no User Found";
        }
        return repo3.findByUserName(authentication.getName()).getUserName();
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestParam String userName,@RequestParam String password) {
        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("No name given",HttpStatus.FORBIDDEN);
        }

        Player player = repo3.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>("Name already used",HttpStatus.CONFLICT);
        }

        repo3.save(new Player(userName,password));
        return new ResponseEntity<>("Name added",HttpStatus.CREATED);

    }

    @RequestMapping("/games2")
    public Map<String, Object> gameList(Authentication authentication) {
        Player player = CurrentPlayer(authentication);
        List<Object> yourGames = new ArrayList<>();
        Set<GamePlayer> playerGames = player.gamePlayers;
        List<Game> games = repo.findAll();
        for (GamePlayer playerGame : playerGames) {
            Map<String, Object> gamesList = new LinkedHashMap<String, Object>();
            List<Object> players = new ArrayList<>();
            if (playerGame.getGame().gameScores.size()==0){
                gamesList.put("id",playerGame.getGame().getMatchId());
                gamesList.put("date",playerGame.getGame().getcreateDate());
                gamesList.put("link","game.html?gp=" + playerGame.getId());
                for (GamePlayer gameplayer : playerGame.getGame().gamePlayers) {
                    players.add(makeGamePlayerDto(gameplayer));
                    gamesList.put("players",players);
                    gamesList.put("size",playerGame.getGame().gamePlayers.size());
                }
                yourGames.add(gamesList);
            }




            games.remove(playerGame.getGame());
        }
        Map<String, Object> gamesss = new LinkedHashMap<String, Object>();
        List mainList = new ArrayList();
        for (Game game : games) {
            if(game.gameScores.size()>0){

            }
            else{
                Set<GamePlayer> gamePlayers = game.gamePlayers;
                if (gamePlayers.size() == 2) {
                } else {
                    Map<String, Object> gameList = new LinkedHashMap<String, Object>();
                    gameList.put("id",game.getMatchId());
                    gameList.put("size",game.gamePlayers.size());
                    gameList.put("date",game.getcreateDate());
                    List playerlist = new ArrayList();

                    for (GamePlayer gameplayer : gamePlayers) {
                        playerlist.add(makeGamePlayerDto(gameplayer));
                        gameList.put("players",playerlist);

                    }
                    mainList.add(gameList);
                }
            }
            gamesss.put("yours",yourGames);
            gamesss.put("others",mainList);
            }

        return gamesss;
    }

    @RequestMapping(path = "/joinGame", method = RequestMethod.POST)

    public ResponseEntity<Map<String, Object>> joinGames(Authentication authentication,@RequestParam Long gameId) {
        Player player = CurrentPlayer(authentication);
        Game game = repo.getOne(gameId);
        Set<GamePlayer> gamer = game.gamePlayers;
        if (player == null) {
            return new ResponseEntity<>(makeMap("error","no user found"),HttpStatus.FORBIDDEN);
        }
        if (gamer != null && gamer.size() >= 2) {
            return new ResponseEntity<>(makeMap("error","game is full"),HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = new GamePlayer(game,player);
        repo2.save(gamePlayer);
        return new ResponseEntity<>(makeMap("link","game.html?gp=" + gamePlayer.getId()),HttpStatus.CREATED);


    }

    private Map<String, Object> makeMap(String key,Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key,value);
        return map;
    }

    @RequestMapping(path = "/newGame", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGames(Authentication authentication) {
        Player player = CurrentPlayer(authentication);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error","No user logged in"),HttpStatus.FORBIDDEN);
        } else {
            Game game = new Game();
            repo.save(game);
            GamePlayer gamePlayer = new GamePlayer(game,player);
            repo2.save(gamePlayer);
            return new ResponseEntity<>(makeMap("link","game.html?gp=" + gamePlayer.getId()),HttpStatus.CREATED);

        }

    }

    @RequestMapping(path = "/addShips/{gpId}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShip(Authentication authentication,@PathVariable Long gpId,@RequestParam String shipClass,@RequestParam ArrayList locations,@RequestParam String direction) {
        ArrayList<String> shipTypes = new ArrayList<>(
                Arrays.asList("airplaneCarrier","battleShip","submarine","destroyer","patrolBoat"));
        ArrayList<String> numbers = new ArrayList<>(
                Arrays.asList("1","2","3","4","5","6","7","8","9","10"));
        ArrayList<String> letters = new ArrayList<>(
                Arrays.asList("a","b","c","d","e","f","g","h","i","j"));
        ArrayList<String> directions = new ArrayList<>(
                Arrays.asList("horizantal","vertical"));
        Map<String, Integer> shipLengths = new LinkedHashMap<String, Integer>();
        shipLengths.put("airplaneCarrier",5);
        shipLengths.put("battleShip",4);
        shipLengths.put("submarine",3);
        shipLengths.put("destroyer",3);
        shipLengths.put("patrolBoat",2);
        Player player = CurrentPlayer(authentication);
        List<GamePlayer> gamers = repo2.findAll();
        if (gpId > gamers.size()) {
            return new ResponseEntity<>(makeMap("error","no gameplayer exists"),HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = repo2.getOne(gpId);


        Player player1 = gamePlayer.getPlayer();
        Set<Ship> ships = gamePlayer.playerShips;
        for (Ship ship : ships) {
            if (shipTypes.contains(ship.getShipType())) {
                shipTypes.remove(ship.getShipType());
            }

        }


        if (player == null) {
            return new ResponseEntity<>(makeMap("error","No user logged in"),HttpStatus.FORBIDDEN);
        }
        if (player1 != player) {
            return new ResponseEntity<>(makeMap("error","Not this users game"),HttpStatus.FORBIDDEN);
        }
        if (ships != null && ships.size() >= 5) {
            return new ResponseEntity<>(makeMap("error","Ships Already Full"),HttpStatus.FORBIDDEN);
        }

        boolean decision = false;
        for (Integer i = 0; i < shipTypes.size(); i++) {
            if (shipTypes.get(i).equals(shipClass)) {
                decision = true;
            }
        }
        if (decision == false) {
            return new ResponseEntity<>(makeMap("error","wrong ship type or ship already added"),HttpStatus.FORBIDDEN);
        }

        if (shipLengths.get(shipClass).intValue() != locations.size()) {
            return new ResponseEntity<>(makeMap("error","Wrong size of ship"),HttpStatus.FORBIDDEN);
        }

        ArrayList<String> shipLocations = new ArrayList<>();
        for (Ship ship : ships) {
            for (Integer i = 0; i < ship.getLocation().size(); i++) {
                shipLocations.add(ship.getLocation().get(i));
            }
        }
        for (Integer i = 0; i < locations.size(); i++) {
            for (Integer j = 0; j < shipLocations.size(); j++) {
                if (shipLocations.get(j).equals(locations.get(i))) {
                    return new ResponseEntity<>(makeMap("error","This Place Already Full"),HttpStatus.FORBIDDEN);
                }
            }

        }

        if (directions.contains(direction) == false) {
            return new ResponseEntity<>(makeMap("error","No valid direction"),HttpStatus.FORBIDDEN);
        }

        for (Integer i = 0; i < locations.size(); i++) {
            String letter = locations.get(i).toString().substring(0,1);
            String number = locations.get(i).toString().substring(1);
            if (letters.contains(letter) == false || numbers.contains(number) == false) {
                return new ResponseEntity<>(makeMap("error","wrong coordinates"),HttpStatus.FORBIDDEN);
            }
        }
        if ("horizantal".equals(direction)) {

            for (Integer i = 0; i < locations.size() - 1; i++) {
                String letter1 = locations.get(i).toString().substring(0,1);
                String letter2 = locations.get(i + 1).toString().substring(0,1);
                String number1 = locations.get(i).toString().substring(1);
                String number2 = locations.get(i + 1).toString().substring(1);
                Integer index3 = numbers.indexOf(number1);
                Integer index4 = numbers.indexOf(number2);
                Integer index1 = letters.indexOf(letter1);
                Integer index2 = letters.indexOf(letter2);
                if (index2 - index1 > 1 || index3 != index4) {
                    return new ResponseEntity<>(makeMap("error","Ship coordinates must be sequential"),HttpStatus.FORBIDDEN);
                }
            }

        }
        if ("vertical".equals(direction)) {
            for (Integer i = 0; i < locations.size() - 1; i++) {
                String number1 = locations.get(i).toString().substring(1);
                String number2 = locations.get(i + 1).toString().substring(1);
                String letter1 = locations.get(i).toString().substring(0,1);
                String letter2 = locations.get(i + 1).toString().substring(0,1);
                Integer index1 = numbers.indexOf(number1);
                Integer index2 = numbers.indexOf(number2);
                Integer index3 = letters.indexOf(letter1);
                Integer index4 = letters.indexOf(letter2);
                if (index2 - index1 > 1 || index3 != index4) {
                    return new ResponseEntity<>(makeMap("error","Ship coordinates must be sequential"),HttpStatus.FORBIDDEN);
                }
            }

        }


        Ship shipp = new Ship(shipClass,locations,gamePlayer);
        repo4.save(shipp);

        return new ResponseEntity<>(makeShipDto(shipp),HttpStatus.CREATED);

    }

    @RequestMapping(path = "/addSalvos/{gpId}/{turn}", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(Authentication authentication,@PathVariable Long gpId,@RequestParam ArrayList locations,@PathVariable Integer turn) {
        Player player = CurrentPlayer(authentication);
        List<GamePlayer> gamers = repo2.findAll();


        if (gpId > gamers.size()) {
            return new ResponseEntity<>(makeMap("error","no gameplayer exists"),HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = repo2.getOne(gpId);
        Player player1 = gamePlayer.getPlayer();
        Game game=gamePlayer.getGame();
        Set<GamePlayer> list=game.gamePlayers;
        GamePlayer opponent=null;
        if(list.size()!=2){
            return  new ResponseEntity<>(makeMap("error","No opponent wait for this guy to fire"), HttpStatus.FORBIDDEN);
        }
        for(GamePlayer player2: list){
            if(player2 !=gamePlayer){
                opponent=player2;
            }
        }
        Set<Salvo> opponentSalvos=opponent.playerSalvos;
        Set<Ship> opponentShips=opponent.playerShips;
        Set<Ship> yourShips=gamePlayer.playerShips;
        Integer salvoSize = getShipStatus(yourShips,opponentSalvos);
        Integer oppSalvoSize=getShipStatus(opponentShips,gamePlayer.playerSalvos);
        if (player == null) {
            return new ResponseEntity<>(makeMap("error","No user logged in"),HttpStatus.FORBIDDEN);
        }
        if (player1 != player) {
            return new ResponseEntity<>(makeMap("error","Not this users game"),HttpStatus.FORBIDDEN);
        }
        if(opponent==null){
            return new ResponseEntity<>(makeMap("error", "no oppenent we suggest u not to fire any salvo and waste your turn"),HttpStatus.FORBIDDEN);
        }
        if(opponentShips.size()<5){
            return  new ResponseEntity<>(makeMap("error", "your opponent havent finished placing ship yet "),HttpStatus.FORBIDDEN);
        }
        if(yourShips.size()<5){
            return new ResponseEntity<>(makeMap("error", "First add ships"),HttpStatus.FORBIDDEN);
        }
        Set<Salvo> playerSalvos = gamePlayer.playerSalvos;
        Integer yourLastTurn=getLastTurn(playerSalvos);
        Integer oppenentLastTurn=getLastTurn(opponentSalvos);
        if (locations.size() > salvoSize&& turn>oppenentLastTurn) {
            return new ResponseEntity<>(makeMap("error","you can only send " + salvoSize + " salvos"),HttpStatus.FORBIDDEN);
        }
        ArrayList<String> salvoLocations = new ArrayList<>();


        for (Salvo salvo : playerSalvos) {
            for (Integer i = 0; i < salvo.getSalvoLocation().size(); i++) {
                salvoLocations.add(salvo.getSalvoLocation().get(i));
            }
        }

        if(oppSalvoSize==0){
            return new ResponseEntity<>(makeMap("error","game already finished why you send salvos"),HttpStatus.FORBIDDEN);
        }
        if (turn - yourLastTurn != 1) {
            return new ResponseEntity<>(makeMap("error","Turn number problem"),HttpStatus.FORBIDDEN);
        }
        if(turn-oppenentLastTurn >1){
            return new ResponseEntity<>(makeMap("error", "wait for other player to fire "),HttpStatus.FORBIDDEN);
        }
        for (Integer i = 0; i < locations.size(); i++) {
            for (Integer j = 0; j < salvoLocations.size(); j++) {
                if (locations.get(i).equals(salvoLocations.get(j))) {
                    return new ResponseEntity<>(makeMap("error","you already fired there you idiot"),HttpStatus.FORBIDDEN);
                }
            }
        }

        Salvo salvo = new Salvo(gamePlayer,turn,locations);
        repo5.save(salvo);
        return new ResponseEntity<>(makeSalvoDto(salvo),HttpStatus.CREATED);

    }

    @RequestMapping(path = "/statusReport/{gpId}", method = RequestMethod.GET)
    public Map<String, Object> getSalvoNumbers(Authentication authentication,@PathVariable Long gpId) {
        Player player = CurrentPlayer(authentication);
        if (player == null) {
            return null;
        }

        GamePlayer gamePlayer = repo2.getOne(gpId);
        Set<Salvo> yourSalvos = gamePlayer.playerSalvos;
        Set<Ship> yourShips = gamePlayer.playerShips;
        Map<String, Object> all = new LinkedHashMap<>();
        Map<String, Object> you = new LinkedHashMap<>();
        Map<String, Object> other = new LinkedHashMap<>();
        Game game = gamePlayer.getGame();
        Set<GamePlayer> gamePlayers = game.gamePlayers;
        GamePlayer opponent = null;
        for (GamePlayer gamer : gamePlayers) {
            if (gamer != gamePlayer) {

                opponent = gamer;
            }
        }
        Set<Salvo> opponentSalvos = null;
        Set<Ship> opponentShips = null;
        if (opponent != null) {
            opponentShips = opponent.playerShips;
            opponentSalvos = opponent.playerSalvos;
        }

        Player player1 = gamePlayer.getPlayer();
        if (player1 != player) {
            return null;
        }
        Integer yourLastTurn=getLastTurn(yourSalvos);
        Integer oppLastTurn=getLastTurn(opponentSalvos);
        Integer yourShipRemain=getShipStatus(yourShips,opponentSalvos);
        Integer oppShipRemain=getShipStatus(opponentShips,yourSalvos);
        if((oppShipRemain==0||yourShipRemain==0)&&(yourLastTurn>0&&oppLastTurn>0)&&game.gameScores.size()==0&&yourLastTurn==oppLastTurn){
            if(oppShipRemain>0&&yourShipRemain==0){
                Score sc2=new Score(player,game,new Date(),0.0);
                Score sc1=new Score(opponent.getPlayer(),game,new Date(),1.0);
                repo6.save(sc2);
                repo6.save(sc1);
            }
            if(oppShipRemain==0&&yourShipRemain>0){
                Score sc2=new Score(player,game,new Date(),1.0);
                Score sc1=new Score(opponent.getPlayer(),game,new Date(),0.0);
                repo6.save(sc2);
                repo6.save(sc1);
            }
            if(oppShipRemain==0&&yourShipRemain==0){
                Score sc1=new Score(opponent.getPlayer(),game,new Date(),0.5);
                Score sc2=new Score(player,game,new Date(),0.5);
                repo6.save(sc2);
                repo6.save(sc1);
            }
        }

        you.put("shipStatus", getShipStatus(yourShips,opponentSalvos));
        you.put("salvoStatus", getSalvoStatus(opponentShips,yourSalvos));
        other.put("shipStatus", getShipStatus(opponentShips,yourSalvos));
        other.put("salvoStatus", getSalvoStatus(yourShips,opponentSalvos));
        all.put("you",you);
        all.put("other",other);
        return all;
    }
    public Integer getShipStatus(Set<Ship> ships,Set<Salvo> salvoes) {
        if (ships==null||salvoes==null){
            return 5;
        }

        List<Map> opponentList=new ArrayList<>();
        Integer shipsLeft=5;
        for (Ship ship:ships){
            Integer count = 0;
            Integer turn=0;
            Integer maxTurn=0;

            for (Integer i=0; i<ship.getLocation().size();i++){
                for (Salvo salvo: salvoes){
                    for(Integer j=0; j<salvo.getSalvoLocation().size(); j++){
                        if (ship.getLocation().get(i).equals(salvo.getSalvoLocation().get(j))){
                            count=count+1;
                            turn=salvo.getTurn();
                            if(turn>maxTurn){
                                maxTurn=turn;
                            }
                        }
                    }
                }
            }
            Map<String, Object> shippie = new LinkedHashMap<>();

            if (count==ship.getLocation().size()) {
                shipsLeft = shipsLeft - 1;
                shippie.put("shipsLeft",shipsLeft);
                shippie.put(ship.getShipType(),"sunk");
                shippie.put("turn",maxTurn);
            }
                else{
                    shippie.put("shipsLeft",shipsLeft);
                    shippie.put("turn",turn);
                }




       opponentList.add(shippie);
        }
        return shipsLeft;
    }
    public Integer getShipStatusByTurn(Set<Ship> ships,Set<Salvo> salvoes,Integer turnValue) {
        if (ships==null||salvoes==null||turnValue==0){
            return 5;
        }

        List<Map> opponentList=new ArrayList<>();
        Integer shipsLeft=5;
        for (Ship ship:ships){
            Integer count = 0;


            for (Integer i=0; i<ship.getLocation().size();i++){
                for (Salvo salvo: salvoes){
                    if(salvo.getTurn()<=turnValue){
                        for(Integer j=0; j<salvo.getSalvoLocation().size(); j++){
                            if (ship.getLocation().get(i).equals(salvo.getSalvoLocation().get(j))){
                                count=count+1;

                            }
                        }
                    }
                }


                if (count==ship.getLocation().size()) {
                    shipsLeft = shipsLeft - 1;
                }
            }
                    }

        return shipsLeft;
    }
    public List<Map<String, Object>> getSalvoStatus(Set<Ship> ships,Set<Salvo> salvoes) {
        if (ships == null || salvoes == null) {

            return null;
        }
        List<Map<String, Object>> salvoList=new ArrayList<>();
        for (Salvo salvo:salvoes){
            Map<String, Object> turn = new LinkedHashMap<>();
            turn.put("turn",salvo.getTurn());
            List<Map> list=new ArrayList<>();
            String name=null;
            for (Integer i=0; i<salvo.getSalvoLocation().size(); i++){
                boolean hit=false;
                for (Ship ship:ships){
                    for (Integer j=0; j<ship.getLocation().size(); j++){
                        if (salvo.getSalvoLocation().get(i).equals(ship.getLocation().get(j))){
                            hit= true;
                            name=ship.getShipType();

                        }
                    }

                }
                Map<String, Object> opponentPart = new LinkedHashMap<>();

                    opponentPart.put("hit",hit);
                    opponentPart.put("location",salvo.getSalvoLocation().get(i));
                    if (name != null){
                        opponentPart.put("ship",name);
                    }
                    name=null;

                list.add(opponentPart);

            }
            turn.put("status",list);
            salvoList.add(turn);

        }
        Collections.sort(salvoList,new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> m1,Map<String, Object> m2) {
                return ((Integer) m2.get("turn")).compareTo((Integer) m1.get("turn"));
            }
        });
        return salvoList;
    }
    public Integer getLastTurn(Set<Salvo> salvoes) {
        Integer lastTurn = 0;
        if (salvoes==null){
            return 0;
        }
        else {
            for (Salvo salvo : salvoes) {
                if(lastTurn<salvo.getTurn()){
                    lastTurn=salvo.getTurn();
                }

            }
        }

        return  lastTurn;
    }
    @RequestMapping(path = "/checkOpponent/{gpId}", method = RequestMethod.GET)
    public Map<String, Object> getGameSituation(Authentication authentication,@PathVariable Long gpId) {
        Player player = CurrentPlayer(authentication);
        if (player == null) {
            return null;
        }
        GamePlayer gamePlayer=repo2.findOne(gpId);
        Game game=gamePlayer.getGame();
        Set<GamePlayer> list=game.gamePlayers;
        if (list.size()<2){
            return makeMap("status","waiting for opponent");
        }
        GamePlayer opponent=null;
        if (list.size()==2){
            for (GamePlayer gamePlayer1:list){
                if(gamePlayer != gamePlayer1){
                    opponent=gamePlayer1;
                }
            }
        }
        Set<Ship> yourShips=gamePlayer.playerShips;
        Set<Salvo> yourSalvos=gamePlayer.playerSalvos;
        Set<Ship> opShips=opponent.playerShips;
        Set<Salvo> opSalvos=opponent.playerSalvos;
        if(yourShips.size()<5){
            return  makeMap("status", "Place your ships");
        }
        if (opShips.size()<5){
            return makeMap("status","waiting for opponent ships");
        }
        Integer yourTurn=getLastTurn(yourSalvos);
        Integer opTurn=getLastTurn(opSalvos);
        Integer salvosize=getShipStatusByTurn(yourShips,opSalvos,yourTurn);
        LinkedHashMap<String,Object> mapple=new LinkedHashMap<>();
        if (yourTurn>opTurn){
            mapple.put("status", "waiting for opponent to finish turn");
            mapple.put("ships", salvosize);
            return mapple;
        }
        if (yourTurn<opTurn){

            mapple.put("status", "opponent waiting for you finish turn");
            mapple.put("ships", salvosize);
            return mapple;
        }
        if(game.gameScores.size()>0){
            for (Score score:game.gameScores){
                if(score.getPlayerScores()==player){
                    if(score.getScore()==0.0){
                        mapple.put("status", "you lost");
                    }
                    if(score.getScore()==1.0){
                        mapple.put("status","you win");
                    }
                    if(score.getScore()==0.5){
                        mapple.put("status","its a tie");
                    }
                }
            }

            return mapple;
        }
        mapple.put("status", "everythisngseemsnormal");
        mapple.put("ships", salvosize);
        return mapple;


    }


}






