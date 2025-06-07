package me.chatapp.stchat.service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.Random;

public class ChatService {
    private final Random random = new Random();

    // Simulated responses - replace with actual AI service integration
    private final String[] responses = {
            "Tôi hiểu ý bạn. Bạn có thể nói rõ hơn không?",
            "Đó là một câu hỏi thú vị! Hãy để tôi suy nghĩ...",
            "Cảm ơn bạn đã chia sẻ. Tôi sẽ ghi nhớ điều này.",
            "Tôi đồng ý với quan điểm của bạn.",
            "Bạn có muốn tôi giải thích thêm về vấn đề này không?",
            "Đây là một chủ đề rất hay để thảo luận!",
            "Tôi cần thêm thông tin để có thể trả lời chính xác hơn.",
            "Theo tôi hiểu, bạn đang muốn nói về...",
            "Điều đó nghe có vẻ thú vị. Bạn có thể kể thêm không?",
            "Tôi sẽ cố gắng hỗ trợ bạn tốt nhất có thể!"
    };

    public void processMessage(String message, Consumer<String> callback) {
        // Simulate async processing
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate processing time
                Thread.sleep(1000 + random.nextInt(2000));

                // Generate response based on message content
                String response = generateResponse(message);
                callback.accept(response);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                callback.accept("Xin lỗi, đã có lỗi xảy ra khi xử lý tin nhắn.");
            }
        });
    }

    private String generateResponse(String message) {
        String lowerMessage = message.toLowerCase();

        // Simple keyword-based responses
        if (lowerMessage.contains("xin chào") || lowerMessage.contains("hello")) {
            return "Xin chào! Tôi có thể giúp gì cho bạn?";
        } else if (lowerMessage.contains("cảm ơn") || lowerMessage.contains("thanks")) {
            return "Không có gì! Tôi luôn sẵn sàng giúp đỡ bạn.";
        } else if (lowerMessage.contains("tạm biệt") || lowerMessage.contains("bye")) {
            return "Tạm biệt! Hẹn gặp lại bạn sau nhé!";
        } else if (lowerMessage.contains("giúp") || lowerMessage.contains("help")) {
            return "Tôi có thể giúp bạn trò chuyện và trả lời các câu hỏi. Bạn cần hỗ trợ gì?";
        } else {
            return responses[random.nextInt(responses.length)];
        }
    }
}
