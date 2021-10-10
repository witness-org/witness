import 'package:client/models/greeting.dart';
import 'package:client/providers/greeting_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

class ApiConsumerShowcase extends StatelessWidget {
  const ApiConsumerShowcase({final Key? key}) : super(key: key);

  void _showResult(final BuildContext context, final String result) {
    showDialog<void>(
      context: context,
      builder: (final ctx) => AlertDialog(
        title: const Text("API Response"),
        content: SelectableText(result),
        actions: [
          TextButton(
            onPressed: () => Clipboard.setData(ClipboardData(text: result)),
            child: const Text("COPY"),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text("OK"),
          ),
        ],
      ),
    );
  }

  void fetchData(final BuildContext context, final Future<String> fetcher) {
    ProgressLoader()
        .show(context)
        .then((final _) => fetcher)
        .then((final result) => _showResult(context, result))
        .then((final _) => ProgressLoader().dismiss());
  }

  @override
  Widget build(final BuildContext context) {
    final greetings = Provider.of<GreetingProvider>(context, listen: false);
    return Column(
      children: [
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getPublicData()),
          child: const Text('GET Public Data'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getRegisteredData('registered user')),
          child: const Text('GET Registered Data'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getRegisteredData('registered user', false)),
          child: const Text('GET Registered Data (Without Token)'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getPremiumData()),
          child: const Text('GET Premium Data'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getAdminData()),
          child: const Text('GET Admin Data'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getPremiumOrAdminData()),
          child: const Text('GET Premium/Admin Data'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.getRegisteredData(' ')),
          child: const Text('Validation Error Request Parameter'),
        ),
        ElevatedButton(
          onPressed: () => fetchData(context, greetings.postData(const Greeting(id: -1, content: ' '))),
          child: const Text('Validation Error Request Body'),
        ),
      ],
    );
  }
}
