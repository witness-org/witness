import 'package:client/configuration/client_configuration.dart';

/// Holds application-wide property values as defined by [ClientConfiguration], applicable for when the application was compiled in in release mode.
/// The public interface is exposed via a singleton implementation, accessible through [ProductionConfiguration.instance].
class ProductionConfiguration implements ClientConfiguration {
  ProductionConfiguration._();

  static final ProductionConfiguration _instance = ProductionConfiguration._();

  static ProductionConfiguration get instance {
    return _instance;
  }

  @override
  String get apiHost {
    throw Exception("Not implemented yet.");
  }

  @override
  bool get useHttps {
    return true;
  }
}
