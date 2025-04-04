package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.exception.EmptyRulebookException;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.RulebookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RulebookVectorUploadServiceTest {

    @Mock
    private PdfProcessingService pdfProcessingService;

    @Mock
    private OpenAIEmbeddingService openAIEmbeddingService;

    @Mock
    private PineconeService pineconeService;

    @Mock
    private RulebookRepository rulebookRepository;

    @InjectMocks
    private RulebookVectorUploadService uploadService;

    @Mock
    private MultipartFile mockFile;

    private RulebookEntity rulebook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rulebook = new RulebookEntity();
        rulebook.setId(UUID.randomUUID());
        rulebook.setTitle("Test Rulebook");
        rulebook.setVersion("1.0");
        rulebook.setSource(RulebookEntity.Source.TDA);
    }

    @Test
    void testProcessAndUpload_withValidInput_shouldProcessSuccessfully() throws IOException {
        List<String> chunks = List.of("Valid Chunk 1", "Valid Chunk 2");
        List<Double> fakeEmbedding = List.of(0.1, 0.2, 0.3);

        when(pdfProcessingService.extractChunksByBoldTitles(mockFile)).thenReturn(chunks);
        when(openAIEmbeddingService.generateEmbedding(anyString())).thenReturn(fakeEmbedding);

        uploadService.processAndUpload(mockFile, rulebook);

        verify(openAIEmbeddingService, times(2)).generateEmbedding(anyString());
        verify(pineconeService, times(2)).upsertVector(anyString(), anyList(), anyMap());
        verify(rulebookRepository).save(rulebook);
    }

    @Test
    void testProcessAndUpload_withEmptyChunks_shouldThrowException() throws IOException {
        when(pdfProcessingService.extractChunksByBoldTitles(mockFile)).thenReturn(Collections.emptyList());

        assertThrows(EmptyRulebookException.class, () -> {
            uploadService.processAndUpload(mockFile, rulebook);
        });

        verify(openAIEmbeddingService, never()).generateEmbedding(anyString());
        verify(rulebookRepository, never()).save(any());
    }

    @Test
    void testProcessAndUpload_withLargeChunk_shouldSplitAndEmbed() throws IOException {
        String longText = "Line\n".repeat(1000); // ~5000 chars
        when(pdfProcessingService.extractChunksByBoldTitles(mockFile)).thenReturn(List.of(longText));
        when(openAIEmbeddingService.generateEmbedding(anyString()))
                .thenReturn(List.of(0.1, 0.2, 0.3));

        uploadService.processAndUpload(mockFile, rulebook);

        verify(openAIEmbeddingService, atLeastOnce()).generateEmbedding(anyString());
        verify(pineconeService, atLeastOnce()).upsertVector(anyString(), anyList(), any(Map.class));
        verify(rulebookRepository).save(rulebook);
    }
}
