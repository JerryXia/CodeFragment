# Repository Guidelines

## Project Structure & Module Organization
This repo is a polyglot sandbox of small, self-contained examples:
- `java/` Maven modules (multiple apps/libs); tests under `src/test/java`.
- `go/` Go snippets and tests (`*_test.go`).
- `csharp/` classic .NET projects (`.sln`, `.csproj`).
- `python/` small scripts (e.g., `MySimpleHttpServer.py`).
- `php/`, `js/`, `css/`, `html/` standalone utilities and pages.
- `flex/` ActionScript/Flash sample; `bat/` helper scripts; `config/` editor/tooling prefs.

## Build, Test, and Development Commands
- Java (per module with `pom.xml`):
  - `cd java/<module>`; run tests: `./mvnw -q test` (or `mvn -q test`).
  - Build jar: `./mvnw -q package` (profiles like `-Pmac`/`-Pwin` exist in some modules).
- Go:
  - From repo root: `go fmt ./go/... && go test ./go/...`.
  - Benchmarks (if present): `go test -bench=. ./go`.
- Python: `python3 python/MySimpleHttpServer.py` (module-specific scripts run directly).
- PHP: `php php/curl.php` (similar for other files in `php/`).
- Static pages: `python3 -m http.server` then open files under `html/`.
- C#: open/build with Visual Studio targeting the provided `.sln` files.

## Coding Style & Naming Conventions
- Java/C#: 4-space indent; classes `UpperCamelCase`, methods/fields `lowerCamelCase`, constants `UPPER_SNAKE_CASE`.
- Go: formatted via `gofmt`/`go fmt`; package names lowercase; exported identifiers `CamelCase`.
- JS/CSS/HTML: 2-space indent; end lines with semicolons in JS; filenames kebab-case when adding new utilities.
- Python: PEP 8 (4 spaces). No repo-wide formatter is enforced; match nearby code.

## Testing Guidelines
- Java: JUnit tests live in `src/test/java`; name tests `*Test.java`; run with `mvn test`.
- Go: place tests in `*_test.go`; keep table-driven tests where practical; run `go test ./go/...`.
- Aim for small, runnable examples; prefer zero external services.

## Commit & Pull Request Guidelines
- History favors short, imperative messages (e.g., "update fsm"). Use present tense and keep to one line; add details in the body if needed.
- For PRs: include what changed and why, minimal repro steps or screenshots for UI, and reference related issues.
- Keep changes scoped to a single folder/module whenever possible.

## Security & Configuration Tips
- Do not commit secrets. Environment-specific files (e.g., `java/**/resources/env/*.properties`) should contain placeholders or local overrides.
