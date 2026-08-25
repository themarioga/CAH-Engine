## Codebase Overview

CAH-Engine is a Java library (Maven module `org.themarioga:cah-engine`) implementing the
domain model, persistence, and business logic for a multiplayer "Cards Against Humanity"
game engine, consumed by separate Telegram bot applications (a game bot and a companion
dictionary-management bot). It has no controller/web layer of its own. It depends on the
sibling `engine-commons` library for shared abstractions (`Base`, `Game`, `Player`, generic
Hibernate DAO, `Room`/`User`/`Lang` entities).

**Stack**: Java, Hibernate/JPA (Jakarta Persistence, HQL via `Session`, no Spring Data
repositories), Spring `@ConfigurationProperties`, Flyway migrations (dual-maintained for
MariaDB and H2), JUnit 5 + Mockito (unit tests) + spring-test-dbunit (one integration test
class), Maven with versions inherited from parent POM `org.themarioga:parent:2.0.0`.

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
