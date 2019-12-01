package cpen221.mp3.server;

import cpen221.mp3.wikimediator.WikiMediator;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents a Server that receives requests over a network socket for tasks
 *      implemented in WikiMediator, then returns results appropriately
 *
 * Abstraction Function:
 *      WIKIMEDIATOR_PORT represents the default port number of the server
 *      WIKIMEDIATOR_N represents the number of clients the server can listen to at the same time
 *      serverSocket is the socket the server will use to handle input and output streams
 *      request is a JSON string with the task that is to be performed through the server
 *      reponse is a JSON string that returns the result of performing the requested task
 *
 * Representation Invariant:
 *      WIKIMEDIATOR_N > 0
 *      1 < WIKIMEDIATOR_PORT < 65535
 *      serverSocket != null
 *      request and response are both non-null and contain an 'id'
 *      request should also include the type of request (ie. task to perform) and any
 *          other appropriate arguments
*       response should also include a status indicating whether or not a task was
 *          completed succesfully and the return value
 *
 */


public class WikiMediatorServer {

    public static final int WIKIMEDIATOR_PORT = 4949;
    public static final int WIKIMEDIATOR_N = 1;
    private JSONObject request;
    private JSONObject response;
    private ServerSocket serverSocket;

    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *
     * @param port the port number to bind the server to
     *             port number, requires 0 <= port <= 65535
     * @param n    the number of concurrent requests the server can handle
     */
    public WikiMediatorServer(int port, int n) throws IOException {
        /* TODO: Implement this method */
        serverSocket = new ServerSocket(port);
        request = new JSONObject();
        response = new JSONObject();
    }

    /**
     * Run the server, listening for connections and handling them.
     *
     * @throws IOException if the main server socket is broken
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            try {
                handle(socket);
            } catch (IOException ioe) {
                ioe.printStackTrace(); // but don't terminate serve()
            } finally {
                socket.close();
            }
        }
    }

    private void handle(Socket socket) throws IOException {
        System.err.println("client connected");

        // get the socket's input stream, and wrap converters around it
        // that convert it from a byte stream to a character stream,
        // and that buffer it so that we can read a line at a time
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));

        // similarly, wrap character=>bytestream converter around the
        // socket output stream, and wrap a PrintWriter around that so
        // that we have more convenient ways to write Java primitive
        // types to it.
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                socket.getOutputStream()));

        try {
            // each request is a single line containing a number
            for (String line = in.readLine(); line != null; line = in
                    .readLine()) {
                System.err.println("request: " + line);
                try {
                    String x = String.valueOf(line);
                    // compute answer and send back to client
                    String y = new WikiMediator().getPage(x);
                    System.err.println("reply: " + y);
                    out.print(y + "\n");
                } catch (NumberFormatException e) {
                    // complain about ill-formatted request
                    System.err.println("reply: err");
                    out.println("err");
                }
                // important! flush our buffer so the reply is sent
                out.flush();
            }
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Start a WikiMediatorServer running on the default port.
     */
    public static void main(String[] args) {
        try {
            WikiMediatorServer server = new WikiMediatorServer(WIKIMEDIATOR_PORT, WIKIMEDIATOR_N);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


