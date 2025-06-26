import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.UserDAO;
import org.json.JSONObject;

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Main server;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    private boolean connected;

    public ClientHandler(Socket clientSocket, Main server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Đọc username từ client
            String request = reader.readLine();
            System.out.println("Received from client: " + request);
            if (request == null || request.trim().isEmpty()) {
                disconnect();
                return;
            }

            try {
                JSONObject json = new JSONObject(request);
                String type = json.optString("type");
                if ("LOGIN".equalsIgnoreCase(type)) {
                    String usernameInput = json.optString("username");
                    String passwordInput = json.optString("password");

                    UserDAO userDAO = new UserDAO();
                    boolean authenticated = userDAO.authenticateUser(usernameInput, passwordInput);

                    if (authenticated) {
                        this.username = usernameInput;
                        JSONObject response = new JSONObject()
                                .put("status", "success")
                                .put("username", usernameInput);
                        writer.println(response.toString());

                        // Thêm client vào danh sách chat
                        server.addClient(username, this);
                        sendMessage("System: Welcome " + username + " to ST Chat!");

                        // Bắt đầu đọc tin nhắn chat
                        listenForMessages();
                    } else {
                        JSONObject response = new JSONObject()
                                .put("status", "error")
                                .put("message", "Invalid username or password.");
                        writer.println(response.toString());
                        disconnect();
                    }
                } else {
                    JSONObject response = new JSONObject()
                            .put("status", "error")
                            .put("message", "Unknown request type.");
                    writer.println(response.toString());
                    disconnect();
                }

                if ("REGISTER".equalsIgnoreCase(type)) {
                    String usernameInput = json.optString("username");
                    String emailInput = json.optString("email");
                    String passwordInput = json.optString("password");

                    UserDAO userDAO = new UserDAO();
                    boolean registered = userDAO.registerUser(usernameInput, emailInput, passwordInput);

                    JSONObject response = new JSONObject();
                    if (registered) {
                        response.put("status", "success").put("message", "Account created successfully");
                    } else {
                        response.put("status", "error").put("message", "Username or email already exists");
                    }

                    writer.println(response.toString());
                    disconnect();
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
                JSONObject response = new JSONObject()
                        .put("status", "error")
                        .put("message", "Invalid request format.");
                writer.println(response.toString());
                disconnect();
            }

            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous_" + System.currentTimeMillis();
            }
            username = username.trim();

            // Kiểm tra username đã tồn tại chưa
            if (server.getClientUsernames().contains(username)) {
                username = username + "_" + System.currentTimeMillis();
            }

            // Thêm client vào server
            server.addClient(username, this);

            // Gửi thông báo chào mừng
            sendMessage("System: Chào mừng " + username + " đến với ST Chat!");

            // Đọc tin nhắn từ client
            String message;
            while (connected && (message = reader.readLine()) != null) {
                if (message.trim().isEmpty()) {
                    continue;
                }

                // Xử lý các lệnh đặc biệt
                if (message.startsWith("/")) {
                    handleCommand(message);
                } else {
                    // Broadcast tin nhắn thường
                    server.broadcastMessage(username, message, this);
                }
            }

        } catch (IOException e) {
            if (connected) {
                System.err.println("Lỗi khi xử lý client " + username + ": " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    private void listenForMessages() throws IOException {
        String message;
        while (connected && (message = reader.readLine()) != null) {
            if (message.trim().isEmpty()) {
                continue;
            }

            if (message.startsWith("/")) {
                handleCommand(message);
            } else {
                server.broadcastMessage(username, message, this);
            }
        }
    }

    private void handleCommand(String command) {
        String[] parts = command.split(" ", 3); // Chia thành lệnh, username, và tin nhắn
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "/help":
                sendMessage("System: Các lệnh có sẵn:");
                sendMessage("System: /help - Hiển thị trợ giúp");
                sendMessage("System: /users - Danh sách người dùng online");
                sendMessage("System: /time - Thời gian hiện tại");
                sendMessage("System: /quit - Thoát khỏi chat");
                sendMessage("System: /msg <username> <message> - Gửi tin nhắn riêng");
                break;

            case "/users":
                StringBuilder userList = new StringBuilder("Người dùng online (" + server.getClientCount() + "): ");
                for (String user : server.getClientUsernames()) {
                    userList.append(user).append(", ");
                }
                if (userList.length() > 2) {
                    userList.setLength(userList.length() - 2);
                }
                sendMessage("System: " + userList.toString());
                break;

            case "/time":
                sendMessage("System: Thời gian hiện tại: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
                break;

            case "/quit":
                sendMessage("System: Tạm biệt " + username + "!");
                disconnect();
                break;

            case "/msg":
                if (parts.length < 3) {
                    sendMessage("System: Cú pháp: /msg <username> <message>");
                    break;
                }
                String targetUsername = parts[1].trim();
                String privateMessage = parts[2].trim();
                if (targetUsername.equals(username)) {
                    sendMessage("System: Không thể gửi tin nhắn cho chính bạn!");
                    break;
                }
                if (privateMessage.isEmpty()) {
                    sendMessage("System: Tin nhắn không được để trống!");
                    break;
                }
                if (!server.getClientUsernames().contains(targetUsername)) {
                    sendMessage("System: Người dùng '" + targetUsername + "' không tồn tại hoặc không online!");
                    break;
                }

                // Gửi tin nhắn riêng
                ClientHandler targetClient = server.getClientHandler(targetUsername);
                if (targetClient != null && targetClient.isConnected()) {
                    String formattedMessage = "Private from " + username + ": " + privateMessage;
                    targetClient.sendMessage(formattedMessage);
                    sendMessage(formattedMessage); // Gửi lại cho người gửi để xác nhận
                    System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + formattedMessage);
                } else {
                    sendMessage("System: Không thể gửi tin nhắn đến '" + targetUsername + "'.");
                }
                break;

            default:
                sendMessage("System: Lệnh không hợp lệ: " + cmd + ". Gõ /help để xem danh sách lệnh.");
                break;
        }
    }

    public boolean sendMessage(String message) {
        if (writer != null && connected) {
            try {
                writer.println(message);
                return !writer.checkError();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public void disconnect() {
        connected = false;

        if (username != null) {
            server.removeClient(username);
        }

        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return connected;
    }
}