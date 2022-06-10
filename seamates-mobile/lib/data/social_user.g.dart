// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'social_user.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

SocialUser _$SocialUserFromJson(Map<String, dynamic> json) {
  return SocialUser(
    json['name'] as String,
    json['email'] as String,
    json['username'] as String,
    json['socialId'] as String,
    json['registrationId'] as String,
  );
}

Map<String, dynamic> _$SocialUserToJson(SocialUser instance) =>
    <String, dynamic>{
      'name': instance.name,
      'email': instance.email,
      'username': instance.username,
      'socialId': instance.socialId,
      'registrationId': instance.registrationId,
    };
