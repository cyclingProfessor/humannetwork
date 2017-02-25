var neighbours = [];  // List of neighbours from whom we have seen a message

$(document).ready(function() {
    $('#netstat').hide(); 

    var sendMessage = function(){
        if(!$("#msg").val().length){
            $(".send-btn").notify("No message selected!", { position:"below" });
        }
        else{
            // create a new message 
            var html = '<div><span class="text">'+$('#msg').val()+'</span></div>';
            // sent message to the server
            var nodeToSendTo = $('#recipient').val();
            if (!nodeToSendTo)
                nodeToSendTo = 0;
            var msg = $(html).BYOIMessage();
            if($("#msg").val().length > 40){
                console.log('Watch Out!');
                msg.addMetadata().relayMessage();
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
                .BYOIMessage()
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
            var msg = $('<div><span class="text">' + $('#msg').val() +'</span></div>')
                .BYOIMessage()
                .addChecksum()
                .relayMessage();
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
                .BYOIMessage()
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
                .BYOIMessage()
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
                .BYOIMessage()
                .addRandomNumber()
                .relayMessage();
            // add the message to the input (update field value)
            BYOI.addMessageToContainer(msg, $('#msg'));
        }
    });

    // bind send method to the message handler
    $('.send-btn').click(sendMessage);

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
                } else if (data.received.task == 'NONE'){
                    task += 'Please wait for your first task';
                }
                var html = '<div class="task"><span class="text"> ' +task+ '</span></div>';
                message.html(html);
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
    var initialTask = '<div><span class="text"> wait </span></div>';
    var extra = { 'task': 'NONE'};
    var msg = $(initialTask).BYOIMessage();
    msg.data('received', extra); 
    msg.data('type', 'TASK'); 
    BYOI.addMessageToContainer(msg, $('#currentTask'));
    
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
            var bcastSent = message.hasClass('broadcast');
            var bcastRec = message.hasClass('received')  && message.data('to') == '0';
            if (bcastSent || bcastRec){
                $('#menu-broadcast').removeClass('hidden');
                $('#menu-broadcast').addClass('unread');
                $('.nav li.active').removeClass('unread');
                return true;
            }
            return false; 
        }, 
        onError:function(msg){console.log(msg);}
    });

    function acceptMessage(msg, tab) {
        if(msg.data('type') != 'PACKET' && msg.data('type') != 'MESSAGE'){
            return false;
        }

        var menu = $('#menu-node-' + tab);
        var tabFor = menu.attr('neighbour');

        if(msg.hasClass('received')){ // incoming message
            var from = msg.data('from');

            // For unassigned tab and an unseen neighbour set up headings.
            if (tabFor == undefined  && neighbours.indexOf(from) < 0) {
                menu.attr('neighbour', from);
                neighbours.push(from);
                tabFor = from;

                menu.removeClass('hidden');
                menu.attr('send-to', from);
                $('#a-node-' + tab).html('To/From Node '+ from);
            }
            if (from == tabFor) {
                return true;
            }
        }

        if (msg.hasClass('sent')) {
            if (msg.data('to') == tabFor){ 
                $(menu.addClass('unread'));
                return true;
            }
        }
        return false;
    }
    // create a Message Handler for all the messages from node 1
    $('#node-1').BYOIMessageHandler({
        accept:function(message){
            return acceptMessage(message, 1);
        }, 
        onError:function(msg){console.log(msg);}
    });

    // create a Message Handler for all the messages node 2
    $('#node-2').BYOIMessageHandler({
        accept:function(message){ 
            return acceptMessage(message, 2);
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
