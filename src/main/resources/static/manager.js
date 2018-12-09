var app = new Vue({

    el: '#vue-app',

    data: {
        username:"",
        playerlist:null,
        passWord:"",
        incoming:false,
        error:"",
        errordiv:false,
    },
     methods: {
    
         dataCheck:function(){
             if (this.username=="" || this.passWord==""){
                 alert("fill password and username both")
             }
             else{
                 alert("both are full")
                 this.login();
             }
         },
         login:function(){
        var player="name:"+app.username+"&pwd="+app.passWord
        console.log(player)
         fetch("/api/login", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: player,
                })
                .then(r =>console.log(r) )
                .catch(e => console.log(e));     

},
         }
     



})
    
    
