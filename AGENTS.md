# AGENTS.md

## Project
Lightweight Java library that injects SQL from classpath `.sql` files into annotated `String` fields.

Stack:
- Java 25
- Maven

Goals:
- minimal API
- predictable behavior
- thread-safe caching
- clear failures

Rules:
- core module must not depend on Spring

## Public API
Keep API small and stable:

- `@SqlInject`
- `SqlInjector.inject(Object)`
- `SqlInjector.inject(Object, SqlLoaderOptions)`

Do not expand API without strong justification.

## SQL loading
- load SQL from classpath resources
- normalize path (`\` → `/`, remove leading `/`)
- use target classloader, fallback to context classloader
- preserve SQL text exactly unless options modify it

## Field rules
Supported:
- instance fields
- `String` type
- any visibility

Reject with clear exception:
- `static`
- `final`
- non-`String` fields

Scan superclass hierarchy (excluding `Object`).

## Errors
Exceptions must include:
- class name
- field name
- SQL path

Never silently ignore failures.

## Caching
- thread-safe
- prefer `ConcurrentHashMap`
- cache SQL text

Correctness over complexity.

## Code style
- simple and readable
- small methods
- descriptive names
- prefer early returns
- minimal abstractions
- keep public API small

## Tests
Behavior changes must include tests.

Priority cases:
- successful injection
- missing resource failure
- invalid field rejection
- inherited fields
- caching behavior

## Build
Use Maven.

Typical command:

```
mvn -q clean test
```


## Diff rules
- minimal changes
- no unrelated formatting
- do not rename APIs without need
- do not edit generated/build files

## Dependencies
Core module:
- prefer JDK APIs
- avoid heavy dependencies

## Workflow

1. Define the API
2. Create tests for desired behaviour for that API
3. Implement the API
4. Check if tests pass
5. Repeat from 1.