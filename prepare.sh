#!/bin/bash

function git_checkout() {
  tag=$1
  url=$2
  dir_name=$3
  if [ -d "$dir_name" ] ; then
    echo "Removing $dir_name"
    rm -rf "$dir_name"
  fi
  git clone --depth 1 --branch "$tag" -c advice.detachedHead=false "$url" "$dir_name"
}

git_checkout v1.6.3 https://git.kernel.org/pub/scm/libs/libgpiod/libgpiod.git libgpiod


echo "Done"
