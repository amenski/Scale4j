# Releasing to Maven Central

## Pre-flight Checklist

### 1. Maven Central account setup (one-time)

- Register at https://central.sonatype.com
- Claim the `io.github.amenski` namespace — verified automatically via GitHub
- Add OSSRH credentials to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_SONATYPE_USERNAME</username>
      <password>YOUR_SONATYPE_TOKEN</password>
    </server>
  </servers>
</settings>
```

### 2. GPG key setup (one-time)

```bash
gpg --gen-key
```

Follow the prompts (use your name + email). Then get your key ID and publish it:

```bash
gpg --list-secret-keys --keyid-format SHORT
# Look for something like: sec   rsa3072/AABBCCDD
# AABBCCDD is your key ID

gpg --keyserver keyserver.ubuntu.com --send-keys AABBCCDD
```

### 3. `~/.m2/settings.xml` credentials (one-time)

Create or edit `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_SONATYPE_USERNAME</username>
      <password>YOUR_SONATYPE_TOKEN</password>
    </server>
  </servers>
</settings>
```

- `YOUR_SONATYPE_USERNAME` — your username at https://central.sonatype.com
- `YOUR_SONATYPE_TOKEN` — generate a token from your account settings (User Token, not your password)

To avoid being prompted for your GPG passphrase during deployment, add it to the same file:

```xml
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>YOUR_SONATYPE_USERNAME</username>
      <password>YOUR_SONATYPE_TOKEN</password>
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

### 4. Update the version

Remove the `-SNAPSHOT` suffix using the Maven versions plugin (updates all modules at once):

```bash
mvn versions:set -DnewVersion=1.0.0 -DgenerateBackupPoms=false
```

### 5. Deploy

```bash
git commit -am "release: 1.0.0"
mvn clean deploy -Prelease
```

### 6. Post-release: bump to next snapshot

```bash
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "chore: bump version to 1.1.0-SNAPSHOT"
```

---

## Notes

- The `release` Maven profile (defined in root `pom.xml`) handles GPG signing and Nexus staging automatically.
- `autoReleaseAfterClose=true` means the artifact will be promoted to Maven Central without a manual release step.
- Staging URL: `https://s01.oss.sonatype.org/`
