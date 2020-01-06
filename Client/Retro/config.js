var gmsg;
var greceived;


var makeDelayBar = function (delay){
    var barType = 'progress-bar-success';
    if(delay > 2 && delay < 5){
        barType = 'progress-bar-warning';
    } else if (delay >= 5){
        barType = 'progress-bar-danger';
    } 
    var percentage = delay * 10;
    percentage = Math.min(percentage, 100);

    var html = delay + ' sec';
    html += '<div class="progress">';
    html += '<div class="progress-bar '+barType+'" role="progressbar" aria-valuenow="'+percentage+'"';
    html += 'aria-valuemin="0" aria-valuemax="100" style="width:'+percentage+'%">';
    html += '<span class="sr-only">' + percentage + '%</div>';
    html += '</div>';
    html += '</div>';
    return html;
};

var makeProgressBar = function (percentage){

    var html = percentage + '%';
    html += '<div class="progress">';
    html += '<div class="progress-bar" role="progressbar" aria-valuenow="'+percentage+'"';
    html += 'aria-valuemin="0" aria-valuemax="100" style="width:'+percentage+'%">';
    html += '<span class="sr-only">' + percentage + '%</div>';
    html += '</div>';
    html += '</div>';
    return html;
};

var updatePercentages = function(drop, error, delay){
    // hide and lock controls according to the network status
    console.log('updatePercentages', drop, error, delay);
    
    if(error > 0){
        $('#encrypt-node').removeClass('disabled');
        $('.decrypt-btn').removeClass('disabled');
        $('.verify-checksum-btn').removeClass('disabled');
        $('.encrypt-btn').removeClass('disabled');
        $('.add-checksum-btn').removeClass('disabled');
        $('.add-random-btn').removeClass('disabled');
    } else {
        $('#encrypt-node').addClass('disabled');
        $('.decrypt-btn').addClass('disabled');
        $('.verify-checksum-btn').addClass('disabled');
        $('.encrypt-btn').addClass('disabled');
        $('.add-checksum-btn').addClass('disabled');
        $('.add-random-btn').addClass('disabled');
    }
   
    // if at leas one of these is more than zero, show the status of the network 
    if(drop > 0 || error > 0 || delay > 0){
        $('#netstat').show(); 
    } else {
        $('#netstat').hide(); 
    }

    $('#p-drop').html(makeProgressBar(drop/100));
    $('#p-error').html(makeProgressBar(error/100));
    $('#delay').html(makeDelayBar(delay));
};

$(document).ready(function() {

     //this file contains only the default configurations for the BYOI object
    $('#loggedInButtons').hide();
    $('#main').hide();
    if (! ("WebSocket" in window)) {
        $('#sidebar').hide();
        $('body').append("<strong>Browser does not support Web Socket.</br>Please reload page in a modern Browser.</strong>");
    }else{
        var urlHost = GetURLParameter('host');
        if(urlHost === undefined) urlHost = '127.0.0.1';
        var urlPort = GetURLParameter('port');
        if(urlPort === undefined) urlPort = 10000;

        // the browser is from this millenium :D
        BYOI.config({
            // Configuration variables
            host : urlHost, // defaults to 127.0.0.1
            port : urlPort, // defaults to 10000
            MSG_MAX_LEN : 40, // defaults to 40

            // Configuration hooks
            // called when any message from the server is received
            onMessageReceived: function(type, msg){
                if(type == 'CONNECTED'){
                    $('#main').show();
                    $('#task-title').html('NODE '+BYOI.myNode+' your current task is:');
                }
                gmsg = msg;
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
