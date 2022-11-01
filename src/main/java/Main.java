import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final var server = new Server();
        server.addHandler("GET", "/index.html", (request, outputStreamStream) -> {
            try {
                Server.positiveresponse(request, outputStreamStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//
//
//
//        for (int i = 0; i < ValidPaths.validPaths.size(); i++) {
//            server.addHandler("GET", ValidPaths.validPaths.get(i), (request, outputStreamStream) -> {
//                try {
//                    Server.positiveresponse(request, outputStreamStream);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }

        server.addHandler("POST", "/links.html", (request, outputStreamStream) -> {
            try {
                Server.positiveresponse(request, outputStreamStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.listen(9999);
    }
}
