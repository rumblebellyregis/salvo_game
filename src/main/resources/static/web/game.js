var app = new Vue({

    el: '#vue-app',

    data: {

        playerlist: null,
        th1: ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'],
        th2: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        number: -1,
        you: null,
        opponent: null,
        ships: [],
        shipList: {
            "airplaneCarrier": [1, 2, 3, 4, 5],
            "battleShip": [1, 2, 3, 4],
            "submarine": [1, 2, 3],
            "destroyer": [1, 2, 3],
            "patrolBoat": [1, 2],
        },
        direction: null,
        shipSelected: null,
        shipDiv: true,
        changeDiv: true,
        dummyList: [],
        turnNumber: 0,
        salvosToSend: [],
        salvoLength: 5,
        yourSalvos: [],
        yourHistory:[],
        otherHistory:[],
        checker:false,
        checkertext:null,
        timer:"",


    },
    mounted() {
        this.locationGetter()


    },
    methods: {
        salvoSender: function () {
            body = "locations=" + this.salvosToSend
            this.salvosToSend=[];
            this.turnNumber = this.turnNumber + 1
            url = "/api/addSalvos/" + this.number + "/" + this.turnNumber;

            fetch(url, {
                credentials: 'include',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: body
            }).then(function (response) {
                response.status

                if (response.status == 201) {
                    return response.json().then((data) => {
                    for(i=0; i<data.locations.length; i++){
                        app.yourSalvos.push(data.locations[i])
                    }
                    app.statusReportGetter();
                    })
                } else {
                    return response.json().then((data) => {
                        alert(data.error)
                        location.reload()
                    })
                }
            }, function (error) {
                alert(error.message)

            })
        },
        shipChecker: function (x, y) {
            if (this.direction == null) {
                return alert("select a direction")
            }
            if (this.shipSelected == null) {
                return alert("Select a ship to send")
            }
            var table = document.getElementById("shiptable")
            rowNumber = (table.rows.length)
            columnNumber = (table.rows[0].cells.length)
            columnIndex = this.th1.indexOf(x) + 1;
            rowIndex = y
            locations = []
            if (this.direction == "vertical") {
                if ((rowNumber - rowIndex) < this.shipList[this.shipSelected].length) {
                    return alert("you shall not pass there")
                } else {

                    for (i = 0; i < this.shipList[this.shipSelected].length; i++) {
                        if (this.ships.indexOf(x + (y + i)) >= 0) {
                            locations = []
                            return alert("one or more the locations occupied")

                        }
                        for (j = 0; j < this.dummyList.length; j++) {
                            for (k = 0; k < this.dummyList[j].locations.length; k++) {
                                if (this.dummyList[j].locations[k] == x + (y + i)) {
                                    locations = []
                                    return alert("something something happened")
                                }
                            }
                        }
                        locations.push(x + (y + i))

                        //                    
                    }
                }
            }
            if (this.direction == "horizantal") {
                if ((columnNumber - columnIndex) < this.shipList[this.shipSelected].length) {
                    return alert("you shall not pass")
                } else {

                    for (i = 0; i < this.shipList[this.shipSelected].length; i++) {
                        index = this.th1.indexOf(x)
                        if (this.ships.indexOf(this.th1[index + i] + y) >= 0) {
                            locations = []
                            return alert("one or more the locations occupied")

                        }
                        for (j = 0; j < this.dummyList.length; j++) {
                            for (k = 0; k < this.dummyList[j].locations.length; k++) {
                                if (this.dummyList[j].locations[k] == this.th1[index + i] + y) {
                                    locations = []
                                    return alert("something something happened")
                                }
                            }
                        }

                        locations.push(this.th1[index + i] + y)
                    }
                }


            }

            if (locations != []) {
                var object = {
                    "name": this.shipSelected,
                    "locations": locations,
                    "direction": this.direction
                }
                this.dummyList.push(object)
                this.dummyColor()
                delete this.shipList[this.shipSelected]

            }
        },
        dummyColor: function () {
            var table = document.getElementById("shiptable")
            for (j = 0; j < this.dummyList.length; j++) {
                for (k = 0; k < this.dummyList[j].locations.length; k++) {
                    coordinate = this.dummyList[j].locations[k];
                    for (i = 0; i < this.th1.length; i++) {
                        if (coordinate[0] == this.th1[i]) {
                            t = Number(coordinate.slice(1))
                            table.rows[t].cells[i + 1].style.backgroundColor = "green"
                        }
                    }
                }
            }
        },
        shipSender: function () {
            for (i = 0; i < this.dummyList.length; i++) {
                body = "shipClass=" + this.dummyList[i].name + "&locations=" + this.dummyList[i].locations + "&direction=" + this.dummyList[i].direction
                app.shipAdder(body)
            }
            this.dummyList = [];
        },
        shipAdder: function (bodyMessage) {
            this.number = Number(this.number)
            url = "/api/addShips/" + num;

            fetch(url, {
                credentials: 'include',
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: bodyMessage
            }).then(function (response) {
                response.status

                if (response.status == 201) {
                    return response.json().then((data) => {
                        for(i=0;i<data.locations.length; i++){
                            app.ships.push(data.locations[i])
                            app.testColor("shiptable",data.locations[i],"red")
                        }
                    })
                } else {
                    return response.json().then((data) => {
                        alert(data.error)
                        location.reload()
                    })
                }
            }, function (error) {
                alert(error.message)

            })
        },
        shipRemover: function () {
            var table = document.getElementById("shiptable")
            for (i = 0; i < this.dummyList.length; i++) {
                if (this.dummyList[i].name == this.shipSelected) {
                    for (k = 0; k < this.dummyList[i].locations.length; k++) {
                        coordinate = this.dummyList[i].locations[k];                
                        for (j = 0; j < this.th1.length; j++) {
                            if (coordinate[0] == this.th1[j]) {
                                t = Number(coordinate.slice(1))
                                table.rows[t].cells[j + 1].style.backgroundColor = "transparent"
                            }
                        }
                    }
                    this.shipList[this.dummyList[i].name] = this.dummyList[i].locations;
                    this.dummyList.splice(i, 1)
                }

            }
        },
        shipReturned: function (ship) {
            this.shipDiv = false;
            deleter = ship.type
            delete this.shipList[deleter]
            this.shipDiv = true;
            for (i = 0; i < ship.locations.length; i++) {
                this.ships.push(ship.locations[i])
            }

            this.shipParser()
        },

        loadData: function (url) {

            fetch(url, {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => app.consoler(json))
                .catch(e => console.log(e));


        },
        consoler: function (json) {
            this.playerlist = json
            id = this.playerlist.id
            this.shipPlacer();
            this.playerSeperator();
            this.statusReportGetter();
        },
        coordinates: function (x, y) {
            coordinate = x + y
           
            if (this.yourSalvos != [] && this.yourSalvos.indexOf(coordinate) >= 0) {
                return alert("you fired it already")
            }
            if (this.salvosToSend != [] && this.salvosToSend.indexOf(coordinate) >= 0) {
                index=this.salvosToSend.indexOf(coordinate)            
                this.salvosToSend.splice(index, 1);                
                this.testColor("targettable", coordinate, "transparent",coordinate)
                return;
            }
            if (this.salvosToSend.length >= this.salvoLength) {
                return alert("you reach your limit")
            }
            this.salvosToSend.push(x + y)
            this.testColor("targettable", coordinate, "yellow")
        },
        testColor: function (name, coordinate, color, turnValue) {
            var table = document.getElementById(name)
            for (k = 0; k < this.th1.length; k++) {
                if (coordinate[0] == this.th1[k]) {
                    t = Number(coordinate.slice(1))
                    table.rows[t].cells[k + 1].style.backgroundColor = color
                    if (turnValue != undefined)
                        table.rows[t].cells[k + 1].innerHTML = turnValue;
                }
            }
        },
        shipPlacer: function () {


            for (i = 0; i < this.playerlist.ships.length; i++) {
                delete this.shipList[this.playerlist.ships[i].type];
                for (j = 0; j < this.playerlist.ships[i].locations.length; j++) {
                    coordinate = this.playerlist.ships[i].locations[j]
                    this.ships.push(coordinate)

                }
            }
            this.shipParser()
        },
        shipParser() {
            var table = document.getElementById("shiptable")
            for (i = 0; i < this.ships.length; i++) {
                coordinate = this.ships[i]
                this.testColor("shiptable", coordinate, "red")
            }

        },
        salvoRemover: function () {
            for (i = 0; i < this.salvosToSend.length; i++) {
                this.testColor("targettable", this.salvosToSend[i], "transparent")
            }
            app.salvosToSend = [];

        },
        locationGetter: function () {
            var currentLocation = window.location;
            num = currentLocation.search.split('=').pop();
            this.number = num
            url = "../api/gameview/" + num;
            this.loadData(url)
        },
        playerSeperator: function () {
            this.you = app.playerlist.players.you
            if (app.playerlist.players.opp.hasOwnProperty('opppenent')) {
                this.opponent = app.playerlist.players.opp;

                this.salvoChecker();

            }
            this.salvoCheckerTarget()


        },
        salvoChecker: function () {
            var table = document.getElementById("shiptable")
            for (i = 0; i < this.opponent.salvo.length; i++) {
                for (j = 0; j < this.opponent.salvo[i].locations.length; j++) {
                    if (this.ships.indexOf(this.opponent.salvo[i].locations[j]) >= 0) {
                        coordinate = this.opponent.salvo[i].locations[j]
                        this.testColor("shiptable", coordinate, "blue", this.opponent.salvo[i].turn)

                    } else {
                        coordinate = this.opponent.salvo[i].locations[j]
                        this.testColor("shiptable", coordinate, "orange", this.opponent.salvo[i].turn)
                    }
                }
            }
        },
        salvoCheckerTarget: function () {
            var table2 = document.getElementById("targettable")

            for (i = 0; i < this.you.salvo.length; i++) {
                if (this.turnNumber < this.you.salvo[i].turn) {
                    this.turnNumber = this.you.salvo[i].turn
                }
                for (j = 0; j < this.you.salvo[i].locations.length; j++) {
                    coordinate = this.you.salvo[i].locations[j]
                    this.yourSalvos.push(coordinate)
                }
            }
        },
        statusReportGetter:function(){
            fetch("/api/statusReport/"+this.number, {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => {
                
                app.colorDistrubutor(json)})
                .catch(e => console.log(e));

            
        },
        colorDistrubutor:function(data){
            if(data.you.salvoStatus !=null){
            if(data.you.salvoStatus.length>0){
                for(i=0; i<data.you.salvoStatus.length; i++){
                    for(j=0; j<data.you.salvoStatus[i].status.length; j++){
                        if (data.you.salvoStatus[i].status[j].hit==true){
                            this.testColor("targettable", data.you.salvoStatus[i].status[j].location, "red", data.you.salvoStatus[i].turn)
                        }
                        else{
                           this.testColor("targettable", data.you.salvoStatus[i].status[j].location, "blue", data.you.salvoStatus[i].turn) 
                        }
                    }
                } 
            }
                }
            if(data.other.salvoStatus !=null){
               if(data.other.salvoStatus.length>0){
                for(i=0; i<data.other.salvoStatus.length; i++){
                    for(j=0; j<data.other.salvoStatus[i].status.length; j++){
                        if (data.other.salvoStatus[i].status[j].hit==true){
                            this.testColor("shiptable", data.other.salvoStatus[i].status[j].location, "blue", data.other.salvoStatus[i].turn)
                        }
                        else{
                           this.testColor("shiptable", data.other.salvoStatus[i].status[j].location, "orange", data.other.salvoStatus[i].turn) 
                        }
                    }
                } 
            }
                }
            this.historique(data)
        },
        historique:function(data){
            console.log(data)
            this.yourHistory=data.you;
            this.otherHistory=data.other;
            this.salvoLength=data.you.shipStatus;
        },
        textCorretor(hit,location,ship){
            
            if (hit==false){
                return "salvo send to "+location+" missed"
            }
            else {
                return "salvo send to "+location+" hit the "+ship;
            }
        
        },
        checkopp:function(){
            if (this.number>0){
              fetch("/api/checkOpponent/"+this.number, {
                    method: "GET",
                    credentials: "include",
                })
                .then(r => r.json())
                .then(json => {
                  
                               app.checkertext=json.status
                              app.checker=true;
                  if (json.status!="waiting for opponent" && app.opponent==null){
                      app.locationGetter()
                  }
                                
                  if(json.hasOwnProperty("ships")){
                      app.salvoLength=json.ships
                  }
                  if(json.status=="you lost"||json.status=="you win"||json.status=="its a tie"){
                      clearInterval(app.timer)
                      app.locationGetter();
                  }
                              })
                .catch(e => console.log(e));
        }  
            }
             

    },
      created: function () {
    this.checkopp();

   this.timer= setInterval(function () {
      this.checkopp();
    }.bind(this), 1500); 
  }
})
