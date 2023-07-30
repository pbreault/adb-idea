#!/bin/bash

# Fetch the token
TOKEN=$(git config --get github.token)

# Check if GitHub token exists
if [ -z "$TOKEN" ]; then
  echo "GitHub token not found in your git config. Please generate a new token at https://github.com/settings/tokens"
  exit 1
fi

# Make sure that the token is valid (a 200 is good enough, not checking scopes)
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: token $TOKEN" https://api.github.com)
if [ "$RESPONSE" != "200" ]; then
  echo "Invalid GitHub token. Please generate a new token at https://github.com/settings/tokens"
  exit 1
fi

# If we made it here, the token is valid
echo -e "GitHub Token is valid. Success!\n "

# Get the latest tag
TAG=$(git describe --tags `git rev-list --tags --max-count=1`)

# Release to upload
FILE="./build/distributions/adb_idea-$TAG.zip"

# Check if the file exists
if [ ! -f "$FILE" ]; then
  echo "$FILE does not exist."
  exit 1
else
  # Get file creation date
    echo "This file will be uploaded:"
    ls -la "$FILE"
    echo ""
fi

# Show the tag to the user
echo -e "Latest tag is: \033[0;32m$TAG\033[0m"
read -p "Is this the correct tag? (yes/no): " answer

if [[ "$answer" != "yes" && "$answer" != "y" ]]; then
  echo "Time to fix the tag."
  exit 1
fi

# Upload the release using the ghr tool
ghr_output=$(ghr ${TAG} ${FILE} 2>&1)

if [[ $? -ne 0 ]]; then
  echo -e "\033[0;31mUpload failed with error: $ghr_output\033[0m"
  exit 1
else
  echo -e "\n\033[0;32mRelease uploaded successfully!\033[0m"
  echo -e "\033[0;32mhttps://github.com/pbreault/adb-idea/releases/tag/${TAG}\033[0m"
fi


