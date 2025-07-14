package me.chatapp.stchat.controller;

import me.chatapp.stchat.api.ConversationApiClient;
import me.chatapp.stchat.model.Conversation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ConversationController {

    private final ConversationApiClient conversationApiClient;

    public ConversationController(ConversationApiClient conversationApiClient) {
        this.conversationApiClient = conversationApiClient;
    }

    public CompletableFuture<Integer> createConversationId(int user1Id, int user2Id) {
        return conversationApiClient.createPrivateConversation(user1Id, user2Id)
                .thenApply(Conversation::getId);
    }

    public CompletableFuture<Integer> createChannelConversation(String channelName) {
        return conversationApiClient.createChannelConversation(channelName)
                .thenApply(Conversation::getId);
    }



}
