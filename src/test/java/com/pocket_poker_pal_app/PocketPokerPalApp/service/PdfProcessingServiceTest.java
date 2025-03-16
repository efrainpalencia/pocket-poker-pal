package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfProcessingServiceTest {

    private PdfProcessingService pdfProcessingService;

    @BeforeEach
    void setUp() {
        pdfProcessingService = new PdfProcessingService();
    }

    @AfterEach
    void tearDown() {
        pdfProcessingService = null;
    }

    @Test
    void extractChunks_withNoContent_returnsEmptyList() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            document.save(outputStream);
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", outputStream.toByteArray()
        );

        List<String> chunks = pdfProcessingService.extractChunks(mockFile);

        assertNotNull(chunks);
        assertEquals(0, chunks.size());
    }

    @Test
    void extractChunks_withContent_returnsChunks() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("1: Rule One Explanation.");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("2: Rule Two Explanation.");
            contentStream.endText();
            contentStream.close();

            document.save(outputStream);
        }

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "rules.pdf", "application/pdf", outputStream.toByteArray()
        );

        List<String> chunks = pdfProcessingService.extractChunks(mockFile);

        assertNotNull(chunks);
        assertFalse(chunks.isEmpty());
        assertEquals(2, chunks.size());

        assertTrue(chunks.get(0).contains("1: Rule One Explanation"));
        assertTrue(chunks.get(1).contains("2: Rule Two Explanation"));
    }
}
