# 🚗 RoadEvader: The "Best Game Ever"

Welcome to **RoadEvader**, a dynamic avoidance game developed as part of the Advanced Android UI Development course. This project focuses on managing game states, utilizing modern UI components, and delivering an intuitive user experience.

## What is the game about?
The player's goal is to survive on a busy road for as long as possible by dodging obstacles and collecting coins. The game tracks the highest scores and presents them on a dedicated leaderboard screen, where users can also see the exact geographical location (via GPS/Google Maps) where each high score was achieved.

## Project Highlights
* **Screens & Gameplay:** Created an intuitive Main Menu with 4 gameplay modes:
    1. Slow-paced gameplay (buttons control).
    2. Fast-paced gameplay (buttons control).
    3. Gyroscope-based gameplay (device tilt).
    4. **Scoreboard & Maps:** A split-screen view showing high scores alongside a Google Maps integration that marks the physical location of each achievement.
* **Fragments Architecture:** I decoupled the leaderboard list and the Google Maps interface into separate Fragments, ensuring smooth transitions and efficient resource management.
* **Data Persistence:** Implemented a robust system to save and display high scores (based on distance, with the potential to be easily modified for coin count).
* **UI/UX:** Focused on a clean, mobile-optimized experience, complete with a custom-designed app icon.

## Technologies Used
* **Language:** Kotlin.
* **Architecture:** Utilized Fragments to manage multiple views instead of switching between numerous Activities, making the application lighter, faster, and more modular.
* **Google Maps API:** Integrated a live map to visualize high-score locations, adding an extra layer of engagement.
* **Version Control:** The project is organized and managed using Git, ensuring a clean folder structure and history.

## Why this approach?
My decision to use a Fragment-based architecture was driven by the need for **scalability** and **code maintainability**. My goal was to create a game that feels like a polished, real-world product, focusing on smooth performance (even under the complexity of matrix-based rendering) and clean, readable code.

---

### Note to the Professor:
The game is configured to run on all standard Android devices (API compatible). To test the leaderboard, simply play a round; the score and location will automatically update in the scoreboard view.
