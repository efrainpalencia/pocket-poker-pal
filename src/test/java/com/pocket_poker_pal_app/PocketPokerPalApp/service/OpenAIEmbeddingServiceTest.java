package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.embedding.EmbeddingClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenAIEmbeddingServiceTest {

    private OpenAIEmbeddingService embeddingService;
    private EmbeddingClient mockEmbeddingClient;

    @BeforeEach
    void setUp() {
        mockEmbeddingClient = mock(EmbeddingClient.class);
        embeddingService = new OpenAIEmbeddingService(mockEmbeddingClient);
    }

    @Test
    void generateEmbedding_shouldReturnValidEmbedding() {
        // Arrange
        List<Double> expectedEmbedding = Collections.nCopies(1536, 0.01);
        doReturn(expectedEmbedding).when(mockEmbeddingClient).embed(Mockito.<String>any());

        String inputText = "Sample text to embed";

        // Act
        List<Double> actualEmbedding = embeddingService.generateEmbedding(inputText);

        // Assert
        assertNotNull(actualEmbedding);
        assertEquals(1536, actualEmbedding.size());
        assertEquals(0.01, actualEmbedding.getFirst());

        verify(mockEmbeddingClient, times(1)).embed(inputText);
    }

    @Test
    void generateEmbedding_withEmptyString_shouldReturnEmptyEmbedding() {
        // Arrange
        List<Double> expectedEmbedding = Collections.emptyList();
        doReturn(expectedEmbedding).when(mockEmbeddingClient).embed(Mockito.<String>any());

        // Act
        List<Double> actualEmbedding = embeddingService.generateEmbedding("");

        // Assert
        assertNotNull(actualEmbedding);
        assertTrue(actualEmbedding.isEmpty());

        verify(mockEmbeddingClient, times(1)).embed("");
    }

    @Test
    void generateEmbedding_withNullInput_shouldHandleGracefully() {
        // Arrange
        doReturn(Collections.emptyList()).when(mockEmbeddingClient).embed(Mockito.<String>isNull());

        // Act
        List<Double> actualEmbedding = embeddingService.generateEmbedding(null);

        // Assert
        assertNotNull(actualEmbedding);
        assertTrue(actualEmbedding.isEmpty());

        verify(mockEmbeddingClient, times(1)).embed((String) null);
    }
}
