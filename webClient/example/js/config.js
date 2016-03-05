$(document).ready(function() {
    //this file contains only the default configurations for the BYOI object
    $('#loggedInButtons').hide();
    $('#main').hide();
    if (! ("WebSocket" in window)) {
        $('#sidebar').hide();
        $('body').append("<strong>Browser does not support Web Socket.</br>Please reload page in a modern Browser.</strong>");
    }else{
        // the browser is from this millenium :D
        BYOI.config({
            // Configuration variables
            host : 'localhost', // defaults to 127.0.0.1
            port : 10000, // defaults to 10000
            MSG_MAX_LEN : 40, // defaults to 40

            // Configuration hooks
            // called when any message from the server is received
            onMessageReceived: function(type, msg){
                if(type == 'CONNECTED'){
                    $('#connectionButtons').hide();
                    $('#loggedInButtons').show();
                    $('#main').show();
                } else if ( type == 'PACKET'){
                    //react in a meaningful way :)
                } else if ( type == 'TASK' ){
                    //react in a meaningful way :)
                }
            }, 
            // called when the connection to the server fails
            onConnectionError: function(){
                $('#main').show();
                BYOI.systemMessage("Connection Error - Is the server running?");
            },
            // called when the connection to the server is closed,
            // as they may be different reasons that can cause this,
            // they are passed to this function as parameters
            onConnectionClose: function(code, reason){
                BYOI.systemMessage('The connection was closed for reason ' + code + ': ' + reason );
            },
            // called after the current client sends a message to the
            // server
            onSend: function(msg){},
        });
    }

});
