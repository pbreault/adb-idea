#!/bin/bash
# Lists the latest 10 Android Studio versions

# Function to check if a command exists
command_exists() {
    type "$1" &> /dev/null
}

# Check if curl is installed
if ! command_exists curl; then
    echo "Error: curl is not installed. Please install it and try again." >&2
    exit 1
fi

# Check if xmlstarlet is installed
if ! command_exists xmlstarlet; then
    echo "Error: xmlstarlet is not installed. Please install it and try again." >&2
    exit 1
fi

# Download the XML into a temporary file so we can run multiple queries without re-downloading
TMP_XML=$(mktemp)
trap 'rm -f "$TMP_XML"' EXIT
curl -sL https://jb.gg/android-studio-releases-list.xml -o "$TMP_XML"

# If download failed or file empty, exit
if [ ! -s "$TMP_XML" ]; then
    echo "Error: failed to download or empty releases list." >&2
    exit 1
fi

# Print the top-10 entries (preserve previous top-10 output)
xmlstarlet sel -t -m "//item" -v "concat(name, ': ', version, ' || IntelliJ Version: ', platformBuild)" -nl "$TMP_XML" | head -n 10

# --- Extract latest Canary and latest stable entries ---
# Note: XPath translate() lowers text before searching for case-insensitive matches.
# Find latest Canary (by version or name containing 'canary')
canaryName=$(xmlstarlet sel -t -m "(//item[contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary') or contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')])[1]" -v "name" -n "$TMP_XML" 2>/dev/null || true)
canaryVersion=$(xmlstarlet sel -t -m "(//item[contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary') or contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')])[1]" -v "version" -n "$TMP_XML" 2>/dev/null || true)
# canaryIntelliJMinSupport = platformBuild of the latest canary found above
canaryIntelliJMinSupport=$(xmlstarlet sel -t -m "(//item[contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary') or contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')])[1]" -v "platformBuild" -n "$TMP_XML" 2>/dev/null || true)


# Find latest stable (first item that does not contain canary/rc/beta in version or name)
stableName=$(xmlstarlet sel -t -m "(//item[not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta'))])[1]" -v "name" -n "$TMP_XML" 2>/dev/null || true)
stableVersion=$(xmlstarlet sel -t -m "(//item[not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta'))])[1]" -v "version" -n "$TMP_XML" 2>/dev/null || true)
# stableIntelliJMinSupport = platformBuild of the latest stable found above
stableIntelliJMinSupport=$(xmlstarlet sel -t -m "(//item[not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(version,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'canary')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'rc')) and not(contains(translate(name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'beta'))])[1]" -v "platformBuild" -n "$TMP_XML" 2>/dev/null || true)

# Fallback behaviour if we didn't find canary/stable entries
[ -z "$canaryVersion" ] && canaryVersion="(none found)"
[ -z "$stableVersion" ] && stableVersion="(none found)"
[ -z "$stableIntelliJMinSupport" ] && stableIntelliJMinSupport="(none found)"
[ -z "$canaryIntelliJMinSupport" ] && canaryIntelliJMinSupport="(none found)"
[ -z "$canaryName" ] && canaryName=""
[ -z "$stableName" ] && stableName=""

# Print only the three required lines in a compact form. Comments include name and version for context.
printf "%s\n" "==================================================="
printf "%s\n" "# $canaryName"
printf "%s\n" "canaryIdeVersion=$canaryVersion"
printf "%s\n" "# $stableName"
printf "%s\n" "stableIdeVersion=$stableVersion"
printf "%s\n" "# Minimum Intellij Platform version supported by this plugin, received from the list-studio-versions.sh"
printf "%s\n" "canaryIntelliJMinSupport=$canaryIntelliJMinSupport"
printf "%s\n" "stableIntelliJMinSupport=$stableIntelliJMinSupport"
