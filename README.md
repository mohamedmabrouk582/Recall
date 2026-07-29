<div align="center">

# 🧠 Recall

### A private, on-device-first AI notebook for Android

_Capture anything → understand it on your phone → **ask your own knowledge** (RAG)._

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Design](https://img.shields.io/badge/Design-Material%203-757575?logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Min SDK](https://img.shields.io/badge/minSdk-24-orange)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/targetSdk-37-brightgreen)](https://developer.android.com)

<br/>

**🧠 AI capabilities**

[![On-Device AI](https://img.shields.io/badge/AI-On--Device%20First-00C853?logo=android&logoColor=white)](https://ai.google.dev/edge)
[![RAG](https://img.shields.io/badge/AI-RAG%20Grounded-6C3EF6)](https://ai.google.dev)
[![Agents](https://img.shields.io/badge/AI-Agentic%20Tools-FF6D00)](https://ai.google.dev)
[![Privacy](https://img.shields.io/badge/AI-Private%20%26%20Offline-1DE9B6?logo=shield&logoColor=white)](https://developer.android.com/privacy)
[![Semantic Search](https://img.shields.io/badge/AI-Semantic%20Search-9C27B0)](https://ai.google.dev)

**🔧 AI engines**

[![ML Kit](https://img.shields.io/badge/On--Device-ML%20Kit%20OCR%20%2F%20Vision-4285F4?logo=google&logoColor=white)](https://developers.google.com/ml-kit)
[![LiteRT](https://img.shields.io/badge/On--Device-LiteRT%20(TFLite)-FF6F00?logo=tensorflow&logoColor=white)](https://ai.google.dev/edge/litert)
[![MediaPipe](https://img.shields.io/badge/On--Device-MediaPipe%20Embeddings-00A6FB?logo=google&logoColor=white)](https://ai.google.dev/edge/mediapipe)
[![Gemini Nano](https://img.shields.io/badge/On--Device-Gemini%20Nano%20LLM-8E24AA?logo=googlegemini&logoColor=white)](https://developer.android.com/ai/gemini-nano)
[![Gemini Cloud](https://img.shields.io/badge/Cloud-Gemini%20Free%20Tier-1A73E8?logo=googlegemini&logoColor=white)](https://ai.google.dev)

</div>

---

## 📖 Table of contents

- [What is Recall?](#-what-is-recall)
- [Why it exists](#-why-it-exists)
- [What Recall covers](#-what-recall-covers)
- [Key features](#-key-features)
- [Screens](#-screens)
- [Architecture](#-architecture)
- [Module graph](#-module-graph)
- [Capture & ingest pipeline](#-capture--ingest-pipeline)
- [On-device AI stack](#-on-device-ai-stack)
- [The intelligence layer](#-the-intelligence-layer-what-makes-it-smart)
- [RAG pipeline](#-rag-pipeline)
- [RAG variants](#-rag-variants)
- [Agent tool loop](#-agent-tool-loop)
- [On-device vs cloud routing](#-on-device-vs-cloud-routing)
- [Model lifecycle](#-model-lifecycle)
- [Privacy & PII redaction flow](#-privacy--pii-redaction-flow)
- [Evaluation harness](#-evaluation-harness)
- [Tech stack](#-tech-stack)
- [Project structure](#-project-structure)
- [Getting started](#-getting-started)
- [Configuration](#-configuration)
- [Build & run](#-build--run)
- [Quality & evaluation](#-quality--evaluation)
- [Privacy & security](#-privacy--security)
- [Roadmap](#-roadmap)
- [Competency map](#-competency-map-learning-goals)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🧩 What is Recall?

**Recall** is a personal knowledge notebook that captures photos, text, voice, and PDFs,
understands them **on the device** (OCR, transcription, tagging, embeddings), and lets you
**ask questions grounded in your own notes** using Retrieval-Augmented Generation (RAG).

It is **on-device first**: everything that _can_ run locally _does_ run locally, for privacy,
offline access, and zero cost. The cloud is used only when a task genuinely needs heavier
reasoning — and even then, only free-tier models.

> Think of it as your "second brain" that lives on your phone, respects your privacy, and
> actually reasons over what you feed it.

---

## 🎯 Why it exists

Recall is both a **real product** and a **structured learning project** to grow from
**Android engineer → Mobile AI engineer**. Every feature is chosen because it forces a real
mobile-AI engineering decision (on-device vs cloud, latency vs quality, privacy vs power),
not because it looks flashy.

That means the codebase demonstrates the full production surface of modern on-device AI:
capture pipelines, custom model inference, embeddings + vector search, RAG orchestration,
agentic tool-calling, routing, evaluation harnesses, and observability.

---

## 🗺 What Recall covers

The whole Mobile-AI surface area, mapped to one product:

```mermaid
mindmap
  root((Recall))
    Capture
      Camera OCR
      Voice STT
      Text notes
      PDF import
    On-Device ML
      LiteRT / TFLite
      GPU delegate
      Quantization bakeoff
      Model registry
    Retrieval / RAG
      On-device embeddings
      Vector store
      Hybrid BM25 + vector
      Chunking
      Rerank
    Generation
      On-device LLM
      Free-tier cloud LLM
      Streaming
      Citations
    Agents
      Multi-step tool loop
      Re-retrieval
      Traces
    Routing & Systems
      AnswerRouter
      Offline degradation
      Timeouts
      Telemetry
    Quality
      Golden sets
      CER / WER
      recall@k
      Groundedness
    Privacy & Security
      Encryption at rest
      PII redaction
      Prompt-injection defense
    Platform Glue
      WorkManager
      App Functions
      Feature flags
```

---

## ✨ Key features

| Area | Capability |
|------|------------|
| 📸 **Capture** | Camera scan with **on-device OCR**, voice memos with **on-device transcription**, quick text notes, PDF import |
| 🏷️ **Understand** | **Auto-tagging** via a custom LiteRT model, **on-device embeddings** for every note |
| 🔎 **Search** | Fully offline **semantic search** ("find that thing about invoices") + hybrid keyword search |
| 💬 **Ask** | **"Ask my notes"** RAG chat with streaming answers, cancel-in-flight, and **source citations** |
| 🧠 **Summarize** | On-device note summaries (Gemini Nano / on-device LLM) |
| 🤖 **Agents** | Multi-step tool-calling agent (plan → tool → observe) with visible traces |
| 🔊 **Read aloud** | On-device neural text-to-speech |
| 🧪 **AI Lab** | Model status, latency/quantization bakeoffs, RAG traces, and evaluation scores |
| 🔐 **Private** | Encryption at rest, PII redaction before any cloud call, export/delete your data |

---

## 📱 Screens

| # | Screen | Purpose |
|---|--------|---------|
| 1 | Onboarding | Privacy-first value proposition |
| 2 | Home / Library | Scrollable list of notes with filters |
| 3 | Capture hub | Choose input type (scan / voice / text / PDF) |
| 4 | Camera OCR | Live scanning with on-device text recognition |
| 5 | Voice memo | Record + live on-device transcript |
| 6 | Note editor | View/edit note, summarize, ask, read aloud |
| 7 | Semantic search | Meaning-based search over your notes |
| 8 | Ask AI | RAG chat with citations + on-device/cloud badge |
| 9 | AI summary sheet | On-device generated summary |
| 10 | Settings | AI mode, model status, privacy controls |
| 11 | **AI Lab** | Benches, bakeoffs, RAG traces, eval dashboard |

---

## 🏛 Architecture

Recall follows **clean, unidirectional, multi-module architecture**:

```
UI (Compose)  →  ViewModel (StateFlow)  →  UseCase  →  Repository  →  AI / Remote / DB
```

Principles:

- **Compose + Material 3** for all UI — no XML View screens.
- **Unidirectional data flow**: ViewModels expose immutable `StateFlow<UiState>`; UI is stateless.
- **Inference never on the main thread** — `Dispatchers.Default`/`IO` or `WorkManager`.
- **Repositories are the only gateway** to data; features never touch `:data:remote` or `:data:ai` directly.
- **`AnswerRouter`** owns the on-device-vs-cloud decision — no feature hardcodes "call the cloud".
- **Domain models are pure Kotlin** (`:core:model` has no Android dependencies).

---

## 🗂 Module graph

```mermaid
graph TD
    app[":app"] --> capture[":feature:capture"]
    app --> library[":feature:library"]
    app --> ask[":feature:ask"]
    app --> lab[":feature:lab"]

    capture --> repository[":data:repository"]
    library --> repository
    ask --> repository
    lab --> repository

    repository --> ai[":data:ai"]
    repository --> remote[":data:remote"]
    repository --> database[":core:database"]

    capture --> designsystem[":core:designsystem"]
    library --> designsystem
    ask --> designsystem
    lab --> designsystem

    repository --> model[":core:model"]
    ai --> model
    remote --> model
    database --> model
```

| Module | Responsibility |
|--------|----------------|
| `:app` | Navigation, DI wiring, theme, app entry point |
| `:core:model` | Pure-JVM domain models (no framework deps) |
| `:core:designsystem` | Material 3 theme, tokens, shared composables |
| `:core:database` | Room entities, DAOs, vector store, encryption |
| `:data:ai` | On-device inference: OCR, STT, embeddings, LiteRT, on-device LLM |
| `:data:remote` | Free-tier cloud LLM client (Gemini) + streaming |
| `:data:repository` | Repositories, `AnswerRouter`, RAG assembly |
| `:feature:capture` | Camera / OCR / voice / text / PDF capture |
| `:feature:library` | Notes list + semantic search |
| `:feature:ask` | RAG chat + agent loop UI |
| `:feature:lab` | AI Lab: benches, bakeoffs, traces, evals |

---

## 📥 Capture & ingest pipeline

Every input type is normalized into a **searchable, embeddable note** — mostly on-device.

```mermaid
flowchart LR
    subgraph Inputs
        IMG[📷 Image]
        VOICE[🎙️ Voice]
        TXT[⌨️ Text]
        PDF[📄 PDF]
    end

    IMG -->|ML Kit OCR| RAW[Extracted text]
    VOICE -->|On-device STT| RAW
    TXT --> RAW
    PDF -->|Text + scanned OCR| RAW

    RAW --> CLEAN[Clean & normalize]
    CLEAN --> CHUNK[Chunk]
    CHUNK --> TAG[LiteRT auto-tag]
    CHUNK --> EMB[MediaPipe embeddings]
    TAG --> STORE[(Room + Vector store)]
    EMB --> STORE
    STORE --> IDX[Indexed & searchable]

    classDef ondevice fill:#00C85322,stroke:#00C853,color:#0b3d1a;
    class IMG,VOICE,TXT,PDF,RAW,CLEAN,CHUNK,TAG,EMB,STORE,IDX ondevice;
```

> 🟢 Green = runs **fully on-device**. The entire ingest path works offline with zero cloud calls.

---

## 📱 On-device AI stack

How the on-device intelligence is layered, from hardware up to features:

```mermaid
flowchart TB
    subgraph FEAT[Feature layer]
        F1[Capture] --- F2[Semantic Search] --- F3[Ask / RAG] --- F4[Summaries]
    end
    subgraph TASK[Task APIs / SDKs]
        T1[ML Kit\nOCR · Vision]
        T2[MediaPipe\nText embeddings]
        T3[Gemini Nano\non-device LLM]
    end
    subgraph RT[Runtime]
        R1[LiteRT / TFLite Interpreter]
        R2[Delegates: NNAPI · GPU · CPU]
    end
    subgraph HW[Hardware acceleration]
        H1[CPU] --- H2[GPU] --- H3[NPU / DSP]
    end

    FEAT --> TASK --> RT --> HW

    classDef ondevice fill:#00C85322,stroke:#00C853,color:#0b3d1a;
    class F1,F2,F3,F4,T1,T2,T3,R1,R2,H1,H2,H3 ondevice;
```

| Layer | What it does | Tech |
|-------|--------------|------|
| Feature | User-facing intelligence | Compose screens |
| Task APIs | High-level ready models | ML Kit, MediaPipe, Gemini Nano |
| Runtime | Executes custom models | LiteRT (TFLite) + delegates |
| Hardware | Accelerates inference | CPU / GPU / NPU |

---

## 🤖 The intelligence layer (what makes it smart)

Recall is "smart" because intelligence is layered, measurable, and routed — not a single API call.

```mermaid
flowchart LR
    subgraph Capture
        OCR[On-device OCR]
        STT[On-device STT]
        TAG[LiteRT auto-tagging]
    end
    subgraph Understand
        EMB[On-device embeddings]
        VDB[(Vector store)]
    end
    subgraph Reason
        RET[Hybrid retrieval]
        ROUTE{AnswerRouter}
        LOCAL[On-device LLM]
        CLOUD[Free-tier cloud LLM]
        AGENT[Agent tool loop]
    end
    OCR --> EMB
    STT --> EMB
    TAG --> VDB
    EMB --> VDB
    VDB --> RET --> ROUTE
    ROUTE -->|simple / offline / private| LOCAL
    ROUTE -->|complex / long context| CLOUD
    ROUTE --> AGENT
```

Capabilities that make it intelligent:

- **Multimodal capture** — text extracted from images (OCR) and speech (STT), all on-device.
- **Semantic understanding** — every note is embedded into a vector for meaning-based retrieval.
- **Grounded answers** — the model only answers from _your_ notes, and cites its sources.
- **Agentic reasoning** — for complex asks, a multi-step agent can call tools (filter, open note, create reminder) and re-retrieve mid-loop.
- **Self-measuring** — an evaluation harness proves retrieval and answer quality instead of guessing.

---

## 🔗 RAG pipeline

Every "Ask my notes" query runs the same grounded pipeline:

```mermaid
sequenceDiagram
    participant U as User
    participant VM as AskViewModel
    participant R as Repository
    participant E as Embedder (on-device)
    participant V as Vector + BM25
    participant Rt as AnswerRouter
    participant L as LLM (local/cloud)

    U->>VM: question
    VM->>R: ask(question)
    R->>E: embed(query)
    E-->>R: query vector
    R->>V: hybrid retrieve top-k
    V-->>R: relevant chunks (+ provenance)
    R->>Rt: choose engine
    Rt-->>R: on-device OR free-tier cloud
    R->>L: prompt + grounded context
    L-->>VM: streamed answer + citations
    VM-->>U: render (cancelable, cited)
```

1. **Embed** the query on-device.
2. **Retrieve** top-k with hybrid **BM25 + vector** search (with tag/time filters).
3. **Assemble** a grounded context preserving chunk → note/page provenance.
4. **Route** to on-device or free-tier cloud via `AnswerRouter`.
5. **Stream** the answer with **structured citations** and cancel-in-flight support.
6. **Log** route/latency/cost and **evaluate** groundedness + recall@k.

---

## 🧬 RAG variants

Recall implements four flavors of RAG, each exercising a different engineering skill:

```mermaid
flowchart TD
    Q[User question] --> TYPE{What kind?}
    TYPE -->|offline / private| OFF[Offline RAG\nlocal embed + local LLM]
    TYPE -->|complex reasoning| CLD[Cloud RAG\nlocal retrieve + cloud LLM]
    TYPE -->|about a document| DOC[Doc RAG\nPDF chunk + cite pages]
    TYPE -->|needs actions| AG[Agent RAG\nre-retrieve mid tool-loop]

    OFF --> ANS[Grounded, cited answer]
    CLD --> ANS
    DOC --> ANS
    AG --> ANS
```

| Variant | Retrieval | Generation | Highlights |
|---------|-----------|------------|-----------|
| **Offline RAG** | on-device | on-device LLM | Works in airplane mode, fully private |
| **Cloud RAG** | on-device | free-tier cloud | Heavy reasoning, long context |
| **Doc RAG** | PDF chunks | local/cloud | Page-level citations for documents |
| **Agent RAG** | re-retrieved | local/cloud | Re-queries knowledge inside the tool loop |

---

## 🛠 Agent tool loop

For complex asks, a multi-step agent **plans → calls a tool → observes → repeats** until it can answer:

```mermaid
stateDiagram-v2
    [*] --> Plan
    Plan --> SelectTool: decide next step
    SelectTool --> Retrieve: search notes
    SelectTool --> OpenNote: read a note
    SelectTool --> Filter: filter by tag/time
    SelectTool --> Reminder: create reminder
    Retrieve --> Observe
    OpenNote --> Observe
    Filter --> Observe
    Reminder --> Observe
    Observe --> Plan: need more?
    Observe --> Answer: enough context
    Answer --> [*]
```

Every step is recorded as a **trace** (tool, input, output, latency) and shown in the **AI Lab**,
with guardrails that block unsafe tool calls.

---

## 🧭 On-device vs cloud routing

`AnswerRouter` (in `:data:repository`) decides per query:

| Prefer **on-device** when… | Escalate to **free-tier cloud** when… |
|----------------------------|----------------------------------------|
| Offline / airplane mode | Complex multi-note reasoning |
| Short / simple query | Long context windows |
| Privacy-sensitive content | Hard multimodal understanding |
| Low latency required | Multi-step agent planning |

Every decision is logged with measured latency and cost, and surfaced in the **AI Lab**.

> 💡 **Budget rule:** cloud is only used when the device genuinely cannot do the job, and only
> with free-tier models. No paid subscription is required to run Recall.

---

## 📦 Model lifecycle

On-device models are versioned, downloaded on demand, and benchmarked before they ship to users:

```mermaid
flowchart LR
    REG[Model registry\nname · version · hash] --> CHK{On device?}
    CHK -->|no| DL[Download\nWorkManager]
    CHK -->|yes| VERIFY[Verify hash]
    DL --> VERIFY
    VERIFY --> BAKE[Quantization bakeoff\nfp32 vs int8 vs int4]
    BAKE --> PICK[Pick best\nsize · latency · accuracy]
    PICK --> LOAD[Load into LiteRT]
    LOAD --> SERVE[Serve inference]
    SERVE --> METRICS[Log latency / memory\n→ AI Lab]
```

The **quantization bakeoff** compares model variants on **size vs latency vs accuracy** and records
the tradeoff so the choice is defensible, not arbitrary.

---

## 🔐 Privacy & PII redaction flow

Nothing leaves the device without passing through the privacy gate:

```mermaid
flowchart LR
    NOTE[Note content] --> ROUTE{AnswerRouter}
    ROUTE -->|on-device| LOCAL[Local LLM\nraw content OK]
    ROUTE -->|cloud needed| PII[PII detection]
    PII --> REDACT[Redact emails / phones / names / IDs]
    REDACT --> SANITIZE[Strip injection patterns]
    SANITIZE --> SEND[Send minimal context to cloud]
    SEND --> RESP[Response]
    RESP --> REHYDRATE[Re-insert local references]
    LOCAL --> OUT[Answer]
    REHYDRATE --> OUT

    classDef safe fill:#1DE9B622,stroke:#1DE9B6,color:#064e40;
    class LOCAL,PII,REDACT,SANITIZE safe;
```

Layers: **encryption at rest** → **PII redaction** before cloud → **prompt-injection sanitization**
of retrieved content → **export/delete** controls in Settings.

---

## 📊 Evaluation harness

Quality is proven with numbers, not vibes. Each capability runs against a **golden set** and gates CI:

```mermaid
flowchart LR
    GOLD[(Golden datasets)] --> RUN[Run pipeline]
    RUN --> M1[OCR: CER / WER]
    RUN --> M2[Retrieval: recall@k · MRR]
    RUN --> M3[Answers: groundedness · citation accuracy]
    RUN --> M4[Agents: task success · unsafe-call rate]
    M1 --> GATE{Meets threshold?}
    M2 --> GATE
    M3 --> GATE
    M4 --> GATE
    GATE -->|yes| PASS[✅ Merge / ship]
    GATE -->|no| FAIL[❌ Block + surface in AI Lab]
```

---

## 🛠 Tech stack

All versions are centralized in the **Gradle Version Catalog** ([`gradle/libs.versions.toml`](gradle/libs.versions.toml)).

### Core
- **Kotlin** `2.2.10` · **AGP** `9.3.1` · **Coroutines / Flow**
- **Jetpack Compose** (BOM `2026.02.01`) · **Material 3** · **Navigation Compose**
- **Hilt** (DI) · **Room** (DB) · **DataStore** (settings) · **WorkManager** (background) · **Coil** (images)

### On-device AI
- **CameraX** — camera pipeline
- **ML Kit Text Recognition** — OCR
- **ML Kit Image Labeling** — vision tagging
- **LiteRT (TensorFlow Lite)** + **GPU delegate** — custom model inference
- **MediaPipe Tasks (Text)** — on-device embeddings
- On-device LLM — summaries (Gemini Nano / MediaPipe LLM)

### Cloud AI (free-tier)
- **Google Generative AI (Gemini)** — heavy reasoning / multimodal
- **Retrofit + OkHttp + Kotlinx Serialization** — networking & structured output

### Quality
- **JUnit** · **Truth** · **Compose UI Test** · **Espresso**

---

## 📁 Project structure

```
Recall/
├── app/                      # Application module (entry point, navigation, DI graph)
├── core/
│   ├── model/                # Pure-JVM domain models
│   ├── designsystem/         # Material 3 theme + shared composables
│   └── database/             # Room + vector store + encryption
├── data/
│   ├── ai/                   # On-device inference (OCR, STT, embeddings, LiteRT, LLM)
│   ├── remote/               # Free-tier Gemini client + streaming
│   └── repository/           # Repositories + AnswerRouter + RAG assembly
├── feature/
│   ├── capture/              # Camera / OCR / voice / text / PDF capture
│   ├── library/              # Notes list + semantic search
│   ├── ask/                  # RAG chat + agent loop
│   └── lab/                  # AI Lab (benches, bakeoffs, traces, evals)
├── gradle/
│   └── libs.versions.toml    # Single source of truth for versions
├── build.gradle.kts          # Root build config
└── settings.gradle.kts       # Module includes
```

---

## 🚀 Getting started

### Prerequisites

- **Android Studio** (latest stable, with AGP 9.x support)
- **JDK 11+**
- An Android device or emulator running **Android 7.0 (API 24)** or higher
- _(Optional)_ A free **Gemini API key** for cloud RAG features

### Clone

```bash
git clone <your-repo-url> Recall
cd Recall
```

---

## ⚙️ Configuration

Cloud features are optional. To enable free-tier Gemini, add your key to `local.properties`
(this file is **git-ignored** — never commit secrets):

```properties
# local.properties
GEMINI_API_KEY=your_free_tier_key_here
```

It is then exposed to the app via `BuildConfig` (wired in the app module). Without a key,
Recall still runs fully **on-device** (capture, OCR, STT, embeddings, semantic search, on-device summaries).

---

## 🔨 Build & run

```bash
# Build a debug APK
./gradlew :app:assembleDebug

# Install on a connected device/emulator
./gradlew :app:installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (device/emulator required)
./gradlew connectedAndroidTest

# Full check (lint + tests)
./gradlew check
```

The debug APK is produced at:

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Quality & evaluation

Recall treats AI quality as a **first-class, measurable** concern. A feature is not "done"
until it has a **working implementation + a decision note + an evaluation metric**.

| Capability | Metric |
|------------|--------|
| OCR | Character/Word Error Rate (CER/WER) |
| Retrieval | `recall@k`, MRR |
| Answers | Groundedness, citation accuracy |
| Agents | Task success rate, unsafe-tool-call rate |

Golden datasets and evaluation gates are exposed in the **AI Lab** and wired toward CI.

---

## 🔐 Privacy & security

- **On-device first** — your notes never leave the device unless a cloud call is explicitly needed.
- **Encryption at rest** — note text, transcripts, and embeddings are encrypted.
- **PII redaction** — sensitive content is redacted before any cloud request.
- **Prompt-injection defense** — retrieved note content is sanitized before prompting.
- **You own your data** — export or delete everything from Settings.
- **No secrets in VCS** — API keys live in `local.properties` / `BuildConfig` only.

---

## 🗺 Roadmap

Built depth-first — each phase must be solid (with evals) before the next.

- [x] **Phase 0** — Multi-module scaffold + version catalog + build green
- [ ] **Phase 1** — Capture + on-device OCR + Room + Library
- [ ] **Phase 2** — Voice transcription + auto-tagging + embeddings + semantic search
- [ ] **Phase 3** — RAG "Ask my notes" (offline + cloud, streaming, citations)
- [ ] **Phase 4** — On-device summaries + `AnswerRouter` + eval harness
- [ ] **Phase 5** — PDF / Doc RAG (text + scanned OCR → chunk → cite)
- [ ] **Phase 6** — Vision beyond OCR (routed on-device/cloud)
- [ ] **Phase 7** — Multi-step agent (routed) + Agent RAG
- [ ] **Phase 8** — On-device neural TTS
- [ ] **Phase 9** — Personalization (feedback + on-device adapted classifier)
- [ ] **Continuous** — AI Lab, App Function export, feature flags

---

## 🎓 Competency map (learning goals)

Recall is designed so every senior **Mobile AI engineering** skill is exercised by a real product need:

| Pillar | Covered by |
|--------|------------|
| Capture multimodal | OCR, vision, STT, PDF, TTS |
| On-device ML | LiteRT, delegates, quantization bakeoff, model registry |
| Retrieval / RAG core | Embeddings, vector store, hybrid BM25+vector, chunking, rerank |
| Generation | On-device + cloud LLM, streaming, structured citations |
| Agents | Multi-step tool loop, traces, routed planner |
| Routing & systems | `AnswerRouter`, offline degradation, timeouts, telemetry |
| Quality | Golden sets + CI gates for OCR/retrieval/answers/agents |
| Privacy / security | Encryption, PII redaction, prompt-injection defense |
| Platform glue | WorkManager, App Functions, feature flags |

---

## 🤝 Contributing

This is primarily a personal learning project, but suggestions and issues are welcome.

1. Keep the architecture rules (Compose + Hilt, unidirectional flow, repository gateway).
2. Add versions only via `gradle/libs.versions.toml`.
3. Every AI feature ships with a decision note and an evaluation metric.

---

## 📄 License

TBD — add a `LICENSE` file to define usage terms.

---

<div align="center">

**Recall** — your knowledge, on your device, actually intelligent.

</div>
