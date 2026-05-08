# Roadmap

## Current Direction

Phase 1 and Phase 2 have been completed across the platform. The next direction is Phase 3 product maturity, scalability, observability, and premium intelligence.

## Phase 1 Execution Order

1. documentation and repo baseline
2. gateway and auth hardening
3. quiz hardening
4. coding hardening
5. cross-cutting cleanup and validation

## Current Phase 1 Change Log

### Documentation batch
- restored root README
- restored structured system docs

### Platform batch
- gateway JWT handling hardening
- safer auth registration and OTP flow

### Quiz batch
- attempt security tightening
- result ownership tightening
- quiz answer correctness fix
- quiz review/detail parity improvements
- quiz admin status toggle fix

### Coding batch
- testcase-to-problem binding fix
- submission identity hardening
- custom input execution path
- submission history and detail baseline

## Phase 2 Change Log

### Activation and onboarding
- onboarding and preferences support added in `user-service`
- personalized dashboard summary added
- goal, track, and level-based activation flow completed

### Study plans and progress
- study plan entities and APIs added
- seeded default study plans
- study plan enrollment and progress summary completed
- dashboard progress widgets added

### Real activity sync
- accepted coding submissions now update study plan progress
- completed quiz results now update study plan progress
- percentage, next item, and locked/completed states are recalculated automatically

### Analytics and recommendations
- user analytics APIs added
- coding and quiz performance summaries added
- weak and strong topic detection added
- rules-based recommendation engine v1 added
- analytics page and dashboard recommendation cards added

### History and review depth
- coding submission history filters added
- problem-level attempt summaries added
- quiz history filters added
- richer quiz review and score comparison added

### Admin analytics and event tracking
- problem, quiz, and question analytics dashboards added
- KPI reporting APIs and admin KPI dashboard added
- product event ingestion and frontend tracking hooks added

### Stabilization and production-readiness
- broader JUnit coverage added
- edge-case and null-handling fixes completed
- frontend state and routing issues cleaned up
- affected backend and frontend verification completed

## Current Recommended Next Work

- Phase 3 observability and operational maturity
- async/event-pipeline evolution for scale
- deeper personalization and recommendation quality
- performance optimization including frontend chunk splitting
- richer retention and funnel analytics
