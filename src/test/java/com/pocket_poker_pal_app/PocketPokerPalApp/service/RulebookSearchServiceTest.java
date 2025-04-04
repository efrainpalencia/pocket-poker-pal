package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIEmbeddingService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.RulebookSearchService;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RulebookSearchServiceTest {

    private RulebookSearchService rulebookSearchService;
    private OpenAIEmbeddingService mockEmbeddingService;
    private OkHttpClient mockClient;
    private Call mockCall;
    private Response mockResponse;
    private ResponseBody mockResponseBody;

    @BeforeEach
    void setUp() throws Exception {
        mockEmbeddingService = mock(OpenAIEmbeddingService.class);
        mockClient = mock(OkHttpClient.class);
        mockCall = mock(Call.class);
        mockResponse = mock(Response.class);
        mockResponseBody = mock(ResponseBody.class);

        rulebookSearchService = new RulebookSearchService(mockEmbeddingService);

        // Inject private fields via reflection
        injectPrivateField("client", mockClient);
        injectPrivateField("pineconeApiKey", "mock-api-key");
        injectPrivateField("pineconeIndexUrl", "https://mock-pinecone.com");

        // Mocks
        when(mockClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("""
            {
              "matches": [
                {"metadata": {"text": "Example Rule A"}},
                {"metadata": {"text": "Example Rule B"}}
              ]
            }
        """);

        when(mockEmbeddingService.generateEmbedding("What is the rule for showdown?"))
                .thenReturn(List.of(0.1, 0.2, 0.3));
    }

    private void injectPrivateField(String fieldName, Object value) throws Exception {
        Field field = RulebookSearchService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(rulebookSearchService, value);
    }

    @Test
    void searchRelevantChunks_returnsExpectedResults() throws IOException {
        List<String> result = rulebookSearchService.searchRelevantChunks("What is the rule for showdown?", 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Example Rule A", result.get(0));
        assertEquals("Example Rule B", result.get(1));
    }
}
