import 'dart:io';

import 'package:client/configuration/client_configuration.dart';

abstract class BaseService {
  /// Builds a [Map] that represents the HTTP headers to be sent along with an HTTP request. If [authorization] is not `null`, the `Authorization`
  /// header is set to `Bearer $authorization`. If `jsonContent` is `true`, the `Content-Typ` header is set to `application/json; charset=utf-8`.
  Map<String, String> getHttpHeaders({final String? authorization, final bool jsonContent = false}) {
    final headers = <String, String>{};

    _setHeaderConditionally(headers, () => authorization != null, HttpHeaders.authorizationHeader, 'Bearer $authorization');
    _setHeaderConditionally(headers, () => jsonContent, HttpHeaders.contentTypeHeader, ContentType.json.toString());

    return headers;
  }

  /// Returns an [Uri] object to be used when sending a request URL. This method is aware of the application-wide configuration properties (see
  /// [ClientConfiguration.instance]), meaning that the host of the returned [Uri] is equivalent to [ClientConfiguration.apiHost]. The protocol prefix
  /// is `https://` if [ClientConfiguration.useHttps] is `true`, otherwise `http://`.
  ///
  /// Examples:
  /// ```
  /// // apiHost = '10.0.2.2:8080', useHttps = false
  /// getUri('') => http://10.0.2.2:8080
  /// getUri('greeting/public') => http://10.0.2.2:8080/greeting/public
  ///
  /// // apiHost = app.server, useHttps = true
  /// getUri('message/create', {'title': 'Interesting title'})) => https://app.server/message/create?title=Interesting+title
  /// getUri('path with spaces/1') => https://app.server/path%20with%20spaces/1/delete
  /// ```
  Uri getUri(final String unencodedPath, {final Map<String, Object>? queryParameters}) {
    if (ClientConfiguration.instance.useHttps) {
      return Uri.https(ClientConfiguration.instance.apiHost, unencodedPath, queryParameters);
    } else {
      return Uri.http(ClientConfiguration.instance.apiHost, unencodedPath, queryParameters);
    }
  }

  void _setHeaderConditionally(final Map<String, String> headerMap, final bool Function() predicate, final String header, final String value) {
    if (predicate()) {
      headerMap[header] = value;
    }
  }
}
