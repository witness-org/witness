import 'package:json_annotation/json_annotation.dart';

part 'greeting.g.dart';

@JsonSerializable()
class Greeting {
  const Greeting({required final this.id, required final this.content});

  factory Greeting.fromJson(final Map<String, dynamic> json) => _$GreetingFromJson(json);

  final int id;
  final String content;

  Map<String, dynamic> toJson() => _$GreetingToJson(this);
}
