# RankX

RankX is a dual-track learning platform built around two connected product experiences:

- coding practice, submissions, and study progression
- quiz authoring, attempts, results, and review

The platform now has a completed `Phase 1` production MVP baseline and a completed `Phase 2` premium product baseline, including onboarding, study plans, progress tracking, analytics, recommendations, history improvements, admin analytics, and event tracking.

## Product Tracks

### Coding platform
- browse problems
- run code with sample or custom input
- submit code and review results
- track progress through study plans
- review submission history and attempt summaries
- manage problems, languages, templates, and testcases

### Quiz platform
- browse quizzes
- attempt quizzes
- review results with richer feedback
- track progress through study plans
- review quiz history and score changes
- manage quizzes and questions

## Service Map

### Backend services
- `api-gateway`
- `auth-service`
- `user-service`
- `config-server`
- `discovery-server`
- `problemservice`
- `submissionservice`
- `quiz-service`
- `question-service`
- `attempt-service`
- `result-service`

### Frontend applications
- `Frontend/Rankx`
- `Frontend/Rankx-admin`

## Current Phase Status

- Phase 1: completed
- Phase 2: completed
- Coding platform: Phase 2 complete
- Quiz platform: Phase 2 complete and aligned with the coding platform
- Admin analytics baseline: complete
- Event tracking baseline: complete

## Phase 2 Outcomes

Phase 2 added the product depth that Phase 1 intentionally deferred:

- onboarding and user preferences
- personalized dashboard summaries
- study plans and enrollment
- user progress tracking
- real progress sync from coding and quiz activity
- analytics and recommendation engine v1
- richer coding and quiz history
- admin content analytics and KPI dashboards
- event ingestion for activation and engagement reporting
- deeper test coverage and Phase 2 stabilization

## Documentation Map

- [Docs/ARCHITECTURE.md](E:/Workspace/RankX/Docs/ARCHITECTURE.md)
- [Docs/SECURITY.md](E:/Workspace/RankX/Docs/SECURITY.md)
- [Docs/CODING_PLATFORM.md](E:/Workspace/RankX/Docs/CODING_PLATFORM.md)
- [Docs/QUIZ_PLATFORM.md](E:/Workspace/RankX/Docs/QUIZ_PLATFORM.md)
- [Docs/PHASES.md](E:/Workspace/RankX/Docs/PHASES.md)
- [Docs/ROADMAP.md](E:/Workspace/RankX/Docs/ROADMAP.md)
- [Docs/REPOSITORY_STRUCTURE.md](E:/Workspace/RankX/Docs/REPOSITORY_STRUCTURE.md)
- [Docs/PHASE_2_COMPLETION.md](E:/Workspace/RankX/Docs/PHASE_2_COMPLETION.md)

## Current Repository Note

The canonical backend service source is under `Backend/`, with frontend applications under `Frontend/`. Legacy service names still exist for `problemservice` and `submissionservice`; those names should only be normalized through a separate explicit repository migration.

## Verification Snapshot

Phase 2 verification completed with:

- `mvn test` for `user-service`
- `mvn test` for `submissionservice`
- `mvn test` for `result-service`
- `npm run build` for `Frontend/Rankx`
- `npm run build` for `Frontend/Rankx-admin`

Phase 1 verification was also completed earlier across the full platform baseline.
