#!/usr/bin/env bash

CHROME_DRIVER_VERSION=`curl --silent --show-error https://chromedriver.storage.googleapis.com/LATEST_RELEASE`

wget --quiet --show-progress --output-document=./chromedriver.zip https://chromedriver.storage.googleapis.com/$CHROME_DRIVER_VERSION/chromedriver_linux64.zip

unzip ./chromedriver.zip -d ./
rm ./chromedriver.zip
sudo mv -f ./chromedriver /usr/local/bin/chromedriver
sudo chown root:root /usr/local/bin/chromedriver
sudo chmod 0755 /usr/local/bin/chromedriver