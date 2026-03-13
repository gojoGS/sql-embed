# Maven Central Acceptance Plan

## Goal
Prepare this project for successful publication to Maven Central via Sonatype Central Portal.

## Current gaps (based on current repository state)
- Missing required POM metadata: `description`, `licenses`, `developers`, `scm`.
- Missing required release artifacts for `jar` packaging: `-sources.jar`, `-javadoc.jar`.
- Missing GPG signing of published artifacts (`.asc` files).
- Missing Central Portal Maven publishing plugin configuration.
- Namespace ownership/verification for `groupId` must be confirmed in Central Portal.
- Recommended project docs are missing: `README.md`, `LICENSE`.

## Action plan

1. Confirm publish coordinates and namespace
- Decide final release coordinates (`groupId`, `artifactId`, `version`).
- Verify namespace ownership in Sonatype Central Portal for the chosen `groupId`.

2. Add required POM metadata
- Add `<description>`.
- Add `<licenses>` with SPDX-compatible license details.
- Add `<developers>` with maintainer info.
- Add `<scm>` with repository URLs.
- Keep `<url>` pointing to public project home.

3. Attach sources and javadoc artifacts
- Configure `maven-source-plugin` with `jar-no-fork`.
- Configure `maven-javadoc-plugin` with `jar`.
- Ensure `mvn clean verify` produces:
  - `<artifactId>-<version>.jar`
  - `<artifactId>-<version>-sources.jar`
  - `<artifactId>-<version>-javadoc.jar`

4. Configure artifact signing
- Set up a GPG key for releases.
- Configure `maven-gpg-plugin` to sign during `verify` (or `deploy`).
- Ensure `.asc` signatures are produced for all deployed files.

5. Configure Central Portal publishing
- Add `org.sonatype.central:central-publishing-maven-plugin` to build.
- Set `publishingServerId` (commonly `central`).
- Configure Maven `settings.xml` with Central user token credentials.

6. Add repository-level release essentials
- Add `README.md` with usage and release notes basics.
- Add `LICENSE` file matching the POM license declaration.

7. Run pre-publish verification locally
- Run `mvn clean verify`.
- Confirm generated artifacts include main JAR, sources JAR, javadoc JAR, signatures.
- Confirm POM metadata is complete and accurate.

8. Publish a release
- Use release version (non-`-SNAPSHOT`).
- Run `mvn deploy` with Central credentials.
- If `autoPublish` is disabled, finalize publication in Central Portal UI.

## Suggested POM additions (high-level)
- Required metadata: `description`, `licenses`, `developers`, `scm`.
- Plugins:
  - `maven-source-plugin`
  - `maven-javadoc-plugin`
  - `maven-gpg-plugin`
  - `central-publishing-maven-plugin`

## Done criteria
- Central validates deployment bundle without metadata/signature/attachment errors.
- Artifact is published and searchable on Maven Central.
