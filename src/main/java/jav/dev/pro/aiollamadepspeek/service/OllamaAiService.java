package jav.dev.pro.aiollamadepspeek.service;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.ChatResponse;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaAiService {

    private final OllamaApi ollamaApi;

    public OllamaAiService(OllamaApi ollamaApi) {
        this.ollamaApi = ollamaApi;
    }

    public String askOllama(String query) {
        try {
            ChatRequest request = ChatRequest.builder("deepseek-r1:7b")
                    .stream(false)
                    .messages(List.of(
                            Message.builder(Role.SYSTEM)
                                    .content("You are a helpful assistant. Please answer clearly and concisely.")
                                    .build(),
                            Message.builder(Role.USER)
                                    .content(query)  // Savolni bevosita yuboramiz
                                    .build()))
                    .options(OllamaOptions.builder().temperature(0.7).build())
                    .build();

            // Javobni olish
            ChatResponse response = ollamaApi.chat(request);

            // Javobni olish va qaytarish
            String answer = response.message().content();
            return formatNicely(query, answer);

        } catch (Exception e) {
            return "❌ Ollama AI javob berishda xatolik: " + e.getMessage();
        }
    }

    private String formatNicely(String query, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("🧠 *Ollama AI javobi:*\n\n");
        builder.append("❓ *Savol:* ").append(query.trim()).append("\n\n");

        String[] sentences = content.split("(?<=\\.) ");
        int count = 0;
        StringBuilder segment = new StringBuilder();
        int segmentLength = 4000;

        for (String sentence : sentences) {
            segment.append("💬 ").append(sentence.trim()).append("\n\n");
            count += sentence.length();

            if (count >= segmentLength) {
                builder.append(segment.toString()).append("\n");
                segment.setLength(0);
                count = 0;
            }
        }

        if (segment.length() > 0) {
            builder.append(segment.toString()).append("\n");
        }

        return builder.toString().trim();
    }
}
