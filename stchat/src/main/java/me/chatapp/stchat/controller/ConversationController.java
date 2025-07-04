package me.chatapp.stchat.controller;

import java.util.HashMap;
import java.util.Map;

public class ConversationController {
    private final Map<String, Integer> conversationMap = new HashMap<>();

    public int getOrCreateConversationId(String contactName) {
        return conversationMap.computeIfAbsent(contactName, name -> Math.abs(name.hashCode()));
    }
}
