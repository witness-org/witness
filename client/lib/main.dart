import 'package:flutter/material.dart';

void main() => runApp(const CounterApp());

class CounterApp extends StatelessWidget {
  const CounterApp({final Key? key}) : super(key: key);

  @override
  Widget build(final BuildContext context) {
    return const MaterialApp(
      title: 'Counter App',
      home: CounterAppPage(title: 'Counter App Home Page'),
    );
  }
}

class CountHolder {
  int value = 0;

  void increment() => value++;

  void decrement() => value--;
}

class CounterAppPage extends StatefulWidget {
  const CounterAppPage({final Key? key, required final this.title}) : super(key: key);

  final String title;

  @override
  // ignore: library_private_types_in_public_api
  _CounterAppPageState createState() => _CounterAppPageState();
}

class _CounterAppPageState extends State<CounterAppPage> {
  final CountHolder _counter = CountHolder();

  void _incrementCounter() {
    setState(() {
      _counter.increment();
    });
  }

  @override
  Widget build(final BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text('You have pushed the button this many times:'),
            Text(
              '${_counter.value}',
              // Provide a Key to this specific Text widget. This allows
              // identifying the widget from inside the test suite,
              // and reading the text.
              key: const Key('counter'),
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        // Provide a Key to this button. This allows finding this
        // specific button inside the test suite, and tapping it.
        key: const Key('increment'),
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}
