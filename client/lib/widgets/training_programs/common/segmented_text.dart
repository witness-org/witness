import 'package:client/extensions/string_extensions.dart';
import 'package:flutter/material.dart';

class SegmentedText extends StatelessWidget {
  const SegmentedText({final this.baseStyle, required final this.segments, final this.skipEmpty = true, final Key? key}) : super(key: key);

  final TextStyle? baseStyle;
  final List<TextSegment> segments;
  final bool skipEmpty;

  List<TextSpan> _textsFromSegments(final List<TextSegment> segments) {
    final spans = <TextSpan>[];

    for (final segment in segments) {
      if (segment.text != null && (segment.text!.isNotEmpty || !skipEmpty)) {
        spans.add(
          TextSpan(
            text: StringExtensions.join(
              [
                segment.prefix,
                segment.text,
                segment.suffix,
              ],
            ),
            style: segment.style,
          ),
        );
      }
    }

    return spans;
  }

  @override
  Widget build(final BuildContext context) {
    return RichText(
      text: TextSpan(
        style: baseStyle,
        children: _textsFromSegments(segments),
      ),
    );
  }
}

class TextSegment {
  const TextSegment(final this.text, {final this.style, final this.prefix, final this.suffix});

  final String? text;
  final TextStyle? style;
  final String? prefix;
  final String? suffix;
}
