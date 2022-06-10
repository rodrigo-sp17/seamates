import 'package:json_annotation/json_annotation.dart';

part 'user_request.g.dart';

@JsonSerializable()
class UserRequest {
  String username;
  String name;
  String email;
  String password;
  String confirmPassword;

  UserRequest(this.username, this.name, this.email, this.password,
      this.confirmPassword);

  factory UserRequest.empty() {
    return UserRequest("", "", "", "", "");
  }

  factory UserRequest.fromJson(Map<String, dynamic> json) =>
      _$UserRequestFromJson(json);
  Map<String, dynamic> toJson() => _$UserRequestToJson(this);
}
