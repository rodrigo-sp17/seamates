// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'shift.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class ShiftAdapter extends TypeAdapter<Shift> {
  @override
  final int typeId = 0;

  @override
  Shift read(BinaryReader reader) {
    final numOfFields = reader.readByte();
    final fields = <int, dynamic>{
      for (int i = 0; i < numOfFields; i++) reader.readByte(): reader.read(),
    };
    return Shift(
      unavailabilityStartDate: fields[0] as DateTime?,
      boardingDate: fields[1] as DateTime?,
      leavingDate: fields[2] as DateTime?,
      unavailabilityEndDate: fields[3] as DateTime?,
      syncStatus: fields[4] as SyncStatus?,
    );
  }

  @override
  void write(BinaryWriter writer, Shift obj) {
    writer
      ..writeByte(5)
      ..writeByte(0)
      ..write(obj.unavailabilityStartDate)
      ..writeByte(1)
      ..write(obj.boardingDate)
      ..writeByte(2)
      ..write(obj.leavingDate)
      ..writeByte(3)
      ..write(obj.unavailabilityEndDate)
      ..writeByte(4)
      ..write(obj.syncStatus);
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is ShiftAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Shift _$ShiftFromJson(Map<String, dynamic> json) {
  return Shift(
    unavailabilityStartDate: json['unavailabilityStartDate'] == null
        ? null
        : DateTime.parse(json['unavailabilityStartDate'] as String),
    boardingDate: json['boardingDate'] == null
        ? null
        : DateTime.parse(json['boardingDate'] as String),
    leavingDate: json['leavingDate'] == null
        ? null
        : DateTime.parse(json['leavingDate'] as String),
    unavailabilityEndDate: json['unavailabilityEndDate'] == null
        ? null
        : DateTime.parse(json['unavailabilityEndDate'] as String),
  )
    ..id = json['shiftId'] as int?
    ..cycleDays = json['cycleDays'] as int?
    ..repeat = json['repeat'] as int?;
}

Map<String, dynamic> _$ShiftToJson(Shift instance) => <String, dynamic>{
      'shiftId': instance.id,
      'unavailabilityStartDate':
          instance.unavailabilityStartDate.toIso8601String(),
      'boardingDate': instance.boardingDate.toIso8601String(),
      'leavingDate': instance.leavingDate.toIso8601String(),
      'unavailabilityEndDate': instance.unavailabilityEndDate.toIso8601String(),
      'cycleDays': instance.cycleDays,
      'repeat': instance.repeat,
    };
