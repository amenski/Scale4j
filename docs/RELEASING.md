# Releasing to Maven Central

## Pre-flight Checklist

### 1. Fill in placeholder properties in `pom.xml`

The root `pom.xml` has several unset placeholders that must be filled before release:
- `${github-username}` - your GitHub username
- `${organization-name}`, `${organization-url}`
- `${developer-id}`, `${developer-name}`, `${developer-email}`

### 2. Update the version

Remove `-SNAPSHOT` suffix for a release:

```xml
<version>1.0.0</version>
```

### 3. Maven Central account setup (one-time)

- Register at https://central.sonatype.com
- Claim the `com.scale4j` namespace (requires proving ownership of the `scale4j.com` domain), **or** use `io.github.<username>` which is verified automatically via GitHub (easier)
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

### 4. GPG key setup

```bash
gpg --gen-key
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### 5. Deploy

```bash
mvn clean deploy -Prelease
```

---

## Namespace Recommendation

Since `com.scale4j` requires domain ownership verification, consider using `io.github.<your-username>` as `groupId` — it's verified automatically via GitHub. You would need to update the `groupId` in all modules accordingly.

---

## Notes

- The `release` Maven profile (defined in root `pom.xml`) handles GPG signing and Nexus staging automatically.
- `autoReleaseAfterClose=true` means the artifact will be promoted to Maven Central without a manual release step.
- Staging URL: `https://s01.oss.sonatype.org/`
