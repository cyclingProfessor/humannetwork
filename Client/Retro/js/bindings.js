var from1 = 'not';
var from2 = 'not';


$(document).ready(function() {
    $('#netstat').hide(); 

    var envMeta = function(){
        var meta = {
            text: $('#msg').val(),
            from: +BYOI.myNode,
            to: +$('#recipient').val(),
            type: 'MESSAGE',
            date: Date.now()
        };
        return meta;
    }

    var sendMessage = function(){
        if(!$("#msg").val().length){
            $(".send-btn").notify("No message selected!", { position:"below" });
        }
        else{
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
            var nodeToSendTo = $('#recipient').val();
            if (!nodeToSendTo)
                nodeToSendTo = 0;
            var msg = $(html).BYOIMessage();
            if($("#msg").val().length > 40){
                console.log('Watch Out!');
                var meta = {
                    text: $('#msg').val(),
                    from: +BYOI.myNode,
                    to: nodeToSendTo,
                    type: 'MESSAGE',
                    date: Date.now()
                };
                msg.addMetadata(meta).relayMessage();
            } else {
                msg.send(nodeToSendTo);
            }

            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
            $("#msg").val('');
            $("#msg").html('');
        }
    };    

   
    BYOI.connect();
    $(".shell-top-bar").val(BYOI.myName);
    $(".shell-body").sortable();
   
    function populateBar() {
        alert("populate bar is called");
    }

    $("#msg").on('keydown', function(e){
        if (e.which == 13) { //ENTER
            sendMessage();
            e.preventDefault();
        }
    });

    // check for exceeded char length
    // TODO - Nuno should make this nicer!
    $("#msg").change(checklength);
    $("#msg").keyup(checklength);

    function checklength() {
        if($("#msg").val().length >= 41){
           // $("#msg").notify("Message over 40 chars!\n This will require you to split the message before sending!");
           $("#msg").css('border-color', 'red')
           $("#msg").css('background-color', 'pink')
        }
        if($("#msg").val().length < 41){
           $("#msg").css('border-color', '')
           $("#msg").css('background-color', '')
        }
    }

    $('.delete-msg-btn').click(function(){
        // delete all selected messages from the main Message Handler
        if(!$("#msg").val().length){

               $(".delete-msg-btn").notify("No message selected!", { position:"right" });
        }
        else{
            $('.BYOI-messageHandler.active').getSelectedMessages().remove();
            $('#msg').val('');
        }

    });    


    // enable click selection of messages
    $(document).on('click', '.BYOI-message', function(){// bind to every BYOI message, regardless of their Message Handler
        // toggle selection
        $(this).toggleSelectMessage();
        // set the input value to the selected message's text 
        $('#msg').val($(this).data('text'));
        checklength();
    });

    // bind combine method to the message handler
    $('.combine-btn').click(function(){
        if(!$("#msg").val().length){

               $(".combine-btn").notify("No message selected!", { position:"right" });
        }
        else{
            $('.BYOI-messageHandler.active').combineMessages(); 
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
    });

    // bind split method to the message handler
    $('.split-btn').click(function(){
        if(!$("#msg").val().length){

               $(".split-btn").notify("No message selected!", { position:"right" });
        }
        else{

            $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage(envMeta())
                .splitMessage()
                .relayMessage();
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
    });

    // bind checksum method to the message
    $('.add-checksum-btn').click(function(){
        if(!$("#msg").val().length){

               $(".add-checksum-btn").notify("No message selected!", { position:"right" });
        }
        else{
            // get the text
            var input = $('#msg');
            // create a new message from the input text and add a checksum to it
            var msg = $('<div><span class="text">' + input.val() +'</span></div>')
                .BYOIMessage(envMeta())
                .addChecksum()
                .relayMessage();
            console.log(msg.data());
            // add the message to the input (update field value)
            BYOI.addMessageToContainer(msg, input);
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
    });
    
    // bind verify checksum method to the message handler
    $('.verify-checksum-btn').click(function(){
        // create a new message and verify the checksum
        if(!$("#msg").val().length){
            $(".verify-checksum-btn").notify("No message selected!", { position:"right" });
        }
        else{
            var verify = $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage()
                .verifyChecksum();
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
        
    });

    // bind encryption method to the message handler
    $('.encrypt-btn').click(function(){
        if(!$("#msg").val().length){
            $(".encrypt-btn").notify("No message selected!", { position:"right" });
        }
        else{
            // encrypt the last select
            var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage(envMeta())
                .encryptMessage(parseInt(+$('#encrypt-node').val())) // encryption key is the recipient node
                .relayMessage(); // send to every message handler
            // add the message to the input (update field value)
            BYOI.addMessageToContainer(msg, $('#msg'));
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
    });

    // bind decryption method to the message handler
    $('.decrypt-btn').click(function(){
        if(!$("#msg").val().length){
            $(".decrypt-btn").notify("No message selected!", { position:"right" });
        }
        else{
            var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage(envMeta())
                .decryptMessage( 0 )
                .relayMessage(); // send to every message handler
            // add the message to the input (update field value)
            BYOI.addMessageToContainer(msg, $('#msg'));
            $('.BYOI-messageHandler.active').getSelectedMessages().toggleSelectMessage();
        }
    });

    // bind random number method to the message handler
    $('.add-random-btn').click(function(){
        if(!$("#msg").val().length){
            $(".add-random-btn").notify("No message selected!", { position:"right" });
        }
        else{
            // add a random number to the last selected element of the message handler
            var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage(envMeta())
                .addRandomNumber()
                .relayMessage();
            // add the message to the input (update field value)
            BYOI.addMessageToContainer(msg, $('#msg'));
        }
    });

    // bind send method to the message handler
    $('.send-btn').click(sendMessage);


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

    $('#currentTask').BYOIMessageHandler({
        accept: function(message){
            if(message.data('type') == 'TASK'){
                var data = message.data();
                var task = '';
                if(data.received.task == 'MESSAGE'){
                    task += 'Send the message "' + data.received.text;  
                    task += '" to node: ' + data.received.recipient;
                } else if (data.received.task == 'TOPOLOGY'){
                    task += 'Find all the connections in your network';
                } else if (data.received.task == 'WHOIS'){
                    task += 'Who is ' + data.received.other; 
                }
                console.log('task', task);
                var html = '<div class="task"><span class="text"> ' +task+ '</span></div>';
                message.html(html);
                gmsg = message;
                console.log(message);
                $('#currentTask .BYOI-message').each(function(){
                    $(this).hide();
                });

                updatePercentages(
                    data.received.PercentageDrop, 
                    data.received.PercentageError,
                    data.received.randDelay
                );
                return true;
            }
            return false;
        }
    });

    // Begin with an empty task
    // TODO This does not work!!!
    var initialTask = '<div><span class="text"> Please wait for your first Task </span></div>';
    BYOI.addMessageToContainer($(initialTask).BYOIMessage(),$('#currentTask'));
    
    // create a Message Handler for all the messages
    $('#all').BYOIMessageHandler({
        accept:function(message){ 
            if(message.data('type') != 'TASK'){
                $('#menu-all').addClass('unread');
                $('.nav li.active').removeClass('unread');
                return true; 
            }
            return false;
        }, 
        onError:function(msg){console.log(msg);}
    });

    // create a Message Handler for all the broadcasted messages
    $('#broadcast').BYOIMessageHandler({
        accept:function(message){ 
            var to = message.data('to');
            if (to == 0){
                $('#menu-broadcast').removeClass('hidden');
                $('#menu-broadcast').addClass('unread');
                $('.nav li.active').removeClass('unread');
                return true;
            }
            return false; 
        }, 
        onError:function(msg){console.log(msg);}
    });

    // create a Message Handler for all the messages from node 1
    $('#node-1').BYOIMessageHandler({
        accept:function(message){ 
            console.log('node-1-message',message); //borrar
            gmsg = message; //borrar
            var rtn = false;
            if(message.data('type') == 'PACKET' || message.data('type') == 'MESSAGE'){
                var to = message.data('to');
                var from = message.data('from');
                if(from != BYOI.myNode){ // incoming message
                    if(from1 == 'not'){
                        from1 = from;
                        $('#menu-node-1').removeClass('hidden');
                        $('#menu-node-1').attr('send-to', from);
                        $('#a-node-1').html('Node '+ from);
                    }
                    if (to != 0 && from == from1){ rtn = true; } 
                } else { // outgoing message
                    if(from1 == to){ rtn = true; }
                }
            }
            if(rtn){
                $('#menu-node-1').addClass('unread');
                $('.nav li.active').removeClass('unread');
            }
            return rtn; 
        }, 
        onError:function(msg){console.log(msg);}
    });

    // create a Message Handler for all the messages node 2
    $('#node-2').BYOIMessageHandler({
        accept:function(message){ 
            var rtn = false;
            if(message.data('type') == 'PACKET' || message.data('type') == 'MESSAGE'){
                var to = message.data('to');
                var from = message.data('from');
                if(from != BYOI.myNode){
                    if(from1 != 'not' && from != from1 && from2 == 'not'){
                        from2 = from;
                        $('#menu-node-2').removeClass('hidden');
                        $('#menu-node-2').attr('send-to', from);
                        $('#a-node-2').html('Node '+ from);
                    }
                    if (to != 0 && from == from2){ rtn = true; }
                    
                } else {
                    if (from2 == to){ rtn = true; }
                }
            }
            if(rtn){
                $('#menu-node-2').addClass('unread');
                $('.nav li.active').removeClass('unread');
            }
            return rtn; 
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

    // cause navbar to collapse when clicked on in mobile view

    $('.nav').on('click', function(){
        $('.navbar-toggle').click() //bootstrap 3.x by Richard
    });



    //lock the receipient depending on the clicked tab
    $(document).on('click', '.nav-tabs li', function(){
        var id = $(this).attr('id');
        $(this).removeClass('unread');
        if(id == 'menu-all'){
            $('#recipient').removeAttr("disabled");
        } else {
            $('#recipient').val($(this).attr('send-to'));
            $('#recipient').attr("disabled", "disabled");
        }
    });

    $('#recipient').tooltip({'trigger':'focus', 'title': 'Node Number (0 or empty for Broadcast)'});
});
