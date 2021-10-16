import 'package:client/configuration/development_configuration.dart';
import 'package:client/configuration/production_configuration.dart';
import 'package:flutter/foundation.dart';

/// Defines an interface that provides access to application-wide configuration properties that are to be globally consumed.
/// Exposes a getter, [ClientConfiguration.instance], that returns the applicable implementation, based on environment conditions.
/// Implementations should, just like this abstract class, be accessible via an implementation of the singleton pattern only.
abstract class ClientConfiguration {
  factory ClientConfiguration._createInstance() {
    return kDebugMode ? DevelopmentConfiguration.instance : ProductionConfiguration.instance;
  }

  static final ClientConfiguration _instance = ClientConfiguration._createInstance();

  /// Returns the [ClientConfiguration] implementation relevant to the application. If it was compiled in debug mode, the [DevelopmentConfiguration]
  /// instance is returned. For release and profile builds, the [ProductionConfiguration] instance is returned.
  static ClientConfiguration get instance {
    return _instance;
  }

  /// Specifies the host (i.e. hostname and optional port, separated by `:`) of the server whose REST API is consumed by the application. This
  /// property must not include a scheme specification (e.g. `http://` or `https://`). For that purpose, [ClientConfiguration.useHttps] denotes
  /// the protocol to be used.
  String get apiHost;

  /// Determines whether HTTPS should be used when sending server API requests to the host specified by [ClientConfiguration.apiHost].
  bool get useHttps;
}
