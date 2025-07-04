# 🎯 Jackaroo – JavaFX Single-Player Card Board Game

Jackaroo is a strategic multiplayer board game inspired by the traditional Middle Eastern game "Tawla" and similar to games like Ludo or Parcheesi – but with a modern card-based twist! This JavaFX version is a **single-player game against CPU opponents**, designed with a focus on strategy, turn-based logic, and object-oriented programming.

---

## 🕹️ Game Description

**Objective**:  
Be the first player to move all your colored marbles from your home base, around the track, through the safe zone, and into the finish zone.

**Gameplay Rules**:
- Each player starts with 4 marbles.
- Movement is determined by playing cards from a hand of 5.
- Certain cards (like 7s or Jacks) have special abilities like splitting moves or swapping marbles.
- Trap cells on the board send marbles back to home.
- Players take turns, and CPU opponents play automatically using simple logic.
- The game ends when one player gets all 4 marbles into their safe zone.

---

## ⚙️ How I Built It

### 🧠 Programming Concepts Used
- **Java (OOP Principles)**:  
  Full use of encapsulation, inheritance, interfaces, and abstraction.
  
- **JavaFX**:  
  For designing the user interface, scenes, transitions, and animations.

- **MVC-like Design**:  
  - `Game`, `Player`, `Board`, `Card`, and `Marble` handle game logic  
  - `GameView` and `StartView` build the GUI  
  - `Main` connects the views to the logic

- **Custom Exception Handling**:  
  To manage illegal actions like playing the wrong card or selecting invalid marbles.

- **CSV File Handling**:  
  Loads card information dynamically using a `Cards.csv`.

---

### 🛠️ Features Implemented

- ✅ Card drawing, selecting, and playing
- ✅ Special cards (Split, Swap, Burn, etc.)
- ✅ Animated CPU players using delays
- ✅ Trap alert popups and game-win screens
- ✅ Color-coded UI and labels for clarity
- ✅ Clean and responsive interface with custom backgrounds and animations

---

## 🧩 Dependencies & Tools
- Java 8+
- JavaFX 

---

## 📸 Link

Here is a Link for me playing the game and some screenshots!! 
https://drive.google.com/drive/folders/1_JDAMEXXBkvGb8_j57H6xA9_DXzSJ9cM?usp=drive_link


---

## 🧑‍💻 Author

**Ahmed Karam**  
Computer Engineering Student – German University in Cairo (GUC)  
Spring 2025 | `CSEN 401 – Computer Programming Lab`

---

