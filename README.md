# 🂡 Pocket Poker Pal – Backend (Spring Boot)

Pocket Poker Pal is a voice-powered AI assistant for poker players and professionals. This backend service uses OpenAI for natural language processing, Whisper for speech-to-text transcription, and Pinecone for semantic search across poker rulebooks.

---

## 🛠️ Tech Stack

- **Spring Boot 3** (Java 17+)
- **OpenAI GPT / Whisper API**
- **Pinecone Vector Database**
- **PDFBox** for PDF parsing
- **OkHttp** for external API communication
- **Spring AI** for prompt engineering
- **MySQL** (rulebook metadata storage)
- **Maven** build system

---

## 📁 Project Structure Highlights

| Layer | Purpose |
|-------|---------|
| `controller` | REST endpoints (e.g., `/api/ask`, `/api/ask-audio`) |
| `service` | Handles transcription, embedding, rule chunk search, AI response |
| `entity` | JPA models (e.g., `RulebookEntity`, `AdminUser`) |
| `repository` | Spring Data JPA interfaces |
| `util` | Utility classes like `PromptBuilder`, `BoldSectionTextStripper` |

---

## 🔊 Endpoints

### 🎙 `/api/ask-audio`

- Accepts `MultipartFile` audio input (m4a/webm).
- Uses Whisper to transcribe the question.
- Retrieves matching rulebook chunks from Pinecone.
- Calls OpenAI to generate a rule-based answer.

**POST Example:**

```
curl -X POST http://localhost:8080/api/ask-audio \
  -H "Content-Type: multipart/form-data" \
  -F "audio=@question.m4a"
```

### 💬 `/api/ask`

- Accepts raw text questions.
- Returns an OpenAI-generated answer based on rulebook context.

**POST Example:**

```json
POST /api/ask
{
  "input": "What happens if a player acts out of turn in no-limit Texas Hold'em?"
}
```

---

## 🤖 Services

| Service | Responsibility |
|---------|----------------|
| `AudioQuestionService` | End-to-end flow for handling voice questions |
| `WhisperService` | Uses OpenAI Whisper to transcribe audio |
| `RulebookSearchService` | Embeds the question and queries Pinecone |
| `OpenAIAnswerService` | Builds a prompt and gets the AI answer |
| `RulebookVectorUploadService` | Handles PDF parsing and Pinecone upserts |

---

## 📦 Rulebook Upload Flow

- Admin uploads a rulebook PDF (via `/api/rulebook/upload`)
- Chunks are extracted based on bold section headers using PDFBox
- Each chunk is embedded using OpenAI and upserted to Pinecone
- Metadata is stored in the MySQL `rulebooks` table

---

## 🧪 Testing

- Unit tests use `@SpringBootTest`, `MockMultipartFile`, and Mockito
- Example test coverage: WhisperService, RulebookSearchService

---

## 🧱 Requirements

- Java 17+
- Maven
- MySQL 8.x
- OpenAI API Key
- Pinecone API Key
- Add environment variables in `env.properties` or `.env`

---

## 🗂 Environment Configuration

```properties
# .env or application-dev.properties
openai.api.key=sk-...
pinecone.api.key=pcsk-...
pinecone.index.url=https://your-index.svc.your-region.pinecone.io
jwt.secret=your-jwt-secret
spring.datasource.url=jdbc:mysql://localhost:3306/poker_rules
```

---

## 🚀 Deployment

1. **Build Docker Image**

```bash
./mvnw clean package
docker build -t pocket-poker-pal-backend .
```

2. **Deploy on Railway or EC2**
3. **Connect frontend on Vercel to `/api` endpoints**
