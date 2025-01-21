# Contributing Guidelines

Thank you for considering a contribution to app-mod-sample! Please read the guidelines carefully.

## Contributing prerequisites (CLA/DCO)

The project does not yet define a Contributor License Agreement. By submitting pull requests submitters acknowledge they grant the [Apache License v2](./LICENSE) to the code and that they are eligible to grant this license for all commits submitted in their pull requests.

## Getting Started

The `main` branch on this repository contains a traditional IBM WebSphere Application Server version of the application. All contributions to the `main` branch must be compatible with the application being deployed on IBM WebSphere Application Server 9 and using Java 8.

The `liberty-java8` branch shows what the application looks like after modernizing to Liberty. 

The `liberty-java21` branch shows what the application looks like after modernizing to Liberty **AND** upgrading Java to Java 21.

The `liberty-` branches must be kept consistent with the main branch. We want the branches to always reflect different stages of the modernization process.


## Writing Pull Requests

Contributions can be submitted by creating a pull request on Github.
We recommend you do the following to ensure the maintainers can collaborate on your contribution:

- Fork the project into your personal Github account
- Create a new feature branch for your contribution
- Make your changes
- Open a PR with a clear description

Contributors must include a Signed-off-by line in their commit message, to avoid having PRs blocked. Always include an email address that matches the
commit author. For example:

```
feat: this is my commit message

Signed-off-by: Author Name <authoremail@example.com>
```

You can also do this automatically with `git`, by using the -s flag:

```
$ git commit -s -m 'This is my commit message'
```

## Code review process

Once your pull request is submitted, a Project maintainer should be assigned to review your changes.

The code review should cover:

Contributors are expected to respond to feedback from reviewers in a constructive manner.
Reviewers are expected to respond to new submissions in a timely fashion, with clear language if changes are requested.

Once the pull request is approved, it will get merged.
