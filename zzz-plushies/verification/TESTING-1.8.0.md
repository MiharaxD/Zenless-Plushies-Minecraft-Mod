# 1.8.0 verification

- The new Marion recording was converted to streaming Vorbis. Decoded audio from 0–9 seconds correlates 0.9980 with the supplied MP3, confirming that it starts at the recording's beginning.
- All 13 A-rank IDs are distinct and have item models and blockstates in the bundled resources. The rank list is recorded in `A_RANK_SOURCES.md`.
- Java 17 / Forge 1.20.1 client compiled and ran in the development world. The odds and timing boundary check passed, including the exact 160-tick / 8-second lock time.
- A ten-pull batch was still flickering at about 7.1 seconds and showed its final ranks by about 8.5 seconds. The result stayed on screen until clicked.
- A batch with two S results gave each S its own focus before the full reward grid.
- A later ten-pull batch with A/B only used the new A/B sound event; the game log contained no missing gacha sound warnings.
- A ten-pull batch awarded a Pan Yinhu plush in a purple A result. Pity advanced from 15 to 25 without resetting, and ten tapes were consumed. The crowded test inventory meant overflow prizes were dropped by the existing inventory handling; the reward card confirmed the server result.
- Subjective speaker output and multiplayer synchronization were not independently measured.
