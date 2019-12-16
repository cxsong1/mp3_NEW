package cpen221.mp3.server;
import cpen221.mp3.wikimediator.WikiMediator;
import netscape.javascript.JSObject;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

/**
 * WikiMediatorClient is a client that sends requests to the WikiMediatorServer
 * and interprets its replies.
 * A new WikiMediatorClient is "open" until the close() method is called,
 * at which point it is "closed" and may not be used further.
 */
public class WikiMediatorClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    // Rep invariant: socket, in, out != null

    /**
     * Make a WikiMediatorClient and connect it to a server running on
     * hostname at the specified port.
     * @throws IOException if can't connect
     */

    public WikiMediatorClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Send a request to the server. Requires this is "open".
     * @param x Client request as a JSONobject
     * @throws IOException if network or server failure
     */

    public void sendRequest(JSObject x) throws IOException {
        out.print(x + "\n");
        out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires this is "open".
     * @return requested result as a JSONObject
     * @throws IOException if network or server failure
     */

    public BigInteger getReply() throws IOException {
        String reply = in.readLine();
        if (reply == null) {
            throw new IOException("connection terminated unexpectedly");
        }
        try {
            return new BigInteger(reply);
        } catch (NumberFormatException nfe) {
            throw new IOException("misformatted reply: " + reply);
        }
    }

    /**
     * Closes the client's connection to the server.
     * This client is now "closed". Requires this is "open".
     * @throws IOException if close fails
     */
    public void close() throws IOException { in.close();
    out.close();
    socket.close();
    }
    private static final int N = 100;

    /**
     * Use a WikiMediatorServer to find the requested result
     */
    public static void main(String[] args) {
        try {
            WikiMediatorClient client = new WikiMediatorClient("localhost", WikiMediatorServer.WIKIMEDIATOR_PORT);

            // send the requests to find the first N Fibonacci numbers
            for (int x = 1; x <= N; ++x) {
           //     client.sendRequest(x);
                // parsing the request and print it
                System.out.println("request ("+x+") = ?");
            }

            // collect the replies
            for (int x = 1; x <= N; ++x) {
                BigInteger y = client.getReply();
                System.out.println("fibonacci("+x+") = "+y);
            }

            client.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
