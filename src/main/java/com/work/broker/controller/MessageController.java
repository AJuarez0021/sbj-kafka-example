package com.work.broker.controller;

import com.work.broker.model.Message;
import com.work.broker.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody MessageRequest request) {
        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .content(request.content())
                .sender(request.sender())
                .timestamp(LocalDateTime.now())
                .build();

        messageProducer.sendMessage(message);

        return ResponseEntity.ok(Map.of(
                "status", "Message sent",
                "messageId", message.getId()
        ));
    }

    @PostMapping("/simple")
    public ResponseEntity<Map<String, String>> sendSimpleMessage(
            @RequestParam String content,
            @RequestParam String sender) {

        messageProducer.sendMessage(content, sender);

        return ResponseEntity.ok(Map.of(
                "status", "Message sent",
                "content", content,
                "sender", sender
        ));
    }

    public record MessageRequest(String content, String sender) {}
}
