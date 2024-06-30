There are a number of best practices that can be handled at once by inheriting from a parent pom that takes care of them all.

Liftwizard ships with several parent poms that form an inheritance hierarchy.

- [`liftwizard-minimal-parent`](minimal-parent.md) is the most minimal parent pom. It is meant to contain uncontroversial best practices that are applicable to all projects.
- [`liftwizard-profile-parent`](profile-parent.md) is a parent pom that inherits from `liftwizard-minimal-parent` and enables several linters and validators in profiles that are off by default.
- [`liftwizard-bom`](bill-of-materials.md) is a [Bill of Materials (BOM)](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms) that exports all modules within Liftwizard.
- `liftwizard-parent` is a parent pom that inherits from `liftwizard-profile-parent`, selects versions of libraries related to Dropwizard applications, and includes opinionated configurations for plugins.

## Learning Maven

Maven can be confusing due to the extent of the "convention over configuration" approach.

For example, to answer "how does maven run compilation before tests" you would need to learn:

- Plugins which are bound and enabled by default
- `maven-surefire-plugin` is the plugin that handles tests
- `maven-compiler-plugin` binds to the `compile` and `testCompile` phases. `maven-surefire-plugin` binds to the `test` phase
- In the [lifecycle phases](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#default-lifecycle) `compile` comes before `testCompile` which comes before `test`.

None of this information appears in `pom.xml`, and little of it is logged during the build.

To make it easier to understand, `liftwizard-minimal-parent` includes region markers surrounding each plugin that label the phase that the plugin is bound to. The sections are sorted by phase.

:include-file: liftwizard-minimal-parent/pom.xml {
title: "liftwizard-minimal-parent/pom.xml",
surroundedBy: ["region Phase 22: install"],
surroundedByKeep: true
}
