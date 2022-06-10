import 'package:hive/hive.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:sea_mates/data/user.dart';

part 'auth_user.g.dart';

@JsonSerializable()
@HiveType(typeId: 2)
class AuthenticatedUser implements User {
  @JsonKey(name: 'userId')
  @HiveField(0)
  int id;

  @HiveField(1)
  String username;

  @HiveField(2)
  String name;

  @HiveField(3)
  String email;

  @HiveField(4)
  @JsonKey(ignore: true)
  String token;

  AuthenticatedUser(
      {required this.id,
      required this.username,
      required this.name,
      required this.email,
      String? token})
      : this.token = token ?? "";

  AuthenticatedUser.full(
      this.id, this.username, this.name, this.email, this.token);

  factory AuthenticatedUser.fromJson(Map<String, dynamic> json) =>
      _$AuthenticatedUserFromJson(json);
  Map<String, dynamic> toJson() => _$AuthenticatedUserToJson(this);

  factory AuthenticatedUser.fromAppUserJson(Map<String, dynamic> json) {
    return AuthenticatedUser(
        id: json['userId'],
        username: json['userInfo']['username'],
        name: json['userInfo']['name'],
        email: json['userInfo']['email']);
  }

  @override
  bool isLocalUser() => false;
}
