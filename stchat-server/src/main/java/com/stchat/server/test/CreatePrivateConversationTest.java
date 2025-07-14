package com.stchat.server.test;

import com.stchat.server.model.Conversation;
import com.stchat.server.service.ConversationService;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CreatePrivateConversationTest {
    @Test
    public void testCreatePrivateConversation_ShouldGenerateId() {
        Conversation conv = ConversationService.createPrivateConversation(1, 2);
        assertNotNull(conv);
        assertTrue("ID should be generated and greater than 0", conv.getId() > 0);
    }
}
