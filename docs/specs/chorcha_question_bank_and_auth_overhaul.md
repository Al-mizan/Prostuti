# Specification: Google Sign-In, Responsive UI, Splash/Skeleton, and Chorcha-Style Question Bank Overhaul

**Status:** Ready for Implementation  
**Originating Prompt:** User request for Google Sign-In, responsive layout fixes, animated splash/skeleton states, and Chorcha.net-style Question Bank (Subject-wise, 15–30 day Live Model Test, BCS series archive 50th–10th, and Start Exam / View Questions action modal).

---

## 1. Problem Statement

1. **Authentication:** Currently, the application only supports email/password authentication. Users expect modern one-tap "Sign in with Google" via Android Credential Manager, verified securely on the embedded Ktor backend without requiring a password.
2. **UI Responsiveness:** The authentication screens and certain action buttons lack keyboard insets (`imePadding`) and scroll support, causing layout overflow and clipped/unclickable buttons on small or landscape screens. Button loading indicators jump or alter height.
3. **App Launch & Loading Experience:** The current splash screen is a single instantaneous spinner. Loading screens across Question Bank and Practice rely on raw circular spinners rather than modern shimmer skeleton placeholders.
4. **Question Bank UX Disconnect:** The existing Question Bank is a single flat list with dropdown filters. In contrast, the proven Chorcha.net layout (as detailed in `.scratch/bcs_exam_list.json` and reference assets) organizes BCS exam prep into:
   - **বিষয় ভিত্তিক (Subject-wise):** Thematic grid of subjects with large typography watermarks and rich gradient cards.
   - **লাইভ মডেল টেস্ট (Live Model Test):** A 15–30 day featured mock test window with 1-attempt constraint.
   - **প্রতিষ্ঠান ভিত্তিক (Institute-based):** BCS Preliminary archive (50th down to 10th BCS).
   - **Exam Action Modal:** Detail bottom sheet with time, question count, full marks, negative marking, and dual CTAs: **"পরীক্ষা শুরু করুন" (Start Exam)** and **"প্রশ্নগুলো দেখুন" (View Questions)**.

---

## 2. Proposed Solution & Architecture

### 2.1 Google Sign-In Vertical Slice
- **Client (`androidApp` & `feature/auth`):**
  - Integrate Android Credential Manager (`androidx.credentials`) using `GetGoogleIdOption` configured with `WEB_GOOGLE_CLIENT_ID`.
  - Add branded "গুগল দিয়ে সাইন ইন করুন" (Continue with Google) button to `AuthScreen`.
  - Transmit retrieved Google ID token to `POST /api/v1/auth/google`.
- **Server (`server`):**
  - `GoogleAuthVerifier` verifies token signature, expiry, and audience using Google API Client against `WEB_GOOGLE_CLIENT_ID`.
  - Update `UsersTable`: make `password_hash` nullable; add unique `google_id`.
  - `AuthService.loginWithGoogle(idToken)`: finds user by Google sub or email; inserts new student or updates existing, returning a Prostuti JWT session.

### 2.2 Responsive Layout & Button Touch-Target Fixes
- Add `Modifier.verticalScroll(rememberScrollState())` and `Modifier.imePadding()` to `AuthScreens.kt`.
- Enforce standard `48.dp` minimum touch target height in `ProstutiButton` and constrain `CircularProgressIndicator` to `20.dp` to prevent button height distortion during loading.

### 2.3 Deluxe Splash Screen & Skeleton Shimmer Loaders
- **Splash Screen (`SplashScreen.kt`):** Full-screen branded experience with Crimson Red gradient, animated mascot entrance (fade + scale), Bengali app typography, and a minimum 600ms display timer to eliminate visual jarring.
- **Shimmer System (`core/designsystem`):**
  - `Modifier.shimmerEffect()`: Infinite animated linear gradient shimmer.
  - Reusable skeletons: `QuestionCardSkeleton`, `SubjectGridSkeleton`, `BcsSessionListSkeleton`.
  - Replace raw spinners in `QuestionBankScreen` and `PracticeScreen` with skeleton placeholders.

### 2.4 Chorcha-Style Question Bank Overhaul
- **Architecture:** Multi-state navigation within `feature/questionbank`:
  1. `HOME`:
     - **লাইভ মডেল টেস্ট (Live Model Test):** Banner card displaying active featured test, countdown timer ("১৫ দিন বাকি"), and "১ বার অংশগ্রহণ যোগ্য" badge.
     - **প্রতিষ্ঠান ভিত্তিক (Institute-based):** Dedicated "বিসিএস প্রিলিমিনারি" card with shield icon, "১০ম - ৫০তম বিসিএস", and "৪০+ পরীক্ষা" badge.
     - **বিষয় ভিত্তিক (Subject-wise):** 9 BCS subject cards matching reference picture aesthetic (vibrant gradients, large Bangla watermark characters `অ`, `আ`, `A`, `a`, etc., subject badge). Tapping opens study questions filtered by that subject.
  2. `BCS_SESSIONS`:
     - Full catalog of 50th down to 10th BCS (sourced from `.scratch/bcs_exam_list.json`).
     - Display title, duration badge (২ ঘন্টা / ১ ঘন্টা), question count badge (২০০ টি / ১০০ টি প্রশ্ন).
     - Tapping any session opens the Exam Action Modal.
  3. `EXAM_ACTION_MODAL`:
     - Bottom sheet showing: ⏱ সময় (১২০ মিনিট), ❓ প্রশ্ন (২০০ টি), 🎯 পূর্ণমান (২০০), ⚠️ নেগেটিভ মার্কিং (০.৫ নম্বর).
     - Action 1: **"পরীক্ষা শুরু করুন" (Start Exam)** -> triggers callback to launch timed Exam Mode in `feature/exam`.
     - Action 2: **"প্রশ্নগুলো দেখুন" (View Questions)** -> enters study question view with solutions and explanations.
  4. `STUDY_VIEW`:
     - Questions list with options, correct answer indicator, collapsible Bengali explanation, and pagination.

---

## 3. Test Seams & Verification Strategy

| Layer | Seam / Interface | Verification Strategy |
|---|---|---|
| **Server Auth** | `GoogleAuthVerifier` & `AuthService.loginWithGoogle` | Unit test with mocked Google ID token; test existing vs new user upsert; test invalid audience rejection |
| **Server Question Bank** | `QuestionBankService.getBcsSessions()` | Unit test verifying enriched session metadata (duration, questions count, marks) |
| **Client Auth Repository** | `AuthRepository.loginWithGoogle(idToken)` | Mock `AuthApi` verifying `Result.Success` on valid token and session store persistence |
| **UI Components** | `QuestionBankScreen`, `BcsSessionsScreen`, `AuthScreen` | Compose screenshot / preview tests; verify keyboard scrolling and modal interaction |

---

## 4. Acceptance Criteria

- [ ] Users can sign in using Google ID tokens verified on the Ktor backend; `password_hash` in PostgreSQL allows null values.
- [ ] Login and Register screens do not clip or break when the software keyboard opens on small screens.
- [ ] Buttons maintain consistent `48.dp` height without layout shift when toggling loading state.
- [ ] App launches with an animated branded splash screen; Question Bank displays shimmer skeletons while data loads.
- [ ] Question Bank landing shows: (1) Live Model Test, (2) Institute BCS Preliminary card, (3) 9 Subject cards matching the reference image.
- [ ] Tapping BCS Preliminary shows the series archive (50th down to 10th BCS).
- [ ] Tapping any BCS session opens the action modal with exam details and two distinct buttons: "পরীক্ষা শুরু করুন" (launches Exam) and "প্রশ্নগুলো দেখুন" (launches Study mode).
