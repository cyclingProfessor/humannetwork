humannetwork
============

A network simulator for a STEM challenge activity.

Can be used in alecture to introduce concepts of the internet since it now elies only on a client web browser that supports WebSocket.

It is made of two parts: a server which controls the topology and quality of the network, and a client that pupils can use to receive,build and send messages to each other.

Usage
-----
Compilation can be done by going in the src directory and typing:

> javac Server.java

The server can be started with:

> java Server

The server has several optional argumeents which are parsed in a standard way:
   "?", "Print a help message"
   "h", Host adaptor to listen on.  Can be a hostname or an IP address. Defauls to "127.0.0.1"
   "p", Port number to listen on.  Defaults to 10000
   "s", Session number used to identify clients so that a server can be restarted (not yet implemented)

The client is a standard (adaptive) web page application that should be hosted on a reachable web server.

Code Organisation
-----------------
The Server class is the entry point for the server. Its state is represented by the classes ConnectionList, LinkList and MessageList. Its logic is defined in Route, using DelayedMessages. Its GUI is in ServerGui and ServerController.

Protocol
--------
All messages are encoded in JSON

1. Client to Server: HELLO
When a WebSocket connection is established the client sends a HELLO message
{session: INT, node: INT, type: "HELLO"}
Cleints may use the HELLO message to reestablish a link to an existing server.

2. Server to Client: CONNECTED
The first response from the server will be a CONNECTED message
{session: INT, name: STRING, node: INT, type: "CONNECTED"}
If the session matches the session id sent in the HELLO


3. At any time after the CONNECT message the server may send out one of two kinds of Message: PACKET or TASK
  A PACKET message is normal nework packet to be added tot he client's messages received tray.
  {session: INT, text: STRING, from: INT, type: "PACKET"}
  The text will be at most 40 characters long.

  A TASK message changes network parameters and/or suggests a new task.  It does not change network topology.
  Different tasks have different JSON attributes.  However, all share:
  {PercentageError: INT, PercentageDrop: INT, randDelay: INT, name: STRING, type: "TASK")

  A TOPOLOGY taks has the attribute {task: "TOPOLOGY"}
  A MESSAGE task has the attributes {task: "MESSAGE", text: STRING, recipient: INT}
  A WHOIS task has the attributes {task: "WHOIS", other: STRING)

4. At any point after the CONNECT message a client may send a MESSAGE:
  {text: STRING, from: INT, to: INT, date: Date, type: "MESSAGE"}
  A "to" attribute of zero indicates "broadcast". The text must be at most 40 characters long.
  
Server Use
----------
It is expected that you will host the cleint page on a local server, then run the Server application.
Following on from this clients will connect and this will be seen on the server control screen.
At any point the server can send out a task.  This appears as a message on each client screen.

After the first task is begun no more cleints can connect.  If a client tries to connect after the first task is started then, if it uses the correct session key, it is assumed that a cleint has failed and the new cleint repalces the appropriate node.

There are three tasks:
1) If the distance to recipient is zero then the task is TOPLOLOGY
2) If the WHOIS flag is set then the task is "WHOIS"
3) Otherwise the task is "MESSAGE"
The network has parameter: DropRate, CorruptionRate, Delay which have natural meanings and are sent to the client with each task message.

Client Use
================
Clients will enter an IP address or hostname and a port number.  TOgether with the message "Welcome to the DarkNet."
They will then be connected.  The interface should display their NODE ID.

Cleints will be able to formulate packets, but the maximum message sent will by the cleint should be 40 characters.

Clients can (automatically):
 Add a nonce (4 digits) to the end of a message
 Add an MD5 checksum to the end of a message
 Split up long messages into (at most) 37 character fragments (Hence 40 after fragment id).
 Select a sequence of message fragments and merge them
 Encrypt or decrypt a message
 Send or broadcast messages

Split will add fragment IDs ("nn:") to each fragment. Merge will check that the fragment ids are consecutive beginning with a "01:" fragement.  

Since passwords are not to be shared encryption must be commutative:  Ie, if A encrypts, B encrypts, A decrypts then B decrypts the original text should be recovered.  Shamir three pass protocol wil be used with the client requiring to perform exponentiation modulo a large prime (the session key: p).  The client will be expected to generate its own pair of keys d,e with de ≡ 1 (mod p-1).  It is expected that clients will use Peter Olson's Big Integers (https://www.npmjs.com/package/big-integer)

Clients will display networks characteristics:
 The network percentage drop rate (if not zero)
 The network corruption rate (if not zero)
 Whether the network will deliver out of sent order.

Clients will see all messages that have been received from the server.
Clients will see the current taks (TOPOLOGY, MESSAGE, or WHOIS).
When in a Message task clients will see (and be able to copy) the long message that is required to be sent, as well as the recipient node number.
When in the WHOIS task clients will display their node name.

Happy Hacking!
