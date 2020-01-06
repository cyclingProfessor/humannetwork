# BYOI jQuery plugin

## Introduction
This is a jQuery plugin that provides an API to handle a connection to the BYOI server.

The API allows the programmer to establish one connection to the server and provides a set of utilities to handle the sent and received messages.

## Elements
The BYOI API defines the following objects to interact with:
* Connection: for the websocket connection to the server
* BYOI System Alert: allows the API to send alerts to the DOM
* BYOI Message: wrapper of the messages that can be sent through the websocket.
* BYOI Message Handler: custom container for BYOI messages that provides several methods for message manipulation.

## BYOI Connection

The library provides a global BYOI object to handle the connection to the server via websocket. Some helpers are also added to this object and are available to the developer.

### Configuration
```javascript
BYOI.config({
    // Configuration variables
    host : 'localhost', // defaults to 127.0.0.1
    port : 10000, // defaults to 10000
    MSG_MAX_LEN : 40, // defaults to 40

    // Configuration hooks
    // called when any message from the server is received
    onMessageReceived: function(type, msg){
        // The message type can be one of the following types:
        // CONNECTED, PACKET or TASK
    }, 
    
    // called when the connection to the server fails
    onConnectionError: function(){},
    
    // called when the connection to the server is closed,
    // as they may be different reasons that can cause this,
    // they are passed to this function as parameters
    onConnectionClose: function(code, reason){
        // The codes are specified by
        // http://tools.ietf.org/html/rfc6455#section-7.4.1
    },
    
    // called after the current client sends a message to the
    // server
    onSend: function(msg){},
});
```

### Connect
* `BYOI.connect(configuration)`: attempts to establish a connection with the server. If passed, the configuration will override the host/port defined by the `BYOI.config`.

A possible way to use this function is obtaining the host/port from the HTML

```html

<div id="connectionFields">
    <span>host</span>
    <input id="host" type="text"/>
    <span>port</span>
    <input id="port" type="text"/>
</div>
```

```javascript
BYOI.connect({host: $('#host').val(), port: $('#port').val()});
```

## BYOI System Alert

This type of element will receive all the alerts sent by the API. You can have as many system alert receivers as you want.

To establish a system alert element you can:

```html
<div id="systemAlerts"></div>
```

```javascript
$('#systemAlerts').BYOISystemAlert({
    //this is the default behaviour if the onAlert property is not provided
    onAlert:function(alert){ 
        $('#systemAlerts').html(alert); 
    }
});
```

If no BYOI System Alert is defined, the alerts will default to being javascript alerts.
    
## BYOI Message
The BYOI Message is the basic unit used in the API. The API will only send/receive BYOI Messages to/from the BYOI server.
Any DOM element can be converted to a BYOI Message, but it is expected to contain an element with `class="text"` as a child element for a correct interaction with the API.
The text element is parsed to add appropriate data elements and classes.

For instance, we can convert

```html
<div id="myMsg"><span class="text">Hello World</span></div>
```

to a BYOI Message by

```javascript
$('#myMsg').BYOIMessage();
```

In its data element, a BYOI Message will contain:
* a `text` property with the contents of the element with `class="text"`.
* an `ID` property with a integer value identifying the message.
* any other property passed to the `BYOIMessage` method, i.e.:
    ```javascript
    $('#myMsg').BYOIMessage({
        prop_1:value_1, 
        prop_2:value_2, 
        ..., 
        prop_n:value_n 
    });
    ```
### Structure
The text element has the form: n/m:Hdr!!payload[R]#nn
where all parts except the payload are optional, but must occur in the order shown if present.
n/m: is required if the payload is a fragment.  A fragment has the two data elements: 'fragNum', 'fragOutOf'
Hdr!! may be a header element that is copied to all fragments, and placed once when combining.  If there is a header it is indicated by the data element: 'hdr'
[R] is a random number that can be used as a nonce in encryption. It is the value of the data element: 'rnd'
#nn is a possible hashnum.  Only unfragmented messages can have hashes, so only the final fragment can retain a hash. Data: 'hash'

Only complete messages can be encrypted or hashed or nonced.  The hdr is not encrypted.

See ### Methods.
The two sources of BYOIMessages are those received from the server, and those built by the user.

### Types
Many methods of the API create BYOI Messages and sends them to every BYOI Message Handler. Depending on how each message is created, the API tags them with the following CSS classes:
* `class="sent"`: the BYOI Connection creates this type of message after sending a message to a specific node.
* `class="broadcast"`: the BYOI Connection creates this type of message after sending a broadcast message.
* `class="fragment"`: messages that appear to be created by the `splitMessages` method.
* `class="combined"`: messages that appear to be created by the `combineMessages` method. 
* `class="checksum"`: messages that appear to be created by the `addChecksum` method. 
* `class="encrypted"`: messages created by the `encryptMessage` method. 
* `class="decrypted"`: messages created by the `decryptMessage` method. 
* `class="random"`: messages that appear to be created by the `addRandomNumber` method. 


### Methods
A BYOI Message has the following methods:
* `send(recipient)`: sends a message to the specified node.
* `relayMessage()`: sends message to all the Message Handlers.
* `addMetadata(options)`: adds `options` to the BYOI Message `data`.
* `toggleSelectMessage()`: toggles whether the message is selected.
* `addChecksum()`: creates a copy of the BYOI Message with a checksum appended.
   Can only be applied to complete (unfragmented) messages.
* `verifyChecksum()`: returns whether the checksum is valid and sends the appropriate System Alert.
* `encryptMessage(key)`: creates a copy of the BYOI Message with the encrypted text.
   Can only be applied to complete (unfragmented) messages.
* `decryptMessage(key)`: creates a copy of the BYOI Message with the decrypted text.
* `addRandomNumber()`: creates a copy of the BYOI Message with a random number appended.
   Can only be applied to complete (unfragmented) messages.

This methods can be used within a jQuery chain, i.e.:
```javascript
$('#myMsg').BYOIMessage().addChecksum();
```

## BYOI Message Handler
The BYOI Message Handler is a special container for BYOI Messages. Although is not mandatory to use it, it provides a toolkit to filter and manage several messages with ease. As with BYOI Messages, any DOM element can be converted to BYOI Message Handler. It might be a good idea for the element to be empty before converting it.

For instance, we can convert
```html
<div id="myMsgHandler"></div>
```
to a BYOI Message Handler by
```javascript
$('#myMsgHandler').BYOIMessageHandler();
```
The BYOI Message Handler constructor provides the following hooks, which can be overridden:
* `accept`: the accept method is a configurable way to define a method that implements rules for the message handler to filter messages, the method is a boolean method. Returning true means that the message is accepted, and therefore added to the message handler
* `onError`: this method is called when an element that is not a message is added to the messageHandler. The API assumes that all the elements inside the message handlers are messages, but if you know what you're doing, you can override this behaviour.
    
    ```javascript
    $('#myMsgHandler').BYOIMessageHandler({
        //this is the default implementation
        accept:function(msg){ return true; },  
        onError:function(msg){ BYOI.systemMessage(msg); } 
    });
    ```

### Methods
A BYOI Message Handler has the following methods:
* `getMessage(index)`: returns the indexth message contained in this message handler.
* `getMessageByID(id)`: get the message contained in this handler with the provided `id`.
* `getAllMessages()`: get all messages contained in this message handler.
* `getSelectedMessages()`: get all selected messages contained in this message handler.
* `addMessage(message, afterTarget)`: adds a message to the handler. The `afterTarget` parameter is an optional parameter that will perform an `.insertAfter` call to the specified message, in most cases, this will be a BYOI Message within the same Message Handler.
* `splitMessages()`: splits the selected messages in this message handler. The new fragments are added to this message handler.
* `combineMessages()`: combine the selected fragments in this message handler. The new message is added to this message handler.

This methods can be used within a jQuery chain, i.e.:
```javascript
var m = $('#myMsg').BYOIMessage();
$('#myMsgHandler').BYOIMessageHandler().addMessage(m);
```
## Dependencies
* jQuery
* md5.js

## BYOI Helper Methods
This methods are part of the class, but the user is not usually expected to interact with them. 
Only use them if you know what you're doing.

* `BYOI.systemMessage(message)`:relays the message to all system messages elements.
* `BYOI.addMessageToContainer(msg, container)`:adds a BYOI Message to the specified container. This function is able to distinguish between BYOI-messageHandlers and other DOM elements.
