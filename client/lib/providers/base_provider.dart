import 'package:flutter/cupertino.dart';
import 'package:logger/logger.dart';

abstract class BaseProvider with ChangeNotifier {
  BaseProvider(this._logger);

  final Logger _logger;
  bool _notifierDisposed = false;

  @override
  void dispose() {
    super.dispose();
    _notifierDisposed = true;
  }

  @override
  void notifyListeners() {
    if (!_notifierDisposed) {
      super.notifyListeners();
    } else {
      _logger.w('Cancelling listener notification because ChangeNotifier has already been disposed.');
    }
  }
}
