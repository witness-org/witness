# witness (client)

This guide gives a short overview of what is necessary to successfully build and run `witness`' server application.

## Prerequisites

* [Flutter](https://flutter.dev/) SDK 2.8
* [Dart](https://dart.dev/) SDK 2.15 (included in Flutter SDK)
* [Android Studio](https://developer.android.com/studio)

## General

The application uses the Flutter SDK and follows common conventions for such project. The [pubspec.yaml](pubspec.yaml)
contains the dependencies. Platform-dependent configurations are found in the respective directories (e.g. `Kotlin`
and `Gradle` versions [android/build.gradle](android/build.gradle)).

## Getting Started

In order to successfully establish a communication channel with the Firebase authentication server, one needs a private
key. For security reasons, they are _not_ checked into the `git` repository. They may only be obtained from trusted
project maintainers via secure means (e.g. no public chats or unencrypted emails).

Therefore, all steps below that refer to actually communicating with the authentication server (logging in, making
requests) can only be successfully re-enacted if you have already obtained such a private key.

They are configured with the help of platform-specific configuration files. Presently, there are only configuration
files for development use in the client project - production files will follow soon, including a way to manage and
switch between different execution environments. They are to be placed in the following locations:

* `android/app/google-services.json`
* `ios/Runner/GoogleService-Info.plist`
* `web/firebase-config.js`

## Assets

The [assets](assets) directory houses images, fonts, and any other files used by the application. At the current point
in time, they are not [resolution-aware](https://flutter.dev/docs/development/ui/assets-and-images#resolution-aware)
yet.

## Localization

This project generates localized messages based on [ARB](https://localizely.com/flutter-arb/) files found in
the [lib/src/localization](lib/localization)
directory to smoothly support multiple languages. Presently, English is the only supported UI language. More information
in the
official [internationalization guide](https://flutter.dev/docs/development/accessibility-and-localization/internationalization)
provided by the Flutter team.

## Run

This project relies heavily on code generation, both
for [JSON (de-)serialization](https://docs.flutter.dev/development/data-and-backend/json#serializing-json-using-code-generation-libraries)
as well as for [`mockito`'s](https://github.com/dart-lang/mockito) null safety support. Therefore, the cleanest way to
run the application is the following:

1. Make sure Flutter and Dart are installed correctly (the command below should terminate without major problems)

```shell
flutter doctor -v
```

2. optional: Verify previous build artifacts are removed

```shell
flutter clean
```

3. Fetch and install dependencies

```shell
flutter pub get
```

4. Generate models and mocks (the `--delete-conflicting-outputs` flag tells the code generator to overwrite files from
   previous generation outputs)

```shell
flutter pub run build_runner build --delete-conflicting-outputs
```

5. Start an emulator and run the application

```shell
flutter run
```

**Note:** Localized messages based on the `ARB` files are only generated upon build. Therefore, if you have never run
the application or added new keys to an `ARB` file, it is possible that your IDE shows error messages in those
locations. They should vanish as soon as you start the application.

If they do persist, the application should - despite IDE errors - run flawlessly. In that case, you could restart
the `Dart Analysis Service` or - if that does not help, either - restart your IDE instance.

There is also an IntelliJ run configuration, `ClientApplication`, which starts up the client app on the configured
device.

## Test

Flutter has three different types of test: [unit, widget, integration](https://docs.flutter.dev/testing). Unit and
widget tests are run headless and integration tests preferably on a physical device or in an emulator.

Tests in the `integration_test` directory are run as integration test (in the configured device, e.g. emulator) and
tests in the `test` directory are pumped as widget or unit tests. This is default behaviour of the Flutter SDK which
cannot be changed.

At the current point, we have not been able to allocate enough resources to run integration tests in a headless Android
emulator within a Docker image in the CI build pipeline. Therefore, until that has been done, all our tests are located
in the `tests` directory and integration tests also run as widget tests in the CI environment.

All the tests may be executed with

```shell
flutter test
```

In order to execute integration tests in an emulator as "actual" integration tests locally, change into
the [test/integration](test/integration) directory and run [run_on_device.bat](test/integration/run_on_device.bat),
[run_on_device.ps1](test/integration/run_on_device.ps1) or [run_on_device.sh](test/integration/run_on_device.sh)
depending on your operating system and preferred shell.

## Package

In order to generate a release build, issue

```shell
flutter build apk --release
```

This generates a release version of the Android app to `build/app/outputs/flutter-apk/app-release.apk` which may be
installed on emulated or physical devices.