/*
 * (C) Copyright 2026 Jawad Azeem
 * Apache 2.0 License
 */

package com.azeem.blueprint.service.martin;

import com.azeem.blueprint.exception.MartinResponseNotValidException;
import com.azeem.blueprint.model.martin.MartinResponse;
import com.azeem.blueprint.model.martin.SqlResponse;
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
    private final QueryExecutionService queryExecutionService;
    private final SqlValidationService sqlValidationService;
    private final ObjectMapper objectMapper;

    public MartinService(ChatModel chatModel,
                         SchemaService schemaService,
                         QueryExecutionService queryExecutionService,
                         SqlValidationService sqlValidationService,
                         ObjectMapper objectMapper
    ) {
        this.chatModel = chatModel;
        this.schemaService = schemaService;
        this.queryExecutionService = queryExecutionService;
        this.sqlValidationService = sqlValidationService;
        this.objectMapper = objectMapper;
    }

    public MartinResponse ask(String promptText, String currentPeriod) {

        SqlResponse sqlResponse = generateResponse(promptText, currentPeriod);

        if (!sqlValidationService.isValidSql(sqlResponse)) {
            throw new MartinResponseNotValidException("Unsafe SQL detected");
        }

        List<Map<String, Object>> results =
                queryExecutionService.executeQuery(sqlResponse);

        Prompt explanationPrompt = new Prompt(List.of(
                new SystemMessage("You are Martin, a billing analyst."),
                new UserMessage("Question: " + promptText),
                new UserMessage("SQL: " + sqlResponse.getSql()),
                new UserMessage("Results: " + results)
        ));

        return new MartinResponse(
            chatModel.call(explanationPrompt)
                .getResult()
                .getOutput()
                .getText(),
            sqlResponse.getSql(),
            sqlResponse.getReasoning()
        );
    }

    /**
     * @param promptText The prompt the user submits to Martin
     * @return SqlResponse object
     */
    private SqlResponse generateResponse(String promptText, String currentPeriod) {
        Prompt prompt = createPrompt(promptText, currentPeriod);

        ChatResponse response = chatModel.call(prompt);
        String json = response.getResult().getOutput().getText();

        SqlResponse sqlResponse;

        try {
            sqlResponse = objectMapper.readValue(json, SqlResponse.class);
        } catch (JsonProcessingException e) {
            log.error("There was a data format error with the Martin's response. Here was " +
                    "Martin's response: {}", json);
            throw new MartinResponseNotValidException("There was a data format error with the Martin's response.");
        }

        log.info("Martin generated: {}", sqlResponse);
        return sqlResponse;
    }

    private Prompt createPrompt(String promptText, String currentPeriod) {
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
                    
                    All queries MUST include WHERE billing_period = 
                    """ + currentPeriod + "Schema:\n" + schema +
                    " dummy-data is a valid billing_period, it is for demo purposes."
                ),
                new UserMessage(promptText)
        ));
    }
}
