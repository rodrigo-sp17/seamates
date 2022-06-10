// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'auth_user.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class AuthenticatedUserAdapter extends TypeAdapter<AuthenticatedUser> {
  @override
  final int typeId = 2;

  @override
  AuthenticatedUser read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return AuthenticatedUser(
      id: fields[0] as int,
      username: fields[1] as String,
      name: fields[2] as String,
      email: fields[3] as String,
      token: fields[4] as String?,
    );
  }

  @override
  void write(BinaryWriter writer, AuthenticatedUser obj) {
    writer
      ..writeByte(5)
      ..writeByte(0)
      ..write(obj.id)
      ..writeByte(1)
      ..write(obj.username)
      ..writeByte(2)
      ..write(obj.name)
      ..writeByte(3)
      ..write(obj.email)
      ..writeByte(4)
      ..write(obj.token);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is AuthenticatedUserAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AuthenticatedUser _$AuthenticatedUserFromJson(Map<String, dynamic> json) {
  return AuthenticatedUser(
    id: json['userId'] as int,
    username: json['username'] as String,
    name: json['name'] as String,
    email: json['email'] as String,
  );
}

Map<String, dynamic> _$AuthenticatedUserToJson(AuthenticatedUser instance) =>
    <String, dynamic>{
      'userId': instance.id,
      'username': instance.username,
      'name': instance.name,
      'email': instance.email,
    };
