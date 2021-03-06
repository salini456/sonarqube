#!/bin/bash
set +x

VERSION="\[RELEASE\]"
HTTP_CODE=$(\
  curl \
    --write-out '%{http_code}' \
    --location \
    --remote-name \
    --user "$ARTIFACTORY_PRIVATE_USERNAME:$ARTIFACTORY_API_KEY" \
    "$ARTIFACTORY_URL/sonarsource-private-releases/com/sonarsource/iris/iris/$VERSION/iris-$VERSION-jar-with-dependencies.jar"\
)

if [ "$HTTP_CODE" != "200" ]; then
  echo "Download $VERSION failed -> $HTTP_CODE"
  exit 1
else
  echo "Downloaded $VERSION"
fi

java \
  -Diris.source.projectKey=org.sonarsource.sonarqube:sonarqube-private \
  -Diris.source.url=https://next.sonarqube.com/sonarqube \
  -Diris.source.token=$NEXT_TOKEN \
  -Diris.destination.projectKey=sonarqube \
  -Diris.destination.url=https://next.sonarqube.com/sonarqube \
  -Diris.destination.token=$NEXT_TOKEN \
  -Diris.maxcountposts=50 \
  -jar iris-\[RELEASE\]-jar-with-dependencies.jar
