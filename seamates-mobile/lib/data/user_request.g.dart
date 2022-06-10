// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserRequest _$UserRequestFromJson(Map<String, dynamic> json) {
  return UserRequest(
    json['username'] as String,
    json['name'] as String,
    json['email'] as String,
    json['password'] as String,
    json['confirmPassword'] as String,
  );
}

Map<String, dynamic> _$UserRequestToJson(UserRequest instance) =>
    <String, dynamic>{
      'username': instance.username,
      'name': instance.name,
      'email': instance.email,
      'password': instance.password,
      'confirmPassword': instance.confirmPassword,
    };
