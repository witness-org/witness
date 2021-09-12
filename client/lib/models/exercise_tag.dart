import 'package:flutter/cupertino.dart';

class ExerciseTag {
  final int id;
  final String name;

  const ExerciseTag(this.id, this.name);

  @override
  int get hashCode {
    return hashValues(id, name);
  }

  @override
  bool operator ==(Object other) {
    return other is ExerciseTag && other.id == id && other.name == name;
  }
}
