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
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 3. Update the version

Remove the `-SNAPSHOT` suffix using the Maven versions plugin (updates all modules at once):

```bash
mvn versions:set -DnewVersion=1.0.0 -DgenerateBackupPoms=false
```

### 4. Deploy

```bash
git commit -am "release: 1.0.0"
mvn clean deploy -Prelease
```

### 5. Post-release: bump to next snapshot

```bash
mvn versions:set -DnewVersion=1.1.0-SNAPSHOT -DgenerateBackupPoms=false
git commit -am "chore: bump version to 1.1.0-SNAPSHOT"
```

---

## Notes

- The `release` Maven profile (defined in root `pom.xml`) handles GPG signing and Nexus staging automatically.
- `autoReleaseAfterClose=true` means the artifact will be promoted to Maven Central without a manual release step.
- Staging URL: `https://s01.oss.sonatype.org/`
