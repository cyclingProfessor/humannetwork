humannetwork
============

A network simulator for the STEM box challenge.

It is made of two parts: a server which controls the topology and
quality of the network, and a client that pupils can use to receive,
build and send messages to each other.

Usage
-----
Compilation can be done by going in the src directory and typing:

> javac *.java

* Server

The server can be started with:

> java Server

An optional argument is the port on which the server is listening for
incoming connections (default is 10000).

> java Server 19999


* Client

The client can be started with:

> java Client hostname.com 10000

The parameters are the hostname of the server and its port. An
additional parameter can be a node number (in order to reconnect
after a failure).

> java Client hostname.com 10000 42

Code Organisation
-----------------
The Server class is the entry point for the server. Its state is
represented by the classes ConnectionList, LinkList and
MessageList. Its logic is defined in Route, using DelayedMessages. Its
GUI is in ServerGui and ServerController.

The Client class is the entry point for the client. Its state is
represented by MessageList. Its GUI is defined in ClientGui and
ClientController. 

Happy Hacking!
