import 'package:client/extensions/string_extensions.dart';
import 'package:flutter/material.dart';

class SegmentedText extends StatelessWidget {
  final TextStyle? baseStyle;
  final List<TextSegment> segments;
  final bool skipEmpty;

  const SegmentedText({this.baseStyle, required this.segments, this.skipEmpty = true, Key? key}) : super(key: key);

  List<TextSpan> _textsFromSegments(List<TextSegment> segments) {
    var spans = <TextSpan>[];

    segments.forEach((segment) {
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
    });

    return spans;
  }

  @override
  Widget build(BuildContext context) {
    return RichText(
      text: TextSpan(
        style: baseStyle,
        children: _textsFromSegments(segments),
      ),
    );
  }
}

class TextSegment {
  final String? text;
  final TextStyle? style;
  final String? prefix;
  final String? suffix;

  const TextSegment(this.text, {this.style, this.prefix, this.suffix});
}
