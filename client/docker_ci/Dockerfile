FROM cirrusci/flutter:2.5.2

RUN sudo apt update && sudo apt install -y gdebi-core libnss3 libgconf-2-4
RUN wget --quiet --show-progress --output-document=./chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN sudo gdebi -n ./chrome.deb

WORKDIR /app
RUN export DRIVER_VERSION=$(curl --silent --show-error https://chromedriver.storage.googleapis.com/LATEST_RELEASE); \
    wget --quiet --show-progress --output-document=./chromedriver.zip https://chromedriver.storage.googleapis.com/$DRIVER_VERSION/chromedriver_linux64.zip
RUN unzip ./chromedriver.zip -d ./
RUN rm ./chromedriver.zip
RUN sudo chmod +x chromedriver

RUN flutter doctor -v