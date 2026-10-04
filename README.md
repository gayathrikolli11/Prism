# Prism

**An Android app whose entire interface is a function of the user's behavior.**

Most apps personalize what you see. Prism personalizes what the app *is*. Click on sports and the layout, color theme, hero section, content source, and ads all rebuild themselves around sports. Switch to food and it transforms again. No settings screen. No onboarding quiz. Just behavior.

<!-- ![Prism demo](docs/demo.gif) -->

https://github.com/user-attachments/assets/e35d0034-c4e8-498a-a34f-e057475fe202



## The idea

One behavioral signal drives everything on screen.

The same interest score that decides which content you see also decides which ads are served, so content and monetization are never out of sync. That alignment between what a user cares about and what gets placed in front of them is the core of how personalized content platforms work, and Prism is a native Android implementation of that loop, from signal to scoring to UI to ad.

## What it does

- **Learns from behavior:** tracks clicks, dwell time, shares, and dismissals, with no explicit input
- **Scores with recency:** exponential decay keeps the profile current instead of frozen in the past
- **Rebuilds the UI:** layout, theme, hero, and content source switch per dominant interest, each with its own native-feeling design
- **Targets ads contextually:** ad category follows the same signal as content
- **Extends to the home screen:** a Jetpack Glance widget reflects your current interest profile without you touching anything
- **Never locks you in:** a share of the feed always comes from other interests, so discovery continues

## How it works

```
User interacts -> BehaviorTracker -> UserInterestRepository (scoring + decay)
      -> dominantInterest (StateFlow) -> FeedViewModel (flatMapLatest)
      -> FeedScreen renders a different layout per interest
```

Two layers work together: an in-memory session layer reacts instantly to what you're doing now, while a persistent layer (Room + DataStore) holds longer-term scores. `flatMapLatest` cancels in-flight requests when interest shifts, so the switch is clean with no stale content.

## Tech stack

Kotlin, Jetpack Compose, Material 3, Coroutines and Flow, Paging 3, Room, DataStore, Hilt, Retrofit, Jetpack Glance, Firebase Analytics, AdMob, Coil

## Architecture

Clean Architecture with MVVM and Hilt. Each interest is a self-contained module (PagingSource, repository, screen), so adding a new category touches no existing code.

```
core/     analytics, ads, shared models
data/     Room, DataStore, Retrofit services, PagingSources, repositories
domain/   models and use cases
ui/       feed router, per-interest screens, theme
widget/   Glance widget
```

## Setup

1. Clone the repo
2. Get free API keys for NewsAPI and OpenWeatherMap
3. Add them to `local.properties`:
   ```
   NEWS_API_KEY=your_key
   WEATHER_API_KEY=your_key
   ```
4. Add your own `google-services.json` to `app/`
5. Build and run

## Where it goes next

- Per-event decay timestamps for more accurate scoring
- Server-side scoring and server-driven layouts, enabling cross-device profiles and A/B testing without app releases
