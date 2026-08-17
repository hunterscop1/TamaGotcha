# TamaTask - AI Tamagotchi Productivity Companion

**TamaTask** is a retro-inspired virtual pet productivity companion built with **Kotlin** and **Jetpack Compose**. It blends the nostalgic charm of classic 90s handheld LCD virtual pets with Google Tasks & Calendar synchronization and a dynamic AI dialogue engine powered by Gemini.

---

## Key Features

### 1. Retro Virtual Pet Hardware Frame & LCD Canvas
- **Pixel Art Canvas**: Dynamic procedural pixel rendering for pet species (Cat, Dog, Dragon, Bunny, Robot) across 4 growth stages (Egg, Baby, Teen, Master).
- **Physical 3-Button Controls**:
  - **Button A (Care & Kitchen)**: Feed nutritious treats (Sushi, Onigiri, Matcha, Ramen), pet, sleep, and change themes.
  - **Button B (AI Voice Chat)**: Real-time interactive conversation with speech synthesis.
  - **Button C (Calendar & Tasks)**: View, manage, and sync Google Tasks and deadlines.
- **Hardware Status Bar**: Real-time mood indicators, level, EXP, coins, and urgent deadline warnings.

### 2. Google Calendar & Google Tasks Integration
- **Two-Way Synchronization**: Track tasks, deadlines, and schedule milestones directly within the app.
- **Proactive Deadline Alerts**: Your pet detects upcoming deadlines and provides urgent reminders, coaching you through crunch periods.
- **EXP & Coin Rewards**: Completing tasks awards EXP toward pet evolutions and coins for the boutique shop.

### 3. Gemini-Powered AI Dialogue & Voice
- **Personality Archetypes**: Select from multiple pet personalities (Cheerful, Tsundere, Curious, Shy, Scholar, Zen Sage).
- **Context-Aware Dialogue**: The pet responds based on current satiety, energy, happiness, and pending deadlines.
- **Cute Character TTS**: High-pitched custom voice synthesis reads your pet's thoughts and messages out loud.

### 4. Productivity Arcade Mini-Games
- **⚡ Deadline Rush**: Fast-paced reflex game defending against urgent deadlines before time expires.
- **📂 Eisenhower Task Sorter**: Quick sorting sprint categorizing tasks into Urgent vs. Distractions.
- **🧠 Schedule Memory**: Event pair-matching challenge that sharpens recall of your daily milestones.

### 5. Tama Boutique & Customization
- **Cosmetics**: Equip hats (Wizard, Crown, Headset, Sakura Blossom), accessories (Specs, Boba, Latte, Sword), pixel palette tints, and LCD room environments.
- **Mood & Vitals Lab**: Interactive slider controls to inspect and adjust hunger, happiness, energy, and focus in real-time.
- **8-Bit Audio Synthesizer**: Custom PCM audio generation providing retro chimes, level-up fanfares, and alerts.

---

## Tech Stack & Architecture

- **UI Framework**: Jetpack Compose & Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) with Clean StateFlow pipelines
- **Local Database**: Android Room for offline persistence of pet status, tasks, cosmetics, and chat history
- **Sound Engine**: Custom non-blocking PCM `AudioTrack` 8-bit sound synthesizer & Android `TextToSpeech`
- **Network / AI**: Gemini API integration with local fallback engines

---

## Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                # Application entry point and modal sheet host
├── api/                           # Gemini AI & Google Calendar API interfaces
├── audio/                         # 8-bit Audio synthesizer & TTS voice engine
├── data/
│   ├── local/                     # Room Database entities & DAOs (Pet, Tasks, Chat)
│   ├── model/                     # Species, Personalities, Cosmetics, Mini-games models
│   └── repository/                # PetRepository managing unified offline/online state
├── ui/
│   ├── care/                      # Feeding, grooming, and shell theme customizer
│   ├── device/                    # Authentically styled handheld Tamagotchi frame
│   ├── dialogs/                   # Mood & Vitals adjustment lab
│   ├── dialogue/                  # AI dialogue speech bubbles & full chat dialog
│   ├── games/                     # Arcade mini-games (Deadline Rush, Sorter, Memory)
│   ├── pet/                       # Procedural pixel canvas renderer & sprite animations
│   ├── store/                     # Tama Boutique store & cosmetic wardrobe
│   ├── tasks/                     # Task manager and Google Calendar sync sheet
│   └── theme/                     # Material 3 Color Schemes & Typography
└── viewmodel/
    └── TamaViewModel.kt           # Central ViewModel coordinating UI and background state
```

---

## Getting Started

1. **Prerequisites**: Android SDK 34+ and modern Gradle toolchain.
2. **Build**: Run `./gradlew assembleDebug` or use the AI Studio preview streaming emulator.
3. **Run Tests**: Execute `gradle :app:testDebugUnitTest` to verify test suites.
