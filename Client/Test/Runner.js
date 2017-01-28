/**
  * We create a server and wait on its exit to close the clients.
  * Each client has its own cookie jar, so that hteu are seen as new connections by the server.
  * The messages are sent at random on an interval timer.
  * Messages are waited for and if they do not occur at the recipients - a report is made.
  * To begin everyone broadcasts and the networks are discovered.
  *
  * @summary (Headless) Tests the Retro client of BYOI using PhantomJS
**/

// First we Create a server instance.
var spawn = require("child_process").spawn
var child = spawn("/usr/bin/java", ["-jar", "/home/dave/git/humannetwork/Server/jar/HumanNetworkServer.jar"])

//child.stdout.on("data", function (data) {
//  console.log("Server:", data)
//})

child.on("exit", function (code) {
  console.log("Server EXIT:", code)
  phantom.exit();
})

// The Client code starts here
var clients = [];
const NUM_CLIENTS = 5;
var openClients = 0;

/**
  * @summary Check if the first task has begun.
  *
  * @return true if the correct text is found in the last opened client.
 */
checkStarted = function() {
    return openClients == NUM_CLIENTS && clients[NUM_CLIENTS - 1].evaluate(function () {
        return $("#currentTask:contains('Find out the topology of the network')").length;
    });
}

// We begin by creating an array of client instances.
for (i = 0; i < NUM_CLIENTS; i++) { 
    clients[i] = require('webpage').create();
    clients[i].cookieJar = require('cookiejar').create();
    clients[i].onConsoleMessage = function(msg, line, source) {
        if (msg.indexOf("Client:") >= 0) {
            console.log(msg);
        }
    }
}
console.log("Phantom: Created the terminals");

/**
  * @summary Callback for when the final client is ready to begin testing.
  *
  * Keeps checking the final client's task to see if the topology messgae is there.
  * We it is it begins the tests by invoking doTests()
 */
clients[NUM_CLIENTS - 1].onLoadFinished = function() {
    interval = setInterval(function () {
        if (checkStarted()) {
            clearInterval(interval); // Stop this interval
            console.log("Phantom: Ready to Roll");
            doTests()
        }},
        250);
}

/**
  * @summary Open each terminal in turn.
  *
  * Wait 5 seconds for the server to have initialised before beginning to open terminals.
 */
setTimeout(function() {
    console.log("Phantom: Opening the terminals");
    for (i = 0; i < NUM_CLIENTS; i++) { 
        clients[i].open("http://127.0.0.1/~dave/Client/Retro", function(status) {
             if (status === 'success') {
                  openClients = openClients + 1;
             } else {
                  console.log("Phantom: Failed to start client: " + i);
                  phantom.exit();
             }
        });
    }
}, 5000);

var nodes = [];

/**
  * @summary Find network topology and perform several tests.
 */
function doTests() {
    for (var i = 0 ; i < NUM_CLIENTS; i++) {
        nodes[i] = clients[i].evaluate(function() {
            return BYOI.myNode;
        });
    }

    // Broadcast an identifiable message from each client
    var testString = [];
    for (var i = 0; i < NUM_CLIENTS; i++) { 
        testString[i] = "Tracer Message " + nodes[i].toString() + "X" + nodes[i].toString();
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
    console.log("... after the waiting....");
    for (var i = 0; i < NUM_CLIENTS; i++) { 
        var nextTo = [];
        nextTo[i] = clients[i].evaluate(function() {
             var neighbours = [];
             $('.BYOI-message:contains("Tracer Message")').each(function() {
                 neighbours.push($(this).text().split('X')[1]);
             });
             return neighbours;
        });
        console.log(nextTo[i][1], " - ", nodes[i], " - ", nextTo[i][2]);
    }
}

