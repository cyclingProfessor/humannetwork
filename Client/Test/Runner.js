/**
  * We create a server from the jar directory and waits on its exit to 
  * close the clients.
  * The Runner then opens up clients 
  * See the Manifest Constants section for configuration.
  * ------------------
  * Each client has its own cookie jar, so that they are seen as new connections by the server.
  * The messages are sent at random on an interval timer.
  * Messages are waited for and if they do not occur at the recipients - a report is made.
  * To begin everyone broadcasts and the networks are discovered.
  *
  * @summary (Headless) Tests the Retro client of BYOI using PhantomJS
**/

const CLIENT_URL = "http://127.0.0.1/~dave/Client/Retro"
const SERVER_PATH = "/home/dave/git/humannetwork/Server/jar/HumanNetworkServer.jar"
const NUM_CLIENTS = 6;
const NUM_MESSAGES = 6;
//////////////////////////////////////////////////////////////////
//  NICE GLOBALS that you might use for tests.
//////////////////////////////////////////////////////////////////

// Any array of the client web instances.
var clients = []; 

// An array of the networks. Each network just a list of node numbers
var network = []; 

// Short links: nextTo[i][1] <-> node[i] == nextTo[i][0] <-> nextTo[i][2]
var nextTo = [];

// Node info: nodes[i] == [num, name, sess]
var nodes = [];

///////////////////////////////////////////////////////////////////////
// First we Create a server instance.
var spawn = require("child_process").spawn
var child = spawn("/usr/bin/java", ["-jar", SERVER_PATH])

// child.stdout.on("data", function (data) {
//     console.log("Server:", data)
// })

child.on("exit", function (code) {
  console.log("Server EXIT:", code)
  phantom.exit();
})

// The Client code starts here
var openClients = 0; // A count of the number of currently opened web pages.

// We begin by creating an array of client instances.
for (i = 0; i < NUM_CLIENTS; i++) { 
    clients[i] = require('webpage').create();
    clients[i].cookieJar = require('cookiejar').create();
    clients[i].onConsoleMessage = function(msg, line, source) {
        if (msg.indexOf("Client:") >= 0) {
            console.log(msg);
        }
    }
    //  Wait 5 seconds for the server to have initialised before beginning to open terminals.
}
setTimeout(openPages, 5000);
console.log("Phantom: Created the terminals");


/**
  * @summary Open each terminal in turn.
  *
 */
function openPages() {
    console.log("Phantom: Opening the terminals");
    for (i = 0; i < NUM_CLIENTS; i++) { 
        clients[i].open(CLIENT_URL, function(status) {
             if (status === 'success') {
                  openClients = openClients + 1;
                  console.log("Phantom: Succeeded Opening client: " + openClients.toString());
             } else {
                  console.log("Phantom: Failed to start client: " + i);
                  phantom.exit();
             }
        });
    }
    console.log("Phantom: Opened the terminals");
    //  Wait for clients to have connected.
    setTimeout(checker, 5000);
}

/**
  * @summary Check if the first task has begun.
  *
  * @return true if the correct text is found in the last opened client.
 */
checkStarted = function() {
    return openClients == NUM_CLIENTS && clients[NUM_CLIENTS - 1].evaluate(function () {
        return $("#currentTask:contains('connections in your network')").length;
    });
}

/**
  * Keeps checking the final client's task to see if the topology message is there.
  * When it is it begins the tests by invoking doTests()
 */
function checker() {
    interval = setInterval(function () {
        if (checkStarted()) {
        clearInterval(interval); // Stop this interval
            console.log("Phantom: Ready to Roll");
            doTests()
        }},
        250);
}

/**
  * @summary Find network topology and perform several tests.
 */
function doTests() {
    for (var i = 0 ; i < NUM_CLIENTS; i++) {
        nodes[i] = clients[i].evaluate(function() {
            return Array(BYOI.myNode, BYOI.myName, BYOI.mySession);
        });
        console.log(nodes[i].toString());
    }

    // Broadcast an identifiable message from each client
    var testString = [];
    for (var i = 0; i < NUM_CLIENTS; i++) { 
        testString[i] = "Tracer Message " + nodes[i][0].toString() + "X" + nodes[i][0].toString();
	var msg = testString[i];
        clients[i].evaluate(function(msg) {
            $('#recipient').val("0");
            $('#msg').val(msg);
            $('.send-btn').click();
        }, msg);
    }
    console.log("Waiting");
    setTimeout(moreTests,10000);
}

function moreTests() {
    // Now we trace all messages sent and received.
    // We could use the received from node number
    console.log("... after the waiting....");
    for (var i = 0; i < NUM_CLIENTS; i++) { 
        nextTo[i] = clients[i].evaluate(function() {
             var neighbours = [];
             $('.BYOI-message:contains("Tracer Message")').each(function() {
                 neighbours.push($(this).text().split('X')[1]);
             });
             return neighbours;
        });
    }

    // Checker matrix to see when we have found all nodes
    var checker = Array(NUM_CLIENTS);
    for (i = 0 ; i < NUM_CLIENTS ; i++) {
        checker[i] = true;
    }
    
    // Find the first unused Node
    var netNum = 0;
    var firstIndex = 0;
    while (firstIndex != -1) {
        var firstNode = nextTo[firstIndex][0];
        network[netNum] = [nextTo[firstIndex][0], nextTo[firstIndex][1]];
        var previous = nextTo[firstIndex][0];
        var current = nextTo[firstIndex][1];
        checker[firstIndex] = false;

        while (current != firstNode) {
            // Extract connections for current
            nextNodeIndex = 0;
            for (i = 0; nextTo[i][0] != current ; i++)
                nextNodeIndex = i + 1 ;
            checker[nextNodeIndex] = false;

            // Get the next node
            if (nextTo[nextNodeIndex][1] != previous) {
                next = nextTo[nextNodeIndex][1];
            } else {
                next = nextTo[nextNodeIndex][2];
            }
            previous = current;
            current = next;
            network[netNum].push(current);
        }
        firstIndex = checker.indexOf(true);
        netNum = netNum + 1;
    }
    network.forEach(function(net) {
        console.log(net.toString());
    });
    nextTo.forEach(function(n) {
        console.log(n[1] + " <-> " + n[0] + " <-> " + n[2]);
    });
    nodes.forEach(function(id) {
        console.log("Num: " + id[0] + ", Name: " + id[1] + ", Session: " + id[2]);
    });
    console.log("Waiting");
    setTimeout(evenMoreTests,1000);
}

var messageCount = 0;
function evenMoreTests() {
    console.log("Now more testing....");
    // Send messages now
    interval = setInterval(function (count) {
        if (messageCount != NUM_MESSAGES) {
            var from = messageCount % NUM_CLIENTS;
            var rec = nextTo[from][1];
            console.log("Broadcast from: " + nodes[from]);

            msg = "Header!!Tester - a long message with a Header" + nodes[from][1] + messageCount.toString();
            clients[from].evaluate(function(msg) {
                $('#msg').val(msg);
                $('.split-btn').click();

                $('#all .fragment').each(function(index) {
                    var outval = "";
                    $.each($(this).data(), function(key, value) {
                        outval += "[" + key + "] => [" + value + "]";
                    });
                    console.log("Client: fragment found: " + outval + "::::" + $(this).text());
                    });
            }, msg);

            console.log("Message from: " + nodes[from] + " to: " + rec);
            clients[from].evaluate(function(rec) {
                $('#recipient').val(rec);
            }, rec);
            msg = "Tester" + nodes[from][1] + messageCount.toString();
            clients[from].evaluate(function(msg) {
                $('#msg').val(msg);
                $('.send-btn').click();
            }, msg);
            messageCount = messageCount + 1;
        } else {
            clearInterval(interval); // Stop this interval
        }
    }, 50);
}
