package cpen221.mp3.server;
import org.json.JSONObject;

import java.io.*;
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

    public void sendRequest(JSONObject x) throws IOException {
        System.out.println("request is sent");
        this.out.print(x + "\n" + "\n");
        this.out.flush(); // important! make sure x actually gets sent
    }

    /**
     * Get a reply from the next request that was submitted.
     * Requires that "this" is open.
     * @return requested result as a JSONObject
     * @throws IOException if network or server failure
     */

    public JSONObject getReply() throws IOException {
        System.out.println("fetching reply...");
        String line = "";

        StringBuilder responseStrBuilder = new StringBuilder();

        while((line = this.in.readLine()) != null){
            responseStrBuilder.append(line);
            System.err.println("reply: " + line);
        }

        JSONObject reply = new JSONObject(responseStrBuilder.toString());

        if (reply.get("response") == null) {
            reply.put("status", "failed");
            //throw new IOException("connection terminated unexpectedly");
        }
        try {
            //return new JSONObject(reply);
            reply.put("status", "success");
            return reply;
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

    /**
     * Use a WikiMediatorServer to find the requested result
     */
    public static void main(String[] args) {
        try {
            WikiMediatorClient client = new WikiMediatorClient("localhost", WikiMediatorServer.WIKIMEDIATOR_PORT);

            JSONObject x = new JSONObject();
            x.put("id", 1);
            x.put("type", "simpleSearch");
            x.put("query", "Disney");
            x.put("limit", 3);

            JSONObject x1 = new JSONObject();
            x1.put("id", 2);
            x1.put("type", "zeitgeist");

            client.sendRequest(x);
            System.out.println("request: ("+x+") ");

            client.sendRequest(x1);
            System.out.println("request: ("+x1+") ");

            JSONObject y = client.getReply();
            System.out.println(y);
            //System.out.println("response("+x+") = "+y.get("response"));

            client.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
