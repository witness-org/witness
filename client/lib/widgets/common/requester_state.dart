import 'package:client/services/server_response.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';

/// Abstract state that provides functions for handling server requests.
/// [TWidget] stands for the stateful widget the state is for (a subclass of [StatefulWidget]). [TSuccess] is the class to which a server response is
/// deserialized if a request was successful (see also [ServerResponse]).
///
/// Use the state as follows:
/// ```
/// class Example extends StatefulWidget {
///
///  @override
//   State<StatefulWidget> createState() => _ExampleState();
///
/// }
///
/// class _ExampleState extends RequesterState<Example, ExampleResponse> {
///
/// Future<void> _sendExampleRequest(final BuildContext context) async {
///    submitRequestWithResponse(
///       context,
///       () => Provider.of<ExampleProvider>(context, listen: false).exampleRequest(),
///       (final _success) => _doSomethingWithResponse(_success),
///       defaultErrorMessage: 'Something went wrong!',
///       showProgress: false,
///     );
///   }
///
///  ...
///
/// }
/// ```
abstract class RequesterState<TWidget extends StatefulWidget, TSuccess> extends State<TWidget> with StringLocalizer {
  /// Executes the function provided by [request] which represents a server request that is expected to return a response (i.e. the success type is
  /// not `void` in the [ServerResponse]) and handles the response.
  /// If the request was successful, the function given by the [successAction] parameter is executed (if it is not `null`). Otherwise (i.e. the
  /// request was not successful), the returned error is shown in a snack bar. In the case that there is no error message from the server,
  /// [defaultErrorMessage] is displayed if it is provided; otherwise the snack bar contains a generic error message.
  /// If [showProgressLoader] is set to `true`, a progress loader is shown before submitting the request and dismissed after having received a
  /// response in order to indicate to the user that the request is in progress.
  Future<void> submitRequestWithResponse(
    final Future<ServerResponse<TSuccess, String>> Function() request, {
    void Function(TSuccess success)? successAction,
    final String? defaultErrorMessage,
    final bool showProgressLoader = true,
  }) async {
    successAction ??= (final _) {};
    _submitRequest<TSuccess>(
      request,
      (final response) => response.isSuccessAndResponse,
      (final response) => successAction!(response.success as TSuccess),
      showProgressLoader: showProgressLoader,
    );
  }

  /// Executes the function provided by [request] which represents a server request that is expected to return no response (i.e. the success type is
  /// `void` in the [ServerResponse]) and handles the response.
  /// If the request was successful, the function given by the [successAction] parameter is executed (if it is not `null`). Otherwise (i.e. the
  /// request was not successful), the returned error is shown in a snack bar. In the case that there is no error message from the server,
  /// [defaultErrorMessage] is displayed if it is provided; otherwise the snack bar contains a generic error message.
  /// If [showProgressLoader] is set to `true`, a progress loader is shown before submitting the request and dismissed after having received a
  /// response in order to indicate to the user that the request is in progress.
  Future<void> submitRequestWithoutResponse(
    final Future<ServerResponse<void, String>> Function() request, {
    void Function()? successAction,
    final String? defaultErrorMessage,
    final bool showProgressLoader = true,
  }) async {
    successAction ??= () {};
    _submitRequest(
      request,
      (final response) => response.isSuccessNoResponse,
      (final _) => successAction!(),
      showProgressLoader: showProgressLoader,
    );
  }

  /// Executes the function provided by [request] which represents a server request that may return a response (i.e. the generic success type
  /// [TResponse] in the [ServerResponse] might also be `void`) and handles the response.
  /// If the request was successful, the function given by the [successAction] parameter is executed, with the returned [ServerResponse] as argument.
  /// Otherwise (i.e. if the request was not successful), the returned error is shown in a snack bar. In the case that there is no error message from
  /// the server, [defaultErrorMessage] is displayed if it is provided; otherwise the snack bar contains a generic error message.
  /// If [showProgressLoader] is set to `true`, a progress loader is shown before submitting the request and dismissed after having received a
  /// response in order to indicate to the user that the request is in progress.
  Future<void> _submitRequest<TResponse>(
    final Future<ServerResponse<TResponse, String>> Function() request,
    final bool Function(ServerResponse<TResponse, String> response) successCondition,
    final void Function(ServerResponse<TResponse, String> response) successAction, {
    String? defaultErrorMessage,
    final bool showProgressLoader = true,
  }) async {
    final uiStrings = getLocalizedStrings(context);
    defaultErrorMessage ??= uiStrings.all_defaultErrorMessage;

    if (showProgressLoader) {
      await ProgressLoader().show(context);
    }

    final response = await request();

    if (showProgressLoader) {
      await ProgressLoader().dismiss();
    }

    if (successCondition(response)) {
      if (!mounted) {
        return;
      }

      successAction(response);
    } else {
      showError(response.error != null ? response.error! : defaultErrorMessage);
    }
  }

  /// Displays a snack bar containing the error message given in the [text] parameter.
  void showError(final String text) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(text)),
    );
  }
}
