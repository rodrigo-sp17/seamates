import 'package:json_annotation/json_annotation.dart';

part 'social_user.g.dart';

@JsonSerializable()
class SocialUser {
  String name;
  String email;
  String username;
  String socialId;
  String registrationId;

  SocialUser(
      this.name, this.email, this.username, this.socialId, this.registrationId);

  factory SocialUser.empty() => SocialUser("", "", "", "", "");

  factory SocialUser.fromJson(Map<String, dynamic> json) =>
      _$SocialUserFromJson(json);
  Map<String, dynamic> toJson() => _$SocialUserToJson(this);
}
