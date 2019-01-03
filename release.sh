#!/bin/sh
RELEASE="$1"
GITHUB_TOKEN="$2"
REPO="$3"
CHANGELOG=`awk -v version="$RELEASE" '/### Release / {printit = $3 == version}; printit;' 'CHANGELOG.md'`
mkdir release
docker run --rm -v "$PWD":/home/gradle/ -w /home/gradle/ android-build zipalign -p 4 app/build/outputs/apk/release/app-release-unsigned.apk release/MEWconnect-Android-$RELEASE-unsigned.apk
docker run --rm -v "$PWD":/home/gradle/ -w /home/gradle/ android-build apksigner sign --out release/MEWconnect-Android-$RELEASE.apk --ks MEWconnect-keystore.jks --ks-key-alias mewconnect --ks-pass pass:$KEYSTORE_PASS --key-pass pass:$KEY_PASS release/MEWconnect-Android-$RELEASE-unsigned.apk
if [ -n "$4" ]; then
    for f in release/*; do
        gpg --output $f.sig --detach-sig $f
    done
fi
cd release && sha256sum * > MEWconnect-Android-$RELEASE-CHECKSUM-SHA256 && cd ..
GH_API="https://api.github.com"
GH_REPO="$GH_API/repos/$REPO"
AUTH="Authorization: token ${GITHUB_TOKEN}"
GH_TAGS="$GH_REPO/releases/tags/$RELEASE"
payload=$(
    jq --null-input \
    --arg tag "$RELEASE" \
    --arg name "MEWconnect Android $RELEASE" \
    --arg body "$CHANGELOG" \
    '{ tag_name: $tag, name: $name, body: $body, draft: false }'
)

response=$(
    curl -H "$AUTH" \
    --fail \
    --silent \
    --location \
    --data "$payload" \
    "https://api.github.com/repos/${REPO}/releases"
)
upresponse=$(curl -sH "$AUTH" $GH_TAGS)
eval $(echo "$upresponse" | grep -m 1 "id.:" | grep -w id | tr : = | tr -cd '[[:alnum:]]=')
[ "$id" ] || { echo "Error: Failed to get release id for tag: $RELEASE"; echo "$upresponse" | awk 'length($0)<100' >&2; exit 1; }

for file in ./release/*; do
    curl -H "$AUTH" \
    --header "Content-Type: application/octet-stream" \
    --data-binary "@$file" \
    --fail \
    --silent \
    --location \
    "https://uploads.github.com/repos/${REPO}/releases/$id/assets?name=$(basename $file)"
done