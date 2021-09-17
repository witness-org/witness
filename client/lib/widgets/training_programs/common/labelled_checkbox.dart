import 'package:flutter/material.dart';

class LabelledCheckbox extends StatefulWidget {
  final String label;
  final bool? value;
  final Function(bool newValue) onCheckedChanged;

  const LabelledCheckbox({required this.label, required this.value, required this.onCheckedChanged, Key? key}) : super(key: key);

  @override
  _LabelledCheckboxState createState() => _LabelledCheckboxState();
}

class _LabelledCheckboxState extends State<LabelledCheckbox> {
  late bool? _isChecked;

  @override
  void initState() {
    super.initState();
    _isChecked = widget.value;
  }

  void _setChecked(bool isChecked) {
    setState(() {
      _isChecked = isChecked;
      widget.onCheckedChanged(isChecked);
    });
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () => _setChecked(_isChecked == true ? false : true),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(widget.label),
          Checkbox(
            value: _isChecked,
            onChanged: (newValue) => _setChecked(newValue == true),
            materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
            visualDensity: VisualDensity(horizontal: -4, vertical: -2.75),
          ),
        ],
      ),
    );
  }
}
