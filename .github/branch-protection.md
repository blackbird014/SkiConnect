# GitHub Branch Protection Settings

This document provides guidance on setting up branch protection rules for the SkiConnect repository to enhance security.

## Recommended Branch Protection Settings

### For the `main` branch:

1. **Require pull request reviews before merging**
   - Required number of approvals: 1
   - Dismiss stale pull request approvals when new commits are pushed
   - Require review from Code Owners

2. **Require status checks to pass before merging**
   - Select the following status checks:
     - `build` (Maven build and test)
     - `gitleaks-action` (Secret scanning)

3. **Require branches to be up to date before merging**
   - This ensures that the branch has the latest changes from the base branch

4. **Include administrators in these restrictions**
   - This ensures that even repository administrators follow the same rules

5. **Allow force pushes**
   - Disable force pushes to prevent history rewriting

6. **Allow deletions**
   - Disable branch deletion to prevent accidental removal of the main branch

## Setting Up Branch Protection

1. Go to your GitHub repository
2. Click on "Settings" tab
3. In the left sidebar, click on "Branches"
4. Under "Branch protection rules", click "Add rule"
5. In "Branch name pattern", enter `main`
6. Configure the settings as described above
7. Click "Create" or "Save changes"

## Code Owners

To enforce code review by specific team members, create a `.github/CODEOWNERS` file with the following content:

```
# These owners will be the default owners for everything in the repo
* @your-username

# Security-related files
*.java @security-team-member
.gitleaks.toml @security-team-member
.github/workflows/maven.yml @security-team-member
```

Replace `@your-username` and `@security-team-member` with actual GitHub usernames.

## Security Best Practices

1. **Regular Security Audits**
   - Review the repository for security issues regularly
   - Check for outdated dependencies
   - Review access permissions

2. **Secret Rotation**
   - If any secrets are accidentally committed, rotate them immediately
   - Use GitHub Secrets for sensitive values

3. **Limited Permissions**
   - Use the principle of least privilege for repository access
   - Regularly review collaborator permissions

4. **Security Alerts**
   - Enable GitHub's security alerts for vulnerable dependencies
   - Address security alerts promptly 