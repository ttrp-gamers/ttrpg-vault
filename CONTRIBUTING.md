# Contributing Guidelines

To maintain high code quality, consistency, and a clean Git history, please follow these guidelines when contributing.

---

## 1. Branch Strategy & Naming Conventions

We follow a **Feature Branching Strategy**. Never commit directly to `main`. Always create a new branch off `main` for your task.

### Branch Name Format
`<category>/<short-description>`

### Categories
* `feature/` — New features or endpoints (e.g., `feature/user-registration`)
* `fix/` — Bug fixes (e.g., `fix/jwt-expiration-calc`)
* `refactor/` — Code changes that neither fix a bug nor add a feature (e.g., `refactor/session-service`)
* `test/` — Adding or modifying unit/integration tests (e.g., `test/auth-controller`)
* `docs/` — Updates to README, OpenAPI, or inline comments (e.g., `docs/update-readme`)

*Examples:*
* `feature/login-rate-limiting`
* `fix/user-table-reserved-keyword`

---

## 2. Commit Message Standards

We use **Conventional Commits** to keep our commit log clear and searchable.

### Format
`<type>(<scope>): <short description in present tense>`

### Types
* `feat`: A new feature
* `fix`: A bug fix
* `refactor`: Refactoring production code without changing behavior
* `test`: Adding or updating tests
* `docs`: Documentation changes only
* `chore`: Build script, dependencies, or tool updates

### Examples
```text
feat(auth): add refresh token rotation service
fix(security): resolve PostgreSQL reserved keyword conflict on user table
test(auth): add unit tests for AuthService register logic
docs(readme): add docker run command for postgres
```

### Commit Guidelines
* Keep commits atomic (one logical change per commit).

* Use the imperative mood in descriptions ("add", not "added" or "adds").

* Do not commit secrets, credentials, or .env files.

---

## 3. Pull Request (PR) Workflow
Follow this step-by-step workflow to get your code merged into main:

```Plaintext
1. Fetch latest main  ──> 2. Create branch ──> 3. Commit changes
                                                      │
6. Merge to main    <── 5. Review & Approval <── 4. Open PR
```

### Step-by-Step Instructions
Update your local main branch:

```Bash
git checkout main
git pull origin main
```
Create your feature branch:

```Bash
git checkout -b feature/your-feature-name
```

Develop and test your changes locally:

``` Bash
./mvnw clean test
```

Ensure all tests pass before committing.

### Stage and commit your work:

```Bash
git add .
git commit -m "feat(auth): implement refresh token reuse detection"
```
Push your branch to GitHub:

```Bash
git push -u origin feature/your-feature-name
```
Open a Pull Request:

* Go to the repository on GitHub.

* Click Compare & pull request.

* Fill out the Pull Request template completely.

* Assign at least one peer reviewer.

### Address Feedback:

* Make requested changes directly on your feature branch and push them.

* Once approved and CI checks pass, squashed-merge into main.

--- 