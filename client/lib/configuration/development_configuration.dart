import 'package:client/configuration/client_configuration.dart';

/// Holds application-wide property values as defined by [ClientConfiguration], applicable for when the application was compiled neither in release
/// nor in profile mode. The public interface is exposed via a singleton implementation, accessible through [DevelopmentConfiguration.instance].
class DevelopmentConfiguration implements ClientConfiguration {
  DevelopmentConfiguration._();

  static final DevelopmentConfiguration _instance = DevelopmentConfiguration._();

  static DevelopmentConfiguration get instance {
    return _instance;
  }

  @override
  String get apiHost {
    return '10.0.2.2:8080';
  }

  @override
  bool get useHttps {
    return false;
  }
}
