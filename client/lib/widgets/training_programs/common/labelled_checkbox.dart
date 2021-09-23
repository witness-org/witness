import 'package:flutter/material.dart';

class LabelledCheckbox extends StatefulWidget {
  const LabelledCheckbox({
    required final this.label,
    required final this.value,
    required final this.onCheckedChanged,
    final Key? key,
  }) : super(key: key);

  final String label;
  final bool? value;
  final Function(bool newValue) onCheckedChanged;

  @override
  _LabelledCheckboxState createState() => _LabelledCheckboxState(); // ignore: library_private_types_in_public_api
}

class _LabelledCheckboxState extends State<LabelledCheckbox> {
  late bool? _isChecked;

  @override
  void initState() {
    super.initState();
    _isChecked = widget.value;
  }

  void _setChecked(final bool isChecked) {
    setState(() {
      _isChecked = isChecked;
      widget.onCheckedChanged(isChecked);
    });
  }

  @override
  Widget build(final BuildContext context) {
    return InkWell(
      onTap: () => _setChecked(_isChecked == true ? false : true),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(widget.label),
          Checkbox(
            value: _isChecked,
            onChanged: (final newValue) => _setChecked(newValue == true),
            materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            visualDensity: const VisualDensity(horizontal: -4, vertical: -2.75),
          ),
        ],
      ),
    );
  }
}
