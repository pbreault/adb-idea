#!/bin/bash
# Lists the latest 10 Android Studio versions

# Function to check if a command exists
command_exists() {
    type "$1" &> /dev/null
}

# Check if curl is installed
if ! command_exists curl; then
    echo "Error: curl is not installed. Please install it and try again."
    exit 1
fi

# Check if xmlstarlet is installed
if ! command_exists xmlstarlet; then
    echo "Error: xmlstarlet is not installed. Please install it and try again."
    exit 1
fi

# If all commands exist, execute the provided command
curl -sL https://jb.gg/android-studio-releases-list.xml | xmlstarlet sel -t -m "//item" -v "concat(name, ': ', version)" -nl | head -n 10
