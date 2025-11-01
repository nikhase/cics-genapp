# Git Workflow and Development Process

This document defines the Git workflow, branch naming conventions, and pull request process for the CICS GenApp project.

## Branch Naming Conventions

All branches should follow a clear naming convention to make it easy to identify the work being done:

### Feature Branches
```
feature/<ticket-id>-<brief-description>
feature/CICS-101-add-customer-rest-api
feature/CICS-102-implement-zitadel-oidc
feature/add-audit-logging-framework
```

Use for: New features, enhancements, API additions

### Bugfix Branches
```
bugfix/<ticket-id>-<brief-description>
bugfix/CICS-205-fix-customer-update-validation
bugfix/fix-spring-security-cors-issue
```

Use for: Bug fixes, issue resolutions

### Hotfix Branches
```
hotfix/<ticket-id>-<brief-description>
hotfix/CICS-500-fix-production-database-connection
hotfix/fix-critical-memory-leak
```

Use for: Critical production fixes

### Documentation Branches
```
docs/<brief-description>
docs/add-api-documentation
docs/update-architecture-decisions
```

Use for: Documentation updates, ADRs, guides

### Chore Branches
```
chore/<brief-description>
chore/update-dependencies
chore/refactor-customer-service
```

Use for: Refactoring, dependency updates, maintenance

## Main Branch Protection

The `main` branch is protected and requires:

1. ✅ Pull Request approval (minimum 1 review)
2. ✅ All CI/CD checks passing (automated tests, code quality, security scans)
3. ✅ No direct commits allowed (all changes via PR)
4. ✅ Branch must be up-to-date with main before merge

## Pull Request Process

### 1. Create Feature Branch

```bash
# Update main to latest
git checkout main
git pull origin main

# Create new feature branch
git checkout -b feature/CICS-123-add-login-page

# Or bugfix/hotfix
git checkout -b bugfix/CICS-456-fix-password-reset
```

### 2. Make Changes

```bash
# Work on your changes
# Commit regularly with clear messages
git add .
git commit -m "Add customer search API endpoint

- Implement GET /api/v1/customers?name=<query>
- Add pagination support (page, size parameters)
- Include input validation for query parameters
- Add unit tests for search logic"

# Push to remote
git push origin feature/CICS-123-add-login-page
```

### 3. Open Pull Request

```bash
# GitHub CLI
gh pr create --title "Add login page with Zitadel OIDC" \
  --body "
## What does this PR do?

Implements the login page with Zitadel OIDC authentication integration.

## Acceptance Criteria Met
- [x] Login form renders correctly
- [x] OIDC redirect flow works
- [x] Session management implemented
- [x] Error messages display

## Testing
- [x] Unit tests (90% coverage)
- [x] Integration tests with mock Zitadel
- [x] Manual testing on dev environment

## Files Changed
- src/main/java/com/example/cicsgenapp/config/SecurityConfig.java
- src/main/java/com/example/cicsgenapp/api/AuthController.java
- src/main/resources/templates/login.html
- src/test/java/.../SecurityConfigTest.java
"
```

Or open via GitHub web interface:
1. Go to repository
2. Click "Compare & pull request"
3. Fill in title and description
4. Select reviewers
5. Click "Create pull request"

### 4. Code Review

Your PR will be reviewed by at least one team member:

- **Reviewer responsibilities:**
  - Check code follows Google Java Style Guide
  - Verify tests are included and pass
  - Look for security issues
  - Ensure architecture patterns are followed
  - Test locally if needed

- **Author responsibilities:**
  - Respond to feedback promptly
  - Make requested changes
  - Push new commits to same branch (don't force push)
  - Mark conversation threads as resolved

### 5. Address Feedback

```bash
# Make changes based on review
git add .
git commit -m "Address review feedback: add error handling for OIDC failures"

# Push commits (do NOT force push!)
git push origin feature/CICS-123-add-login-page
```

**Important:** Never force push to your branch when there are active reviews. This makes it hard to see what changed since the review.

### 6. Approval and Merge

Once approved:

```bash
# Option 1: Via GitHub CLI
gh pr merge <pr-number> --squash

# Option 2: Via GitHub web interface
# Click "Squash and merge" button
```

**Merge Strategy:** We use squash merges to keep main branch history clean. Each PR becomes one commit.

### 7. Cleanup

```bash
# Delete feature branch locally
git branch -d feature/CICS-123-add-login-page

# Delete feature branch on remote
git push origin --delete feature/CICS-123-add-login-page
```

## Commit Message Guidelines

Write clear, descriptive commit messages:

```
# Good example:
git commit -m "Implement customer search API with pagination

- Add GET /api/v1/customers endpoint
- Support search by name, email, phone
- Implement pagination (page, size, sort parameters)
- Add input validation and error handling
- Include 95% test coverage"

# Bad examples:
git commit -m "fix"
git commit -m "update stuff"
git commit -m "WIP"
```

**Format:**
- First line: 50 characters max, imperative mood ("Add" not "Added")
- Blank line
- Detailed description (wrap at 72 chars)
- Reference issues if applicable: "Fixes #123"

## Working with Main Branch

### Keep Your Branch Updated

```bash
# While working on feature branch
git fetch origin
git merge origin/main

# Or rebase (cleaner history, use carefully)
git rebase origin/main
```

### Before Creating PR

```bash
# Update your branch
git fetch origin
git rebase origin/main

# Verify tests still pass
mvn clean test

# Verify formatting
mvn spotless:check

# Push (use --force-with-lease if rebased)
git push origin feature/CICS-123-add-login-page
```

## Release Management

### Release Branches

```bash
# Create release branch from main
git checkout -b release/v1.0.0 main

# Make version bumps and release notes
# Then tag the commit
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin release/v1.0.0
git push origin v1.0.0
```

### Hotfixes from Production

```bash
# Create hotfix branch from latest production tag
git checkout -b hotfix/v1.0.1 v1.0.0

# Make fixes and test
# Then create PR against main and release branch
```

## CI/CD Pipeline Integration

All branches automatically trigger:

1. ✅ **Unit Tests** - Run all tests, minimum 80% coverage required
2. ✅ **Code Quality Checks** - Checkstyle, SpotBugs, code review tools
3. ✅ **Security Scanning** - Dependency check, SAST analysis
4. ✅ **Build Artifact** - Create Docker image for dev/test branches

Status is shown in:
- Pull request checks
- GitHub Actions tab
- Branch status indicators

## Best Practices

### ✅ Do

- Create small, focused PRs (one feature per PR)
- Write descriptive PR titles and descriptions
- Include tests with your changes
- Run tests locally before pushing
- Keep branches up-to-date with main
- Respond promptly to review feedback
- Use GitHub's draft PR feature while work in progress

### ❌ Don't

- Push directly to main (use PRs)
- Force push to branches with active reviews
- Commit sensitive data (passwords, keys, tokens)
- Mix formatting fixes with feature changes
- Leave PRs open longer than necessary
- Merge PRs with failing checks

## Troubleshooting

### Merge Conflicts

```bash
# Update your branch
git fetch origin
git merge origin/main

# Resolve conflicts in IDE or editor
# Then complete merge
git add .
git commit -m "Resolve merge conflicts"
git push origin feature/your-branch
```

### Accidental Commit to Main

```bash
# Create new branch from current main
git branch feature/my-feature

# Reset main to origin
git reset --hard origin/main

# Switch to feature branch
git checkout feature/my-feature
```

### Undo Local Changes

```bash
# Discard uncommitted changes
git restore .

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes)
git reset --hard HEAD~1
```

## Code Review Standards

### Reviewer Checklist

- [ ] Code follows Google Java Style Guide (enforced by Checkstyle)
- [ ] Tests are included (unit, integration, or both)
- [ ] Test coverage is adequate (80%+ for business logic)
- [ ] No unintended changes included
- [ ] Security best practices followed
- [ ] Performance implications considered
- [ ] Documentation updated (comments, README, etc.)
- [ ] No hardcoded values or credentials

### Author Checklist Before PR

- [ ] Code is formatted: `mvn spotless:apply`
- [ ] All tests pass: `mvn clean test`
- [ ] Checkstyle passes: `mvn checkstyle:check`
- [ ] SpotBugs passes: `mvn spotbugs:check`
- [ ] Branch is updated with main: `git pull origin main`
- [ ] Commit messages are clear and descriptive
- [ ] README or docs updated if needed
- [ ] No debug logs or temporary code left

## Questions?

- Check GitHub Documentation: https://docs.github.com/
- Ask in team discussions or reach out to the tech lead
- Review existing merged PRs for examples

---

**Last Updated:** 2025-11-01
**Maintained by:** Development Team
