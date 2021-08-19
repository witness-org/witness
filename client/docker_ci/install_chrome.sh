#!/usr/bin/env bash

sudo apt-get update
sudo apt-get install --yes libxss1 libappindicator1 libindicator7
wget --quiet --show-progress --output-document=./chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
sudo apt-get install --yes ./chrome.deb