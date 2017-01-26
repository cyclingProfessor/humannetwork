$(document).ready(function() {
    //bind elements of the DOM to BYOI methods
    
    $('#connectButton').click(function(){
        // attempt connection to the server
        BYOI.connect();
    });


    $('#deleteButton').click(function(){
        // delete all selected messages from the main Message Handler
        $('#messageList').getSelectedMessages().remove();
    });

    $('#addButton').click(function(){
        //this is just an example, the final html should be defined by the 
        //student, all the meta-data should be added here though
        //
        //in this place we show a "manual" way to create the message and
        //add the meta-data to it. But the API provides a helper method
        //to create the message in a nicer way. An example on how to 
        //use this part of the API is shown in the connection.js file
        $('#messageList').getSelectedMessages().toggleSelectMessage();
        var msg = $('<div class="added"><span class="text">' + $('#msg').val() + '</span>&nbsp;</div>').BYOIMessage();
        msg.relayMessage()
            .css('background', 'green')
            .animate(
                {'background-color':'transparent'}, 
                'slow', 
                'swing', 
                function(){$(this).removeAttr('style');
            });
    });


    // enable click selection of messages
    $(document).on('click', '.BYOI-message', function(){// bind to every BYOI message, regardless of their Message Handler
        // toggle selection
        $(this).toggleSelectMessage();
        // set the input value to the selected message's text 
        $('#msg').val($(this).data('text'));
    });

    // bind combine method to the message handler
    $('#combineButton').click(function(){
        $('#messageList').combineMessages(); 
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });

    // bind split method to the message handler
    $('#splitButton').click(function(){
        $('<div><span class="text">' + $('#msg').val() +'</span></div>').BYOIMessage()
        .splitMessage().relayMessage();
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });

    // bind checksum method to the message
    $('#checksumButton').click(function(){
        // get the text
        var input = $('#msg');
        // create a new message from the input text and add a checksum to it
        var msg = $('<div><span class="text">' + input.val() +'</span></div>').BYOIMessage()
        .addChecksum().relayMessage();
        // add the message to the input (update field value)
        BYOI.addMessageToContainer(msg, input);
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });
    
    // bind verify checksum method to the message handler
    $('#verifyButton').click(function(){
        // create a new message and verify the checksum
        var verify = $('<div><span class="text">' + $('#msg').val() +'</span></div>').BYOIMessage().verifyChecksum();
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });

    // bind encryption method to the message handler
    $('#encryptButton').click(function(){
        // encrypt the last select
        var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>').BYOIMessage().encryptMessage(
            parseInt(+$('#recipient').val()) // encryption key is the recipient node
        ).relayMessage(); // send to every message handler
        // add the message to the input (update field value)
        BYOI.addMessageToContainer(msg, $('#msg'));
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });

    // bind decryption method to the message handler
    $('#decryptButton').click(function(){
        var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>').BYOIMessage().decryptMessage(
            parseInt(+$('#recipient').val()) // decryption key is the recipient node
        ).relayMessage(); // send to every message handler
        // add the message to the input (update field value)
        BYOI.addMessageToContainer(msg, $('#msg'));
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });

    // bind random number method to the message handler
    $('#randomButton').click(function(){
        // add a random number to the last selected element of the message handler
        var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>').BYOIMessage()
        .addRandomNumber().relayMessage();
        // add the message to the input (update field value)
        BYOI.addMessageToContainer(msg, $('#msg'));
    });

    // bind move up method to the message handler
    $('#upButton').click(function(){
        //for each selected message in the main message handler
        $('#messageList').getSelectedMessages().each(function(){
            //get the index of the current element
            var index = $(this).index();
            // if there is some room up
            if(index > 1){
                // get the previous element in the message handler
                var previous = $('#messageList').children().eq(index-1);
                // if it's not selected, swap
                if(!previous.hasClass('BYOI-selected')){
                    $(this).detach().insertBefore(previous);
                }
            }
        });
    });
    
    // bind move down method to the message handler
    $('#downButton').click(function(){
        //for each selected message in the main message handler
        $($('#messageList').getSelectedMessages().get().reverse()).each(function(){
            //get the index of the current element
            var index = $(this).index();
            var length = $(this).parent().children().length;
            // if there is some room down
            if(index < length - 1){
                // get the previous element in the message handler
                var next = $('#messageList').children().eq(index+1);
                // if it's not selected, swap
                if(!next.hasClass('BYOI-selected')){
                    $(this).detach().insertAfter(next);
                }
            }
        });
    });

    // bind send method to the message handler
    $('#sendButton').click(function(){
        // create a new message 
        // NOTE at least one tag with class "text" should be inside the 
        // html of a message, otherwise the content sent will be an 
        // empty string
        //
        // also, notice that because of this, the message is not inserted with 
        // all the html tags provided, but only those that were inside the 
        // tag with the "text" class.
        var html = '<div><span class="text">'+$('#msg').val()+'</span></div>';
        // sent message to the server
        $(html).BYOIMessage().send($('#recipient').val()).css('background', 'gold');
        $('#messageList').getSelectedMessages().toggleSelectMessage();
    });


    // bind close connection method to the message handler
    $('#closeButton').click(function() {
        BYOI.connection.close();
    });


    // create a System Alert
    //$('#systemMessage').BYOISystemAlert();
    $('#systemMessage').BYOISystemAlert({
        //this is the default behaviour if the onAlert property is not provided
        onAlert:function(alert){ 
            $('#systemMessage').html(alert); 
        }
    });
    
    // create a Message Handler
    $('#messageList').BYOIMessageHandler({
        accept:function(message){ 
            return true; 
        }, 
        onError:function(msg){console.log(msg);}
    });

    // create a Message Handler with a filter
    encrypted = $('<div id="encryptedList">encrypted messages</div>');
    encrypted.BYOIMessageHandler({
        accept: function(msg){
            return msg.hasClass('encrypted');
        }
    });
    // add the second message handler to the DOM
    $('#sidebar').append(encrypted);
});
