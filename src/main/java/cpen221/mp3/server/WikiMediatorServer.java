package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Represents a Server that receives requests over a network socket for tasks
 *      implemented in WikiMediator, then returns results appropriately
 *
 * Abstraction Function:
 *      WIKIMEDIATOR_PORT represents the default port number of the server
 *      serverSocket is the socket the server will use to handle input and output streams
 *      request is a JSON object with the task that is to be performed through the server
 *      response is a JSON object that returns the result of performing the requested task
 *
 * Representation Invariant:
 *      WIKIMEDIATOR_N > 0
 *      1 < WIKIMEDIATOR_PORT < 65535
 *      serverSocket != null
 *      request and response are both non-null and contain an 'id'
 *      request should also include the type of request (ie. task to perform) and any
 *          other appropriate arguments
 *       response should also include a status indicating whether or not a task was
 *          completed successfully and the return value
 *
 */

public class WikiMediatorServer {

    public static final int WIKIMEDIATOR_PORT = 4949;
    private ServerSocket serverSocket;

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *
     * @param port the port number to bind the server to
     *             port number, requires 0 <= port <= 65535
     */
    public WikiMediatorServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects

            System.out.println("serving...");
            final Socket socket = serverSocket.accept();
            // create a new thread to handle that client
            Thread handler = new Thread(new Runnable() {
                public void run() {
                    try {
                        try {
                            handle(socket);
                        } finally {
                            socket.close();
                        }
                    } catch (IOException ioe) {
                        // this exception wouldn't terminate serve(),
                        // since we're now on a different thread, but
                        // we still need to handle it
                        ioe.printStackTrace();
                    }
                }
            });
            // start the thread
            handler.start();
        }
    }

    /**
     * Handle one client connection. Returns when client disconnects.
     *
     * @param socket
     *            socket where client is connected
     * @throws IOException
     *             if connection encounters an error
     * reference: https://stackoverflow.com/questions/22461663/convert-inputstream-to-jsonobject
     */
    private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        BufferedReader in = new BufferedReader(new InputStreamReader(
               socket.getInputStream()));
        String line = "";

        StringBuilder responseStrBuilder = new StringBuilder();

        while(!(line =  in.readLine()).equals("")){
            responseStrBuilder.append(line);
            System.err.println("request: " + line);
        }

        JSONObject x = new JSONObject(responseStrBuilder.toString());

        // similarly, wrap character=>byte stream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream()), true);

        try {
            // each request is a JSONObject containing id, type, etc
            // not a single line
                try {
                    // compute answer and send back to client
                    JSONObject y = process(x);
                    System.err.println("reply: " + y);
                    out.println(y);
                } catch (NumberFormatException e) {
                    // complain about ill-formatted request
                    System.err.println("reply: err");
                    out.print("err\n");
                }
                // important! our PrintWriter is auto-flushing, but if it were
                // not:
                // out.flush();
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Parse the request, get response from WikiMediator and
     * add a response field to the JSONObject
     * This method currently accept one JSONObject,
     * can be modified later to accept JSONArray
     *
     * @param n indicates the request passed
     * @return A new JSONObject with response field
     */
    public JSONObject process(JSONObject n) {
        System.out.println("processing...");
        // new object does not contain queries, fix this
        JSONObject result = new JSONObject(n);
        String type = n.getString("type").replaceAll(",", "");

        // how about a switch statement
        if (type.equals("simpleSearch")){
            String query = n.getString("query").replaceAll(",", "");
            int limit = n.optInt("limit");
            // creates a new mediator each time, can we improve this?
            WikiMediator process = new WikiMediator();
            List<String> response = process.simpleSearch(query, limit);
            result.put("response", response);
        }
        else if (type.equals("getPage")){
            String query = n.getString("query").replaceAll(",", "");
            WikiMediator process = new WikiMediator();
            String response = process.getPage(query);
            result.put("response", response);
        }
        else if (type.equals("getConnectedPages")){
            String query = n.getString("query").replaceAll(",", "");
            int hops = n.optInt("hops");
            WikiMediator process = new WikiMediator();
            List<String> response = process.getConnectedPages(query, hops);
            result.put("response", response);
        }
        else if (type.equals("zeitgeist")){
            int limit = n.optInt("limit");
            WikiMediator process = new WikiMediator();
            List<String> response = process.zeitgeist(limit);
            result.put("response", response);
        }
        else if (type.equals("trending")){
            int limit = n.optInt("limit");
            WikiMediator process = new WikiMediator();
            List<String> response = process.trending(limit);
            result.put("response", response);
        }
        else if (type.equals("peakLoad30s")){
            WikiMediator process = new WikiMediator();
            int response = process.peakLoad30s();
            result.put("response", response);
        }

        return result;
    }

    /**
     * Start a WikiMediatorServer running on the default port.
     */
    public static void main(String[] args) {
        try {
            WikiMediatorServer server = new WikiMediatorServer(WIKIMEDIATOR_PORT);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


