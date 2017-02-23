'use strict';
var BYOI = {};

var GetURLParameter = function (sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
};

function deleteAllCookies() {
    var cookies = document.cookie.split(";");

    for (var i = 0; i < cookies.length; i++) {
    	var cookie = cookies[i];
    	var eqPos = cookie.indexOf("=");
    	var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
    	document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}

function setCookie(cname, cvalue) {
    var d = new Date();
    d.setTime(d.getTime() + (24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + encodeURIComponent(cvalue) + "; " + expires;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length,c.length);
    }
    return 0;
}

//split text in several chunks of length len
//and return them in reversed order 
function chunker(text, len){
    var chunks = [];
    while(text.length > len){
        chunks.push(text.substr(0,len))
        text = text.substr(len);   
    }
    chunks.push(text);
    return chunks;
}

(function($){
/******************************************************************************
                    BYOI API
******************************************************************************/
     // this is the starting point of the API, all configurations and 
    // hooks will be overwritten here
    BYOI.config = function(configuration){
        //host
        if(typeof configuration.host != 'undefined'){
            BYOI.host = configuration.host;
        } else {
            BYOI.host = '127.0.0.1';
        }
        //port
        if(typeof configuration.port != 'undefined'){
            BYOI.port = configuration.port;
        } else {
            BYOI.port = 10000;
        }

        //maximum length allowed for the message
        if(typeof configuration.MSG_MAX_LEN != 'undefined'){
            BYOI.MSG_MAX_LEN = configuration.MSG_MAX_LEN;
        } else {
            BYOI.MSG_MAX_LEN = 40;
        }

        //minimum length left after HEADER (up to $$) when fragmenting
        if(typeof configuration.MSG_MAX_LEN != 'undefined'){
            BYOI.MSG_MIN_FRAGMENT = configuration.MSG_MIN_FRAGMENT;
        } else {
            BYOI.MSG_MIN_FRAGMENT = 20;
        }

        //message received hook
        if(typeof configuration.onMessageReceived != 'undefined'){
            BYOI.onMessageReceived = configuration.onMessageReceived;
        } else {
            BYOI.onMessageReceived = function(){};
        }

        //connection error hook
        if(typeof configuration.onConnectionError != 'undefined'){
            BYOI.onConnectionError = configuration.onConnectionError;
        } else {
            BYOI.onConnectionError = function(){
                BYOI.systemMessage("Connection Error: Is the server running?");
            };
        }

        //connection closed hook
        if(typeof configuration.onConnectionClose != 'undefined'){
            BYOI.onConnectionClose = configuration.onConnectionClose;
        } else {
            BYOI.onConnectionClose = function(code, reason){
                BYOI.systemMessage('The connection was closed for reason ' + code + ': ' + reason );
            };
        }

        //connection restored hook
        if(typeof configuration.onConnectionRestored != 'undefined'){
            BYOI.onConnectionRestored = configuration.onConnectionRestored;
        } else {
            BYOI.onConnectionRestored = function(){
                //BYOI.systemMessage('The game has been restored');
            };
        }

        //on message sent hook
        if(typeof configuration.onSend != 'undefined'){
            BYOI.onSend = configuration.onSend;
        } else {
            BYOI.onSend = function(msg){};
        }
         // used to assing a unique id to every message
        BYOI.currID = 0;
        return BYOI;
    };
    
    // using the provided configuration, attempt a connection to the BYOI 
    // websocket server 
    BYOI.connect = function(connectionData){
        // if the programmer wants to bypass the start method, he might 
        // provide the host:port combination directly 
        if(typeof connectionData != 'undefined'){
            BYOI.host = connectionData.host;
            BYOI.port = connectionData.port;
        }
        
        // create the websocket
        BYOI.connection = new WebSocket('ws://'+BYOI.host+':'+BYOI.port+'/');

        // send the HELLO message to the server 
        // if we have the cookie, we should receive or own information back
        // so that we resume a previous session
        BYOI.connection.onopen = function (e) {
            var message = {
                session: Math.abs(+getCookie("session")),
                node: +getCookie("node"),
                type: "HELLO"
            };
            BYOI.connection.send(JSON.stringify(message));
        };
        
        // handle messages received from the websocket server
        BYOI.connection.onmessage = function (e) {
            var received = JSON.parse(e.data);
            var type = received.type; // Could be instructions, broadcast, connected or message
            var html;
            var metaData = {};
            if (type == 'CONNECTED') {
                BYOI.hasStarted = false;
                BYOI.mySession = received.session;
                console.log('received', received);
                // if the session number is the same as the cookie we already 
                // have, we can reconnect to the same session

                var noCookie = GetURLParameter('noCookie');
                console.log('noCookie', noCookie);
                noCookie = (noCookie != undefined);
                if (!noCookie && getCookie("session") == BYOI.mySession) {
                    BYOI.myName = decodeURI(getCookie("name"));
                    BYOI.myNode = decodeURI(getCookie("node"));
                    BYOI.onConnectionRestored();
                } else {
                    // if our cookie does not match the information 
                    // sent by the server, we're joining a new session
                    BYOI.myName = received.name;
                    BYOI.myNode = received.node;
                    setCookie("session", BYOI.mySession);
                    setCookie("node", BYOI.myNode);
                    setCookie("name", BYOI.myName);

                    //BYOI.systemMessage("new game join");
                }
                html = '<div class="received"><span class="connected">Node Number:  '+BYOI.myNode+'</span> | Node name: <span class="text">'+BYOI.myName+'</span></div>';
                metaData = {
                    'node': BYOI.myNode, 
                    'text': BYOI.myName,
                    'session': BYOI.mySession
                };

            }
            if (type == 'PACKET') {
                var text = received.text;
                var from = received.from;
                var to = received.to;
                html = '<div class="received"><span class="node"> '+from+'</span> : <span class="text">'+text+'</span></div>';
                metaData['from'] = from;
                metaData['to'] = to;
            }
            if (type == 'TASK') {
                // The first task message enables the send function.
                BYOI.hasStarted = true;
                var task = received.task;
                html = '<div class="task">Task: <span class="text"> ' +task+ '</span></div>';
                metaData = {
                    'text':task
                };
            }
            metaData['type'] = type;
            metaData['received'] = received;
            // create a new message from the received data
            var msg = $(html).BYOIMessage(metaData)
                .relayMessage(); // relay the message to all message handlers
            
            // call the hook
            BYOI.onMessageReceived(type, msg);
        };

        BYOI.connection.onerror = function (e) {
            BYOI.onConnectionError();
        };

        BYOI.connection.onclose = function (event) {
            var reason;
            // See http://tools.ietf.org/html/rfc6455#section-7.4.1
            if (event.code == 1000)
                reason = "Normal closure, meaning that the purpose for which the connection was established has been fulfilled.";
            else if(event.code == 1001)
                reason = "An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.";
            else if(event.code == 1002)
                reason = "An endpoint is terminating the connection due to a protocol error";
            else if(event.code == 1003)
                reason = "An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).";
            else if(event.code == 1004)
                reason = "Reserved. The specific meaning might be defined in the future.";
            else if(event.code == 1005)
                reason = "No status code was actually present.";
            else if(event.code == 1006)
               reason = "The connection was closed abnormally, e.g., without sending or receiving a Close control frame";
            else if(event.code == 1007)
                reason = "An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [http://tools.ietf.org/html/rfc3629] data within a text message).";
            else if(event.code == 1008)
                reason = "An endpoint is terminating the connection because it has received a message that \"violates its policy\". This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.";
            else if(event.code == 1009)
               reason = "An endpoint is terminating the connection because it has received a message that is too big for it to process.";
            else if(event.code == 1010) // Note that this status code is not used by the server, because it can fail the WebSocket handshake instead.
                reason = "An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn't return them in the response message of the WebSocket handshake. <br /> Specifically, the extensions that are needed are: " + event.reason;
            else if(event.code == 1011)
                reason = "A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.";
            else if(event.code == 1015)
                reason = "The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can't be verified).";
            else
                reason = "Unknown reason";

            BYOI.onConnectionClose(event.code, reason);
        };

    };//BYOI.connect


    // relay the message to all system messages elements, 
    // if none exist, alert
    BYOI.systemMessage = function(message){
        // select all elements in the DOM market as a system message
        // receiver
        var systemMsg = $('.BYOI-systemAlert');
        // if no system message receive is found, alert with a
        // browser pop-up
        if(systemMsg.length == 0){
            alert(message);
        }else{
            systemMsg.each(function(){
                $(this).data('onAlert')(message);
            });
        }
    };

    // adds a BYOI Message to the specified container. This function is 
    // able to distinguish between BYOI-messageHandlers and other DOM elements
    BYOI.addMessageToContainer = function(msg, container){
        // if no container is defined and the message is inside a message
        // handler, append the new message to the container 
        if(typeof container == 'undefined'){
            this.parent().addMessage(msg);
        } else if(container.hasClass('BYOI-messageHandler')){
            // if the specified container is a message handler, create
            // a new message and add it to the specified message handler
            container.addMessage(msg);
        } else {
            // if the specified container is not a message handler, 
            // set the value to the element to text:hash
            container.setVal(msg.data('text'));
        }
    };

/******************************************************************************
                    BYOI methods attached to jQuery
******************************************************************************/
    
/*=============================================================================
                    System message 
=============================================================================*/

    // marks an element to become a receiver for BYOI.systemMessage messages
    $.fn.BYOISystemAlert = function(options){
        if(typeof options == 'undefined'){
            options = {};
        } 
        return this.each(function(){
            var sm = $(this);
            if(options.hasOwnProperty('onAlert')){
                sm.data('onAlert',options.onAlert);
            } else {
                sm.data('onAlert',function(text){ sm.html(text); });
            }
            sm.addClass('BYOI-systemAlert');
        });
    };

/*=============================================================================
                    Message Handler
=============================================================================*/
    // marks an element to receive messages and provides the 
    // neccesary functionality for message handling
    $.fn.BYOIMessageHandler = function(options){
        if(typeof options == 'undefined'){
            options = {};
        }   
        
        return this.each(function(){
            var mh = $(this);
            mh.addClass('BYOI-messageHandler');

            // the accept method is a configurable way to define 
            // a method that implements rules for the message
            // handler to filter messages, the method is a boolean method.
            // Returning true means that the message is accepted, and 
            // therefore added to the message handler
            if(options.hasOwnProperty('accept')){
                mh.data('accept',options.accept);
            } else {
                mh.data('accept',function(message){ return true; });
            }
            
            // this method is called when an element that is not a
            // message is added to the messageHandler. The API assumes
            // that all the elements inside the message handlers are 
            // messages, but if you know what you're doing, you can 
            // override this behaviour.
            if(options.hasOwnProperty('onError')){
                mh.data('onError',options.onError);
            } else {
                mh.data('onError',function(msg){ BYOI.systemMessage(msg); });
            }
        });
    };

    // return the indexth message contained in this message handler
    $.fn.getMessage = function(index){
        if(this.hasClass('BYOI-messageHandler')){
            return this.find('.BYOI-message').eq(index);
        } else {
            BYOI.systemMessage('ERROR: getMessage should be called only on a message handler');
        }

    }

    // get the message contained in this handler with data('ID') == id
    $.fn.getMessageByID = function(id){
        if(this.hasClass('BYOI-messageHandler')){
            return this.find('.BYOI-message').filter(function(){
                return $(this).data('ID') == id;
            });
        } else {
            BYOI.systemMessage('ERROR: getMessageByID should be called only on a message handler');
        }
    };

    // get all messages contained in this message handler
    $.fn.getAllMessages = function(){
        if(this.hasClass('BYOI-messageHandler')){
            return this.find('.BYOI-message');        
        } else {
            BYOI.systemMessage('ERROR: getAllMessages should be called only on a message handler');
        }
    };

    // get all selected messages contained in this message handler
    $.fn.getSelectedMessages = function(){
        if(this.hasClass('BYOI-messageHandler')){
            return this.find('.BYOI-message.BYOI-selected');        
        } else {
            BYOI.systemMessage('ERROR: getSelectedMessages should be called only on a message handler');
        }
    };

    // adds a message to the handler
    $.fn.addMessage = function(message, afterTarget){
        return this.each(function(){
            var mh = $(this);
            if(mh.hasClass('BYOI-messageHandler')){
                if(!message.hasClass('BYOI-message')){
                    mh.data('onError')('ERROR: attempt to add something other than a message to a message handler.');
                    return true;
                }
                // call the accept method to decide on whether add the 
                // message to the handler or not
                if(mh.data('accept')(message)){
                    //add metadata to message
                    if(typeof afterTarget == 'undefined'){
                        mh.append(message.clone(true));
                    } else {
                        message.insertAfter(afterTarget);
                    }
                }
            }
        });
    };

    // combine the selected fragments in this message handler
    $.fn.combineMessages = function(){
        // collection is a selection of messages
        if(this.hasClass('BYOI-messageHandler')){
            var content = '';
            var nextValidSeq = 1;
            var childrenMeta;
            var header = '';
            var offset = 4;
            var error = false;
            
            this.find('.BYOI-message.BYOI-selected.fragment')
                .sort(function(a, b){
                    return $(a).data('fragNum') > $(b).data('fragNum');
                })
                .each(function(){
                    var frag = $(this);
                    if (offset == 4 && frag.data('hdr') != undefined) {
                        header = frag.data('hdr') + "!!";
                        offset = header.length + 4;
                    }
                    var seq = parseInt(frag.data('fragNum'));
                    childrenMeta = $(this).data();
                    if(seq == nextValidSeq){
                        nextValidSeq++;
                        content += frag.data('text').substr(offset);
                    }else{
                        $(this).notify('ERROR: fragments must have consecutive sequence numbers.');
                        error = true;
                        return false;
                    }
                });
            if (!error) {
                if (nextValidSeq != childrenMeta.fragOutOf + 1) {
                    $(this).notify('ERROR: Missing final fragment(s).');
                    return this;
                }

                delete childrenMeta.seq;
                content = header + content;
                childrenMeta.text = content;
                this.addMessage(
                    $('<div class="combined"><span class="text">' + content + "</span></div>")
                         .BYOIMessage(childrenMeta)
                );
                return this;
            }
        } else {
            BYOI.systemMessage('ERROR: called combineMessages on a non-messageHandler element.');
        }
    };
/*=============================================================================
                    Message 
=============================================================================*/
    // Marks an element as a BYOI message
    $.fn.BYOIMessage = function(options){
        if(typeof options == 'undefined')
            options = {};
        return this.each(function(){
            var m = $(this);
            // Parse the text classes.
            var t = m.find('.text');
            if(t){
                // first check whether this is a fragment
                var str = t.text();
                var offset = 0;
                if (str.charAt(3) == ':') {
                    if(!m.hasOwnProperty('fragNum')) options['fragNum'] = Number(str.charAt(0));
                    if(!m.hasOwnProperty('fragOutOf')) options['fragOutOf'] = Number(str.charAt(2));
                    m.addClass('fragment');
                    offset = 4;
                }
                var endHeader = str.indexOf("!!", offset);
                if (endHeader > 0) {
                    if(!m.hasOwnProperty('hdr')) options['hdr'] = str.substring(offset, endHeader);
                    offset = endHeader + 2;
                }
                var nonce = str.match(/\[\d\d*\]/);
                if (nonce != null) {
                    if(!m.hasOwnProperty('rnd')) options['rnd'] = Number(nonce.match(/\d*/));
                    m.addClass('random');
                }
                var checksum = str.match(/#\d\d*$/);
                if (checksum != null) {
                    if(!m.hasOwnProperty('hash')) options['hash'] = Number(checksum.substr(1));
                    m.addClass('checksum');
                    index = str.substr(checksum);
                    if(!m.hasOwnProperty('text-no-hash')) options['text-no-hash'] = str.substr(0,checksum);
                }
                if(!m.hasOwnProperty('text')) options['text'] = t.html();
            }
            // mark the message
            m.addClass('BYOI-message');
            // add all the meta-data to the data element
            m.addMetadata(options);
            // generate an ID for the message
            m.data('ID', BYOI.currID++);
        });
    };

    // if valid, sends the message to the server 
    // and call the onSend hook
    $.fn.send = function(recipient){
        if(this.hasClass('BYOI-message') && BYOI.hasStarted){
            console.log(recipient);
            // if no recipient is provided, broadcast
            if(typeof recipient == 'undefined')
                recipient = 0;
            recipient = parseInt(recipient);
            this.each(function(){
                var msg = $(this);
                // create a long string containing a concatenation of all
                // elements with class 'text' contained in the message
                var content = '';
                msg.find('.text').each(function(){
                    content += $(this).html();
                });

                // validate the length of the message
                if(content.length <= BYOI.MSG_MAX_LEN){
                    // create an object to send to the server
                    var message = {
                        text: content,
                        from: +BYOI.myNode,
                        to: recipient,
                        type: 'MESSAGE',
                        date: Date.now()
                    };
                    // send message to server
                    BYOI.connection.send(JSON.stringify(message));

                    //modify html to relay to message handlers
                    var html;
                    if(message.to == 0){ // broadcast message
                        html = '<div class="broadcast">Broadcast Message: <span class="text">'+message.text+'</span></div>';
                        msg.addClass('broadcast');
                    } else { // regular message
                        html = '<div class="sent">Sent To:<span class="node">'+message.to+'</span> Message: <span class="text">'+message.text+'</span></div>';
                        msg.addClass('sent');
                    }
                    msg.html(html)// modify content
                        .addMetadata(message)// add metadata
                        .relayMessage();// relay
                    // call hook
                    BYOI.onSend(msg);
                } else {
                    // BYOI.systemMessage('ERROR: message too long to send.');
                    $("#msg").notify("ERROR: message too long to send.\n Please split first.");
                }
            });
            //return the relayed messages to allow for jQuery chaining
            return $('.BYOI-messageHandler').getMessage($(this).data('ID'));
        } else if(!BYOI.hasStarted){
            $("#msg").notify("WARNING: cannot send message before network is up.");
        } else {
            BYOI.systemMessage('ERROR: called send on a non-message element.');
        }
    };

    //relay this message to every messageHandler
    $.fn.relayMessage = function(){
        if(this.hasClass('BYOI-message')){
            var mh;
            var message = this;
            this.each(function(){
                // get all the message handlers
                mh = $('.BYOI-messageHandler');
                // if there are no message handlers, inform the user
                if(mh.length == 0){
                    BYOI.systemMessage('ERROR: No message handler found, received' + message.data('text'));
                }else{
                    // add the message to this message handler
                    mh.addMessage($(this));
                }
            });
            // if the message was successfully relayed, return the message
            // to allow for jQuery chaining
            if(mh)
                return mh.getMessage($(this).data('ID'));
        } else {
            if ($(this).length >0 ){
                BYOI.systemMessage('ERROR: called relayMessage on a non-message element.');
            }
        }
    };

    // given a javascript object, copy all the properties to the 
    // data element of a message
    $.fn.addMetadata = function(options){
        if(this.hasClass('BYOI-message')){
            if(typeof options == 'undefined')
                options = {};
            return this.each(function(){
                var m = $(this);
                // add the meta-data to the object
                for (var property in options) {
                    // only add user-defined meta-data
                    if (options.hasOwnProperty(property)) {
                        m.data(property.toString(), options[property]);
                    }
                }
            });
        } else {
            BYOI.systemMessage('ERROR: called addMetadata on a non-message element.');
        }
    };


    // splits the selected messages in this message handler
    $.fn.splitMessage = function(){
        var msgList = $('<div></div>');
        // collection is a selection of messages
        this.each(function(){
            if($(this).hasClass('BYOI-message')){
                var nextSeq = 1;
                var msg = $(this);
                //var prevChunk = msg;

                if(msg.data('text').length <= BYOI.MSG_MAX_LEN){
                    $(".split-btn").notify("Message too short to split!", { position:"right" });
                    return true;
                }
                if(msg.data('fragNum')){
                    $(".split-btn").notify("Cannot split a fragment!", { position:"right" });
                    return true;
                }
                var header = '';
                var chunks; // List of fragments
                var offset = 0;
                var fragmentSize = BYOI.MSG_MAX_LEN - 4

                // Identify where the (possible) message header ends
                var endHeader = msg.data('text').indexOf("!!");
                if (endHeader > 0) {
                    header = msg.data('hdr') + "!!";
                    // Make sure that we have some characters in each message fragment!
                    if (BYOI.MSG_MAX_LEN <= 4 + endHeader + BYOI.MSG_MIN_FRAGMENT) {
                        $(".split-btn").notify("Message Header too long for split!", { position:"right" });
                        return true;
                    }
                    fragmentSize = fragmentSize - endHeader;
                    offset = endHeader + 2;
                }
                var chunks = chunker(msg.data('text').substring(offset), fragmentSize);
                if (chunks.length > 9){
                    $(".split-btn").notify("Message requires too many fragements (>9) for split!", { position:"right" });
                    return true;
                }

                var parentMeta = msg.data();
                var outOf = chunks.length.toString();
                for(var i=0; i < chunks.length; i++){
                    var seq = (i+1).toString();
                    var text = seq + '/' + outOf + ':' + header + chunks[i];
                    parentMeta.text = text;
                    var html = '<div><span class="text">'+text+'</span></div>';
                    var chunk = $(html).BYOIMessage(parentMeta);
                    msgList.append(chunk);
                }
            } else {
                BYOI.systemMessage('ERROR: called splitMessages on a non-messageHandler element.');
            }
        });
        return msgList.children();
    };

    // select/unselect messages, also keeps track of the last selection on 
    // each message handler
    $.fn.toggleSelectMessage = function(){
        return this.each(function(){
            var msg = $(this);
            if(msg.hasClass('BYOI-message')){
                // if the message was selected, unselect
                if(msg.hasClass('BYOI-selected')){
                    msg.removeClass('BYOI-selected');           
                    msg.removeClass('BYOI-last-selected');           
                }else{
                    // if the message was not selected, select and mark
                    // as the last selection on the current message handler
                    msg.addClass('BYOI-selected');           
                    msg.parent().find('.BYOI-last-selected').removeClass('BYOI-last-selected');
                    msg.parent().data('last-selected', msg);
                    msg.addClass('BYOI-last-selected');           
                }
            }
        });
    };

    // creates and returns a new message with a md5 checksum appended
    // to the text of this message
    $.fn.addChecksum = function(){
        if(this.hasClass('BYOI-message')){
            // create the digest
            var hash = md5(this.data('text'));
            // append the hash to the text
            var text = this.data('text') + "#" + hash;

            var msg = $('<div class="checksum"><span class="text">'+text+'</span></div>')
                .BYOIMessage(this.data())
                .addMetadata({text: text});
            return msg;
        } else {
            BYOI.systemMessage('ERROR: called addChecksum on a non-message element.');
        }
    };

    // returns true or false depending on the validation of the md5 hash
    $.fn.verifyChecksum = function(){
        if(this.hasClass('BYOI-message')){
            var result = false; // assume the validation is going to fail
            // if the message contains a hash, validate
            if(this.data('hash') != undefined){   
                // validate
                result = md5(this.data('text-no-hash')) == this.data('hash');
                // let the user know about the validation
                var message = result? 'Correct!':'Incorrect!';
                //BYOI.systemMessage('CHECKSUM:' + message);
                $('.verify-checksum-btn').notify('checksum '+message, "success");
            } else {
                $(".verify-checksum-btn").notify("Message does not contain a checksum!", { position:"right" });

                //BYOI.systemMessage('ERROR: the message does not contain a checksum');
            }
            return result;
        } else {
            BYOI.systemMessage('ERROR: called addMetadata on a non-message element.');
        }
    }

    // encrypts the message using the provided key
    // which defaults to your node number
    // and returns the encrypted message
    $.fn.encryptMessage = function(key){
        if(this.hasClass('BYOI-message')){
            // if a key is provided, use, otherwise, get the value from the 
            // recipient field, if that field is empty, defaults to node number
            key = typeof key == 'number' && key != 0? key : parseInt(BYOI.myNode);
            var msg = $(this);
            // get the text to encrypt
            var val = msg.data('text');

            // encrypt with a super secure method ;)
            var encryptedString = '';
            for(var i=0; i < val.length; i++){
                var s = '000' + (val.charCodeAt(i) + key % 100 ).toString();
                encryptedString += s.substring(s.length - 3);
            }

            // create a new message with the enrypted string
            var html = '<div class="encrypted"><span class="text">';
            html += encryptedString;
            html += '</span></div>';
            var enc = $(html).BYOIMessage(this.data())
                .addMetadata({text:encryptedString});
            return enc;
        } else {
            BYOI.systemMessage('error: called encryptmessage on a non-message element.');
        }
    };


    // decrypts the message using the provided key
    // and returns the decrypted message
    $.fn.decryptMessage = function(key){
        if(this.hasClass('BYOI-message')){
            //if a key is provided, use, otherwise, get the value from the 
            //recipient field, if that field is empty, defaults to node number
            key = typeof key == 'number' && key != 0? key : parseInt(BYOI.myNode);
            var msg = $(this);
            // get the encrypted string
            var encryptedString = msg.data('text');
            var s = ''; // decrypted string

            // proceed to decrypt using the provided key
            while(encryptedString.length > 0){
                var chunk = parseInt(encryptedString.substr(0,3));
                encryptedString = encryptedString.substr(3);
                chunk -= key % 100;
                s += String.fromCharCode(chunk);
            }
            
            // create a new message with the decrypted version of the 
            // text
            var html = '<div class="decrypted"><span class="text">';
            html += s;
            html += '</span></div>';
            var dec = $(html).BYOIMessage(this.data()).addMetadata({text:s});
            return dec;
        } else {
            BYOI.systemMessage('ERROR: called addMetadata on a non-message element.');
        }
    };

    //append a random number to the message box
    $.fn.addRandomNumber = function(){
        if(this.hasClass('BYOI-message')){
            var msg = $(this);
            // get the text from the message and append a random 4 digit 
            // number to it
            var text = msg.data('text') + '|' + Math.floor(Math.random() * 10000);

            // create a new message with the nonce appended
            var html = '<div class="random"><span class="text">';
            html += text;
            html += '</span></div>';
            var random = $(html).BYOIMessage(this.data())
                .addMetadata({text:text});
            return random;
        } else {
            BYOI.systemMessage('ERROR: called addRandomNumber on a non-message element.');
        }
    };


    //http://stackoverflow.com/questions/10592537
    $.fn.setVal = function(value) {

        return this.each(function() {

            if ($.inArray( this.tagName.toLowerCase(), ['input', 'textarea', 'select'] ) != -1) {
                $(this).val( value );
            } else {
                $(this).text( value );
            }       
        });
    };

}( jQuery ));
