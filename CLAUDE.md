## Codebase Overview

CAH-Engine is a Java library (Maven module `org.themarioga:cah-engine`) implementing the
domain model, persistence, and business logic for a multiplayer "Cards Against Humanity"
game engine. It has no controller/web layer of its own, and knows nothing about Telegram: the
front-end is `CAH-Telegram` (a game bot plus a companion dictionary-management bot), and other
platforms are meant to be able to sit on top of the same engine. It depends on the
sibling `commons-engine` library for shared abstractions (`Base`, `Game`, `Player`, generic
Hibernate DAO, `Room`/`User`/`Lang` entities).

**Stack**: Java, Hibernate/JPA (Jakarta Persistence, HQL via `Session`, no Spring Data
repositories), Spring `@ConfigurationProperties`, JUnit 5 + Mockito (unit tests) +
spring-test-dbunit (one integration test class), Maven with versions inherited from parent
POM `org.themarioga:parent:2.0.0`.

**It ships no Flyway migrations.** A library that carries `db/migration/**` imposes its schema
on every app depending on it, and these described the pre-refactor schema — Flyway ran them out
of the jar and corrupted the database. The schema now belongs to the application
(`CAH-Telegram`); the old migrations are kept as history in `docs/legacy-db-migration/`.

**Structure**: `config` (rule defaults) → `models` (JPA entities: dictionaries/cards, games/
rounds/players) → `dao` (Hibernate DAOs, intf+impl) → `services` (business logic, intf+impl,
with `CAHServiceImpl` as the top-level orchestration facade) → `enums`/`exceptions` (state
machines and typed errors). Game rules (votation modes, round state machine, scoring) live
in `services/impl/CAHServiceImpl.java` and `services/impl/game/RoundServiceImpl.java`.

⚠️ Known gaps worth knowing before changing gameplay code: card dealing has **no explicit
shuffle**, `getWinner`/`getMostVotedCard` have **no tie-breaking**, and most of
`src/test/resources/dbunit/` is unused/stale fixture scaffolding. See the map's Gotchas
section for the full list (including mismatched exception error codes and a likely-broken
`PlayerDao` query).

For detailed architecture, module guide, data-flow diagrams, and navigation guide, see
[docs/CODEBASE_MAP.md](docs/CODEBASE_MAP.md).
