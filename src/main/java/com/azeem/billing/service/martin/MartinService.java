/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.billing.service.martin;

import com.azeem.billing.exception.MartinResponseNotValidException;
import com.azeem.billing.model.martin.SqlResponse;
import com.azeem.billing.service.billing.BillingIngestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MartinService {
    private static final Logger log = LoggerFactory.getLogger(MartinService.class);
    private final ChatModel chatModel;
    private final SchemaService schemaService;

    public MartinService(ChatModel chatModel, SchemaService schemaService) {
        this.chatModel = chatModel;
        this.schemaService = schemaService;
    }

    /**
     * @param promptText The prompt the user submits to Martin
     * @return SqlResponse object
     */
    public SqlResponse generateResponse(String promptText) {
        Prompt prompt = createPrompt(promptText);

        ChatResponse response = chatModel.call(prompt);
        String json = response.getResult().getOutput().getText();

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, SqlResponse.class);
        } catch (JsonProcessingException e) {
            log.error("There was a data format error with the Martin's response.");
            throw new MartinResponseNotValidException("There was a data format error with the Martin's response.");
        }
    }

    private Prompt createPrompt(String promptText) {
        String schema = schemaService.getSchema();

        return new Prompt(List.of(
                new SystemMessage("""
                    You are a PostgreSQL query generator.
                    Return valid PostgreSQL 16 syntax.
                    Read-only access only.
                    
                    Return ONLY valid JSON.
                    Format:
                    {
                      "sql": "<PostgreSQL query>",
                      "reasoning": "<short explanation>"
                    }
                    Do not include markdown, comments, or extra text.
                    """
                ),
                new SystemMessage("Schema:\n" + schema),
                new UserMessage(promptText)
        ));
    }
}
