# 1.7.0 verification

- Java 17 / Forge 1.20.1 client compiled and ran in the existing development world.
- Standalone GachaOddsCheck passed: base odds, first soft-pity pull, monotonic increase, exact A/B boundaries and guaranteed 80th pull.
- Ten-pull A/B batch consumed 10 tapes (56 -> 46), raised pity 4 -> 14, held the rank grid beyond the former timeout, and revealed ten matching item cards only after confirmation.
- For the guarantee test, the development world's saved pity was temporarily set to 79 while the world was closed. Original save files were backed up in build/test-backups/1.7.0-pre-guarantee.
- Ten-pull batch with first-pull S consumed 10 tapes (46 -> 36), showed the S focus on confirmation, and then showed the ten reward cards. Final pity was 9, confirming that the remaining nine rolls counted after the S reset.
- The test S lost the target 50/50 for the second time; saved inventory contained one choice_token and the awarded Roxy plush, and the loss counter reset to zero.
- An additional individual B pull consumed one tape (36 -> 35), waited for confirmation, and revealed four raw iron.
- New sound events loaded without missing gacha sound errors. Subjective audio mix/loudness was not independently assessed. Roxy has no available agent voice in the existing voice catalog.
- Multiplayer and Lootr/LootJS integration were not retested in this release.
