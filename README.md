humannetwork
============

A network simulator for a STEM challenge activity.

Can be used in a lecture to introduce concepts of the Internet since it now relies only on a client web browser that supports WebSocket.

It is made of two parts: a server which controls the topology and quality of the network, and a client that pupils can use to receive, build and send messages to each other.

Usage
-----
Compilation can be done by going in the Server directory and typing:

> ant jar

This creates an executable jar, HumanNetworkServer.jar, in the "jar" directory.

The jar file is self contained and can be run anywhere.  The server can be started with:

> java -jar HumanNetworkServer.jar

The server has several optional arguments which are parsed in a standard way:
   "?", "Print a help message"
   "h", Host adapter to listen on.  Can be a host name or an IP address. Defaults to "127.0.0.1"
   "p", Port number to listen on.  Defaults to 10000
   "s", Session number used to identify clients so that a server can be restarted (not yet implemented)

There is a file of messages that the Server provides as challenges to clients.  This file is included in the jar file, but can be overridden with more appropriate or even themed files, by placing a file called "proverbs.txt" in the same directory as the HumanNetworkServer.jar file.  The format is plain UTF_8 text, one message per line.

The client is a standard (adaptive) web page application that should be hosted on a reachable web server.  Clients all use the Javascript API in Client/BYOI.  As such the Client subtree can be copied into your web server tree.

A good client has been constructed using this API which is loaded from Client/Retro/index.html

The Retro client supports the 'ip' and 'port' flags in the client URL with their obvious meanings.

To allow multiple clients on the same web browser you can use the 'nocookie' flag in the client URL.  Otherwise each new client is seen as a reconnect of the others.  On the other hand this cookie mechanism provides graceful reconnect if clients should disconnect for any reason.

Testing
-------
A headless test suite of the server and Retro client using PhantomJS is executed from the Runner.js script in Client/Test
This suite is configurable, but at least you must specify the path tot he server and the URL from which to load the Retro client.

The server has standard Junit tests.

Code Organisation
-----------------
The Server class is the entry point for the server. Its state is represented by the classes ConnectionList, LinkList and MessageList. Its logic is defined in Route, using DelayedMessages. Its GUI is in ServerGui and ServerController.

The client consists of two parts: a web page and an API.
The API is generic and provides hooks for connecting to the server and processing messages.  it is well documented and is contained in Client/BYOI.
There are several example clients provided.  The simple APIexample shows how the API can be used, while the NoAPIexample is an undecorated JQuery page that exercises the server.
The Retro client is a full working user friendly example.

The client API uses cookies to store connection information so that clients can be restarted after a connection failure.  This means that only one client can be run per web browser session.


Protocol
--------
All messages are encoded in JSON

1. Client to Server: HELLO
When a WebSocket connection is established the client sends a HELLO message
{session: INT, node: INT, type: "HELLO"}
Existing clients whose web page has been mysteriously closed can use the HELLO message to re-establish a link to an existing server by sending a non-zero session number.
New clients will only be accepted before the first TASK has been sent.

2. Server to Client: CONNECTED
The first response from the server will be a CONNECTED message
{session: INT, name: STRING, node: INT, type: "CONNECTED"}
If the session matches the session id sent in the HELLO then the server assumes that the client is re-establishing a link and uses the client's existing data rather than generating new data (name etc.,)
New clients trying to connect after the first task has begun will be silently ignored.

3. At any time after the CONNECT message the server may send out one of two kinds of Message: PACKET or TASK
  A PACKET message is a normal network packet to be added to the client's messages received tray.
  {session: INT, text: STRING, from: INT, type: "PACKET"}
  The text will be at most 40 characters long.

  A TASK message changes network parameters and/or suggests a new task.  It does not change network topology.
  Different tasks have different JSON attributes.  However, all share:
  {PercentageError: INT, PercentageDrop: INT, randDelay: INT, name: STRING, type: "TASK")

  A TOPOLOGY task has the attribute {task: "TOPOLOGY"}
  A MESSAGE task has the attributes {task: "MESSAGE", text: STRING, recipient: INT}
  A WHOIS task has the attributes {task: "WHOIS", other: STRING)

4. At any point after the CONNECT message a client may send a MESSAGE:
  {text: STRING, from: INT, to: INT, date: Date, type: "MESSAGE"}
  A "to" attribute of zero indicates "broadcast". The text must be at most 40 characters long.
  
Server Use
----------
It is expected that you will host the client page on a local server, then run the Server application.
Following on from this clients will connect and this will be seen on the server control screen.
At any point the server can send out a task.  This appears as a message on each client screen.

After the first task is begun no more clients can connect.  If a client tries to connect after the first task is started then, if it uses the correct session key, it is assumed that a client has failed and the new client replaces the appropriate node.

There are three tasks:
1) If the distance to recipient is zero then the task is TOPLOLOGY
2) If the WHOIS flag is set then the task is "WHOIS"
3) Otherwise the task is "MESSAGE"
The network has parameter: DropRate, CorruptionRate, Delay which have natural meanings and are sent to the client with each task message.

Client Use
================
Clients will enter an IP address or host name and a port number.  Together with the message "Welcome to the DarkNet."
They will then be connected.  The interface should display their NODE ID.

Clients will be able to formulate packets, but the maximum message sent will by the client should be 40 characters.

Clients can (automatically):
 Add a nonce (4 digits) to the end of a message
 Add an MD5 checksum to the end of a message
 Split up long messages into (at most) 37 character fragments (Hence 40 after fragment id).
 Select a sequence of message fragments and merge them
 Encrypt or decrypt a message
 Send or broadcast messages

Split will add fragment IDs ("n/m:") to each fragment. Merge will check that the fragment ids are consecutive beginning with a "1/m:" fragment.
If there is a !! in the message then characters precedingit are deemed to be message header and are copied to every message fragement.  Hence you can put the destination node for a fragmented multi-hop message as a fragment header.  Merge notices fragment headers properly.  

Since passwords are not to be shared encryption must be commutative:  I.e., if A encrypts, B encrypts, A decrypts then B decrypts the original text should be recovered.  Shamir three pass protocol will be used with the client requiring to perform exponentiation modulo a large prime (the session key: p).  The client will be expected to generate its own pair of keys d,e with de ≡ 1 (mod p-1).  It is expected that clients will use Peter Olson's Big Integers (https://www.npmjs.com/package/big-integer)

Clients will display networks characteristics:
 The network percentage drop rate (if not zero)
 The network corruption rate (if not zero)
 Whether the network will deliver out of sent order.

Clients will see all messages that have been received from the server.
Clients will see the current tasks (TOPOLOGY, MESSAGE, or WHOIS).
When in a Message task clients will see (and be able to copy) the long message that is required to be sent, as well as the recipient node number.
When in the WHOIS task clients will display their node name.

Happy Hacking!
