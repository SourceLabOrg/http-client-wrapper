# Http-Client-Wrapper Java REST Client Abstraction Library

## What is it? 

A collection of common patterns to build REST Client libraries on top of. It attempts to provide an abstracted 
interface for building REST clients. Included is an implementation of interfaces using HttpComponents.

## How to use this library

This client library is released on Maven Central.  Add a new dependency to your project's POM file:

```xml
<dependency>
    <groupId>org.sourcelab</groupId>
    <artifactId>http-client-wrapper</artifactId>
    <version>0.1.0</version>
</dependency>
```

# Contributing

Found a bug? Think you've got an awesome feature you want to add? We welcome contributions!


## Submitting a Contribution

1. Search for an existing issue. If none exists, create a new issue so that other contributors can keep track of what you are trying to add/fix and offer suggestions (or let you know if there is already an effort in progress).  Be sure to clearly state the problem you are trying to solve and an explanation of why you want to use the strategy you're proposing to solve it.
1. Fork this repository on GitHub and create a branch for your feature.
1. Clone your fork and branch to your local machine.
1. Commit changes to your branch.
1. Push your work up to GitHub.
1. Submit a pull request so that we can review your changes.

*Make sure that you rebase your branch off of master before opening a new pull request. We might also ask you to rebase it if master changes after you open your pull request.*

## Acceptance Criteria

We love contributions, but it's important that your pull request adhere to some of the standards we maintain in this repository. 

- All tests must be passing!
- All code changes require tests!
- All code changes must be consistent with our checkstyle rules.
- Great inline comments.

# Other Notes


## Releasing

Steps for proper release:
- Update release version: `mvn versions:set -DnewVersion=X.Y.Z`
- Validate and then commit version: `mvn versions:commit`
- Update CHANGELOG and README files.
- Merge to master.
- Deploy to Maven Central: `mvn clean deploy -P release`
- Create release on Github project.


## Changelog

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

[View Changelog](CHANGELOG.md)



