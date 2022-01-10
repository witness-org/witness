import 'package:client/models/common/tz_date_time_converter.dart';
import 'package:client/models/user/role.dart';
import 'package:client/models/user/sex.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';

part 'user.g.dart';

@JsonSerializable()
@TZDateTimeConverter()
class User {
  const User({
    required final this.id,
    required final this.firebaseId,
    required final this.username,
    required final this.email,
    required final this.role,
    required final this.sex,
    required final this.createdAt,
    required final this.modifiedAt,
    required final this.height,
  });

  factory User.fromJson(final Map<String, dynamic> json) => _$UserFromJson(json);

  final int id;
  final String firebaseId;
  final String username;
  final String email;
  final Role? role;
  final Sex sex;
  final TZDateTime createdAt;
  final TZDateTime modifiedAt;
  final int height;

  Map<String, dynamic> toJson() => _$UserToJson(this);
}
