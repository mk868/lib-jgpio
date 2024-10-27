#!/bin/bash

function get_jextract() {
  file=openjdk-22-jextract+5-33_linux-x64_bin.tar.gz
  jextract_dir=jextract
  if [ -d "$jextract_dir" ] ; then
    echo "$jextract_dir already exist, skipping"
    exit 0
  fi
  curl -O https://download.java.net/java/early_access/jextract/22/5/"$file"
  tar -xf "$file"
  rm -fv "$file"
  mv jextract-22 "$jextract_dir"
}

get_jextract

echo "Done"
