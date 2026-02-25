# Releasing to Maven Central

## Pre-flight Checklist (one-time setup)

### 1. Maven Central account

- Register at https://central.sonatype.com
- Claim the `io.github.amenski` namespace (verified automatically via GitHub)
- Go to **Account** > **Generate User Token** to get your token username and password

### 2. GPG key

Generate a key:

```bash
gpg --gen-key
```

Find your key ID:

```bash
gpg --list-secret-keys --keyid-format LONG
# Example output:
# sec   ed25519/AABBCCDD11223344 2026-01-01 [SC]
#       XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
# The key ID is: AABBCCDD11223344
```

Upload to keyservers (Maven Central checks these to verify signatures):

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
```

Allow a few minutes for propagation before deploying.

### 3. `~/.m2/settings.xml`

Create or edit `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>YOUR_TOKEN_USERNAME</username>
      <password>YOUR_TOKEN_PASSWORD</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>release</id>
      <properties>
        <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

- The server `id` must be `central` (matches `publishingServerId` in `pom.xml`)
- Token credentials come from **Account** > **Generate User Token** on https://central.sonatype.com
- The GPG passphrase entry is optional but avoids interactive prompts during deployment

## Releasing

### 1. Update the version

```bash
mvn versions:set -DnewVersion=1.0.0 -DgenerateBackupPoms=false
```

### 2. Commit and deploy

```bash
git commit -am "release: 1.0.0"
mvn clean deploy -Prelease -DskipTests
```

### 3. Verify

- Check deployment status at https://central.sonatype.com/publishing/deployments
- After 10-30 minutes, the artifacts will appear on https://search.maven.org/search?q=g:io.github.amenski

### 4. Post-release: bump to next snapshot

```bash
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "chore: bump version to 1.1.0-SNAPSHOT"
```

## What the release profile does

The `release` profile in the root `pom.xml` activates:

- **maven-source-plugin** — attaches `-sources.jar` (required by Maven Central)
- **maven-javadoc-plugin** — attaches `-javadoc.jar` (required by Maven Central)
- **maven-gpg-plugin** — signs all artifacts with your GPG key
- **central-publishing-maven-plugin** — bundles and uploads to Maven Central

The `excludeArtifacts` configuration excludes `scale4j-benchmarks` and `scale4j-examples` from the published bundle since they are internal modules not intended for public consumption.

## Troubleshooting

| Error | Cause | Fix |
|-------|-------|-----|
| `401 - Unauthorized` | Wrong or expired credentials | Regenerate token at https://central.sonatype.com > Account > Generate User Token and update `~/.m2/settings.xml` |
| `Could not find a public key` | GPG key not on keyservers | Run `gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID` and wait a few minutes |
| `Deployment failed while publishing` | Missing sources/javadoc jars or other validation failure | Check the error details on https://central.sonatype.com/publishing/deployments |
