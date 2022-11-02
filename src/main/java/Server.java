import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {


    final static ExecutorService SERVICE = Executors.newFixedThreadPool(64);
    private ServerSocket server;
    private Socket socket;
    private static Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen (int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        } try {
            while (true) {
                socket = server.accept();
                SERVICE.submit(processing(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (handlers.containsValue(method)) {
            handlers.get(method).put(path, handler);
        } else {
            handlers.put(method, new ConcurrentHashMap<>(Map.of(path, handler)));
        }
    }

    public static Runnable processing(Socket socket) {
        return () -> {
            try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 final var out = new BufferedOutputStream(socket.getOutputStream())) {
                final var requestLine = in.readLine();
                final var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    socket.close();
                }

                Map<String, String> headers = new HashMap<>();
                String header;
                while (!in.readLine().isBlank()) {
                    header = in.readLine();
                    String[] headerPart = header.split(":", 2);
                    headers.put(headerPart[0].trim(), headerPart[1].trim());
                }

                final var path = parts[1];
                if (!ValidPaths.validPaths.contains(path)) {
                    notFound(out);
                }

                Request request = new Request(parts[0], parts[1], headers, socket.getInputStream());

                Handler handler = Server.getHandlers().get(request.getMethod()).get(request.getPath());

                if (handler == null) {
                    notFound(out);
                    return;
                }

                handler.handle(request, out);
                positiveResponse(request, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public static void positiveResponse(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);
        if (request.getPath().equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();
        }

        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public static Map<String, Map<String, Handler>> getHandlers() {
        return handlers;
    }

    static void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}