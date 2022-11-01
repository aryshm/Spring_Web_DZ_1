import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final var server = new Server();
        server.addHandler("GET", "/index.html", (request, outputStreamStream) -> {
            try {
                Server.positiveResponse(request, outputStreamStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/resources.html", (request, outputStreamStream) -> {
            try {
                Server.positiveResponse(request, outputStreamStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.listen(9999);
    }
}
