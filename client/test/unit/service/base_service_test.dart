import 'package:client/configuration/client_configuration.dart';
import 'package:client/services/base_service.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../common/test_helpers.dart';

const _sutName = 'base_service';
const _stubService = StubService();

void main() {
  void expectUri(final Uri generatedUri, final String expectedPath) {
    final protocol = ClientConfiguration.instance.useHttps ? 'https://' : 'http://';
    final host = ClientConfiguration.instance.apiHost;
    expect(generatedUri.toString(), '$protocol$host$expectedPath');
  }

  group(getPrefixedGroupName(_sutName, 'getUri'), () {
    group('skipTargetResource = false', () {
      test('should return URI without trailing slash on empty string', () {
        expectUri(_stubService.getUri(''), '/stubs');
      });

      test('should return concatenated path with resource as argument', () {
        expectUri(_stubService.getUri('public'), '/stubs/public');
      });

      test('should return concatenated path with nested resource as argument', () {
        expectUri(_stubService.getUri('greeting/public'), '/stubs/greeting/public');
      });

      test('should return concatenated path including escaped space in query parameter with resource and map as argument', () {
        expectUri(
          _stubService.getUri('message/create', queryParameters: {'title': 'Interesting title'}),
          '/stubs/message/create?title=Interesting+title',
        );
      });

      test('should escape spaces in path correctly', () {
        expectUri(_stubService.getUri('path with spaces/1/delete'), '/stubs/path%20with%20spaces/1/delete');
      });
    });

    group('skipTargetResource = true', () {
      test('should return URI without trailing slash on empty string', () {
        expectUri(_stubService.getUri('', skipTargetResource: true), '');
      });

      test('should return concatenated path with resource as argument', () {
        expectUri(_stubService.getUri('public', skipTargetResource: true), '/public');
      });

      test('should return concatenated path with nested resource as argument', () {
        expectUri(_stubService.getUri('greeting/public', skipTargetResource: true), '/greeting/public');
      });

      test('should return concatenated path including escaped space in query parameter with resource and map as argument', () {
        expectUri(
          _stubService.getUri('message/create', skipTargetResource: true, queryParameters: {'title': 'Interesting title'}),
          '/message/create?title=Interesting+title',
        );
      });

      test('should escape spaces in path correctly', () {
        expectUri(_stubService.getUri('path with spaces/1/delete', skipTargetResource: true), '/path%20with%20spaces/1/delete');
      });
    });
  });
}

/// In order to test features of the abstract [BaseService], an implementation which is as simple as possible is needed. This [StubService] provides
/// no additional methods besides the implementation of members required by the contract. It has no state, therefore reusing a single instance
/// of [StubService] across all [BaseService] tests is allowed and safe.
class StubService extends BaseService {
  const StubService() : super('stubs');
}
