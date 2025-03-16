package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PineconeServiceTest {

    private PineconeService pineconeService;
    private OkHttpClient mockHttpClient;
    private Call mockCall;

    private final String apiKey = "test-api-key";
    private final String indexUrl = "https://fake-url";

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(OkHttpClient.class);
        mockCall = mock(Call.class);

        // ✅ Pass mockHttpClient, apiKey, and indexUrl to the constructor
        pineconeService = new PineconeService(mockHttpClient, apiKey, indexUrl);
    }

    @Test
    void upsertVector_successfulResponse_shouldPass() throws IOException {
        // Arrange
        String vectorId = "test-vector-id";
        List<Double> vector = Collections.nCopies(1536, 0.1);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "TDA");

        // Mock a successful response
        Response mockResponse = new Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .message("OK")
                .request(new Request.Builder().url(indexUrl + "/vectors/upsert").build())
                .body(ResponseBody.create("", MediaType.parse("application/json")))
                .build();

        when(mockHttpClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Act & Assert: should NOT throw any exceptions
        assertDoesNotThrow(() -> pineconeService.upsertVector(vectorId, vector, metadata));

        // Verify the correct interactions occurred
        verify(mockHttpClient, times(1)).newCall(any());
        verify(mockCall, times(1)).execute();
    }

    @Test
    void upsertVector_failedResponse_shouldThrowIOException() throws IOException {
        // Arrange
        String vectorId = "test-vector-id";
        List<Double> vector = Collections.nCopies(1536, 0.1);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "TDA");

        // Mock a failed response (HTTP 500)
        Response mockResponse = new Response.Builder()
                .code(500)
                .protocol(Protocol.HTTP_1_1)
                .message("Internal Server Error")
                .request(new Request.Builder().url(indexUrl + "/vectors/upsert").build())
                .body(ResponseBody.create("{ \"error\": \"server error\" }", MediaType.parse("application/json")))
                .build();

        when(mockHttpClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        // Act & Assert: should throw IOException with error message
        IOException exception = assertThrows(IOException.class, () ->
                pineconeService.upsertVector(vectorId, vector, metadata)
        );

        assertTrue(exception.getMessage().contains("Failed to upsert vector"));

        // Verify the correct interactions occurred
        verify(mockHttpClient, times(1)).newCall(any());
        verify(mockCall, times(1)).execute();
    }
}
