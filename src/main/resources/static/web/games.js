Vue.component('yourpart', {
 data(){
    return {
      pageNumber: 0  
    }
},
    props:{
    listData:{
      type:Array,
      required:true
    },
    size:{
      type:Number,
      required:false,
      default: 3
    },
    returnGame:{
    type:Boolean,
      required:true
    }
},
    methods:{
      nextPage(){
         this.pageNumber++;
      },
      prevPage(){
        this.pageNumber--;
      },
        dateReturnner(date) {
           

            date = new Date(date);
            months = ["Jan.", "Feb.", "Mar.", "Apr", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."]
            m = date.getMonth()
            m = months[m];
            day = date.getDate()
            hour = date.getHours()
            min = date.getMinutes()

            if (day < 10) {

                day = "0" + day
            } else {
                day.toString()
            }

            if (min < 10) {

                min = "0" + min
            } else {
                min.toString()
            }

            if (hour < 10) {

                hour = "0" + hour
            } else {
                hour.toString()
            }

            date = day + "-" + m + "--" + hour + ":" + min
            return date
        },
        goto:function(link){
            
            window.location.href=link;
        },
        joinGame:function(id){
           
            body = "gameId="+id;
                fetch("/api/joinGame", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status

                        if (response.status == 201) {
                            return response.json().then((data) => { window.location.href=data.link })
                        } else  {
                           return response.json().then((data) => { alert(data.error) })
                        }
                    }, function (error) {
                    error.message //=> String
                })
        },
},  computed:{
    pageCount(){
      let l = this.listData.length,
          s = this.size;
      return Math.floor(l/s);
    },
    paginatedData(){
      const start = this.pageNumber * this.size,
            end = start + this.size;
      return this.listData
               .slice(start, end);
    }
  },
              

  template:  '<div><div class="wrap"><div v-for="game in paginatedData" class="game col-xs-3"><p>Game Create Date:{{dateReturnner(game.date)}}</p><p> Game ID:{{game.id}}</p> <div v-if="game.players" class="col-xs-8 col-xs-offset-2"> <div v-for="p in game.players" class="col-xs-8 col-xs-offset-2">         <p> Player Name: {{p.player.name}}</p> </div> </div><div class="col-xs-12"> <button v-if="returnGame" class="btn btn-success" v-on:click="goto(game.link)"> Return Game</button> <button v-if="!returnGame" class="btn btn-success" v-on:click="joinGame(game.id)"> Join Game</button></div></div> </div><div class="col-xs-12"><button class="btn btn-warning" :disabled="pageNumber === 0" @click="prevPage"> Previous </button><button class="btn btn-info" :disabled="pageNumber >= pageCount"            @click="nextPage"> Next </button></div></div></div>'
})
var app = new Vue({

    el: '#vue-app',

    data: {
        username: null,
        passWord: null,
        playerlist: null,
        leaderboard: null,
        incoming: false,
        error: "",
        errordiv: false,
        user: null,
        loginDiv: false,
        logoutDiv: false,
        yourGames:null,
        otherGames:null,
    },
    mounted() {
        //this.loadData()
        //this.loadLeaeder()
        //this.login()
        this.userCheck();
    },
    methods: {
        userCheck: function () {
            $.get("../api/current")
                .done(function (data) {
                    app.user = data;
                    
                    if (app.user == "no User Found") {
                        app.loginDiv = true;
                        app.logoutDiv = false;
                        app.incoming = false;
                    } else {
                        alert("Welcome " + app.user)
                        app.loadData();
                        app.loadLeaeder();
                        app.loginDiv = false;
                        app.logoutDiv = true;
                    }
                })
                .fail(function (jqXHR, textStatus) {
                    app.error = "Failed: " + textStatus;
                    app.errordiv = true;
                });
        },
        login: function () {
            
            if (this.username == "" || this.passWord == "") {
                alert("please fill the form")
            } else {
                body = "username=" + this.username + "&password=" + this.passWord
               
                fetch("/api/login", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status

                        if (response.status == 200) {
                            app.userCheck()
                        } else  {
                            alert("wrong password or username")
                        }
                    },              
  
                            function (error) {
                                error.message //=> String
                          

                    });
            }
        },
        createUser: function () {
            if (this.username == "" || this.passWord == "") {
                alert("please fill the form")
            } else {
                body = "userName=" + this.username + "&password=" + this.passWord
                fetch("/api/players", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                    response.status
                    if (response.status == 201) {
                        alert("account created")
                        app.login()
                    } else if(response.status == 409){
                        alert("user added already")
                    }
                }, function (error) {
                    error.message //=> String
                })

            }
        },
        logout: function () {
            fetch("/api/logout", {
                    credentials: 'include',
                    method: 'POST',

                })
                .then(r => console.log(r))
                .then(r => app.userCheck())
                .catch(e => console.log(e));

        },

        loadData: function () {
            $.get("../api/games2")
                .done(function (data) {
                    app.playerlist = data;
                    app.yourGames=app.playerlist.yours;
                    app.otherGames=app.playerlist.others;
                   
                    app.incoming = true
                })
                .fail(function (jqXHR, textStatus) {
                    app.error = "Failed: " + textStatus;
                    app.errordiv = true;
                });
        },
        loadLeaeder: function () {
            $.get("../api/leaderboard")
                .done(function (data) {
                    app.leaderboard = data.player;
                    

                    app.incoming = true
                })
                .fail(function (jqXHR, textStatus) {
                    app.error = "Failed: " + textStatus;
                    app.errordiv = true;
                });
        },
        dateReturnner(date) {
           

            date = new Date(date);
            months = ["Jan.", "Feb.", "Mar.", "Apr", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."]
            m = date.getMonth()
            m = months[m];
            day = date.getDate()
            hour = date.getHours()
            min = date.getMinutes()

            if (day < 10) {

                day = "0" + day
            } else {
                day.toString()
            }

            if (min < 10) {

                min = "0" + min
            } else {
                min.toString()
            }

            if (hour < 10) {

                hour = "0" + hour
            } else {
                hour.toString()
            }

            date = day + "-" + m + "--" + hour + ":" + min
            return date
        },
        goto:function(link){
           
            window.location.href=link;
        },
        joinGame:function(id){
           
            body = "gameId="+id;
                fetch("/api/joinGame", {
                    method: "POST",
                    body: body,
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status

                        if (response.status == 201) {
                            return response.json().then((data) => { app.goto(data.link) })
                        } else  {
                           return response.json().then((data) => { alert(data.error) })
                        }
                    }, function (error) {
                    error.message //=> String
                })
        },
        createGame:function(){
            fetch("/api/newGame", {
                    method: "POST",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    credentials: "same-origin"
                }).then(function (response) {
                        response.status

                        if (response.status == 201) {
                            return response.json().then((data) => { app.goto(data.link) })
                        } else  {
                           return response.json().then((data) => { alert(data.error) })
                        }
                    }, function (error) {
                    error.message //=> String
                })
        },
        
        }
    



})
