import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int DEFAULT_PORT = 12345;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private ServerSocket serverSocket;
    private boolean running;
    private final Map<String, ClientHandler> clients;
    private final ExecutorService threadPool;
    private final Object clientsLock = new Object();

    public Main() {
        this.clients = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
        this.running = false;
    }
    // Trong Main.java, thêm phương thức này vào lớp Main
    public ClientHandler getClientHandler(String username) {
        synchronized (clientsLock) {
            return clients.get(username);
        }
    }
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            running = true;

            System.out.println("=== ST Chat Server ===");
            System.out.println("Server đang chạy trên port: " + port);
            System.out.println("Thời gian khởi động: " + LocalDateTime.now().format(TIME_FORMATTER));
            System.out.println("Chờ kết nối từ client...\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    threadPool.execute(clientHandler);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Lỗi khi chấp nhận kết nối: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Không thể khởi động server: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // Disconnect all clients
            synchronized (clientsLock) {
                for (ClientHandler client : clients.values()) {
                    client.disconnect();
                }
                clients.clear();
            }

            threadPool.shutdown();
            System.out.println("Server đã dừng");
        } catch (IOException e) {
            System.err.println("Lỗi khi dừng server: " + e.getMessage());
        }
    }

    public void addClient(String username, ClientHandler clientHandler) {
        synchronized (clientsLock) {
            clients.put(username, clientHandler);
            System.out.println("[" + getCurrentTime() + "] Người dùng '" + username + "' đã kết nối. Tổng online: " + clients.size());

            // Thông báo cho tất cả client về người dùng mới
            broadcastMessage("System", username + " đã tham gia phòng chat", null);

            // Gửi danh sách người dùng online cho client mới
            sendUserList(clientHandler);
        }
    }

    public void removeClient(String username) {
        synchronized (clientsLock) {
            if (clients.remove(username) != null) {
                System.out.println("[" + getCurrentTime() + "] Người dùng '" + username + "' đã ngắt kết nối. Tổng online: " + clients.size());

                // Thông báo cho tất cả client về người dùng rời đi
                broadcastMessage("System", username + " đã rời khỏi phòng chat", null);
            }
        }
    }

    public void broadcastMessage(String sender, String message, ClientHandler excludeClient) {
        String formattedMessage = sender + ": " + message;

        synchronized (clientsLock) {
            List<String> disconnectedClients = new ArrayList<>();

            for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                ClientHandler client = entry.getValue();
                if (client != excludeClient) {
                    if (!client.sendMessage(formattedMessage)) {
                        disconnectedClients.add(entry.getKey());
                    }
                }
            }

            // Remove disconnected clients
            for (String username : disconnectedClients) {
                removeClient(username);
            }
        }

        // Log message to server console
        System.out.println("[" + getCurrentTime() + "] " + formattedMessage);
    }

    private void sendUserList(ClientHandler clientHandler) {
        synchronized (clientsLock) {
            StringBuilder userList = new StringBuilder("Người dùng online: ");
            for (String username : clients.keySet()) {
                userList.append(username).append(", ");
            }
            if (userList.length() > 2) {
                userList.setLength(userList.length() - 2); // Remove last comma and space
            }
            clientHandler.sendMessage("System: " + userList.toString());
        }
    }

    public int getClientCount() {
        synchronized (clientsLock) {
            return clients.size();
        }
    }

    public Set<String> getClientUsernames() {
        synchronized (clientsLock) {
            return new HashSet<>(clients.keySet());
        }
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    public static void main(String[] args) {
        Main server = new Main();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nĐang dừng server...");
            server.stop();
        }));

        // Start server
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port không hợp lệ, sử dụng port mặc định: " + DEFAULT_PORT);
            }
        }

        server.start(port);
    }
}