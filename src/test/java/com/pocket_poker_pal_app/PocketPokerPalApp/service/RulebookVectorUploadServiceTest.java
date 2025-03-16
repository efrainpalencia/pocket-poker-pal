package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.exception.EmptyRulebookException;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.RulebookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RulebookVectorUploadServiceTest {

    private PdfProcessingService pdfProcessingService;
    private OpenAIEmbeddingService openAIEmbeddingService;
    private PineconeService pineconeService;
    private RulebookRepository rulebookRepository;

    private RulebookVectorUploadService uploadService;

    @BeforeEach
    void setUp() {
        pdfProcessingService = mock(PdfProcessingService.class);
        openAIEmbeddingService = mock(OpenAIEmbeddingService.class);
        pineconeService = mock(PineconeService.class);
        rulebookRepository = mock(RulebookRepository.class);

        uploadService = new RulebookVectorUploadService(
                pdfProcessingService,
                openAIEmbeddingService,
                pineconeService,
                rulebookRepository
        );
    }

    @Test
    void processAndUpload_withValidChunks_shouldProcessSuccessfully() throws IOException {
        // Arrange
        MockMultipartFile mockPdf = new MockMultipartFile(
                "file", "rules.pdf", "application/pdf", new byte[]{1, 2, 3}
        );

        RulebookEntity rulebook = new RulebookEntity();
        rulebook.setId(UUID.randomUUID());
        rulebook.setTitle("TDA Rules");
        rulebook.setVersion("1.0");
        rulebook.setSource(RulebookEntity.Source.TDA);

        // Mock behaviors
        when(pdfProcessingService.extractChunks(any())).thenReturn(List.of("Rule 1 Explanation", "Rule 2 Explanation"));
        when(openAIEmbeddingService.generateEmbedding(anyString())).thenReturn(Collections.nCopies(1536, 0.01));

        // Act
        uploadService.processAndUpload(mockPdf, rulebook);

        // Assert
        verify(pdfProcessingService, times(1)).extractChunks(mockPdf);
        verify(openAIEmbeddingService, times(2)).generateEmbedding(anyString());
        verify(pineconeService, times(2)).upsertVector(anyString(), anyList(), anyMap());
        verify(rulebookRepository, times(1)).save(rulebook);
    }

    @Test
    void processAndUpload_withEmptyChunks_shouldThrowEmptyRulebookException() throws IOException {
        // Arrange
        MockMultipartFile mockPdf = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[]{}
        );

        RulebookEntity rulebook = new RulebookEntity();
        rulebook.setId(UUID.randomUUID());
        rulebook.setTitle("Seminole Rules");
        rulebook.setVersion("1.0");
        rulebook.setSource(RulebookEntity.Source.SEMINOLE);

        // Mock behaviors: no chunks returned from PDF parsing
        when(pdfProcessingService.extractChunks(any())).thenReturn(Collections.emptyList());

        // Act + Assert
        EmptyRulebookException exception = assertThrows(
                EmptyRulebookException.class,
                () -> uploadService.processAndUpload(mockPdf, rulebook)
        );

        assertEquals("PDF parsing resulted in no chunks. Rulebook cannot be processed.", exception.getMessage());

        // Verify no further processing occurs
        verify(openAIEmbeddingService, never()).generateEmbedding(anyString());
        verify(pineconeService, never()).upsertVector(anyString(), anyList(), anyMap());
        verify(rulebookRepository, never()).save(any());
    }
}
