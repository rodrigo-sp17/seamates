// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sync_status.dart';

// **************************************************************************
// TypeAdapterGenerator
// **************************************************************************

class SyncStatusAdapter extends TypeAdapter<SyncStatus> {
  @override
  final int typeId = 1;

  @override
  SyncStatus read(BinaryReader reader) {
    switch (reader.readByte()) {
      case 0:
        return SyncStatus.SYNC;
      case 1:
        return SyncStatus.UNSYNC;
      default:
        return SyncStatus.SYNC;
    }
  }

  @override
  void write(BinaryWriter writer, SyncStatus obj) {
    switch (obj) {
      case SyncStatus.SYNC:
        writer.writeByte(0);
        break;
      case SyncStatus.UNSYNC:
        writer.writeByte(1);
        break;
    }
  }

  @override
  int get hashCode => typeId.hashCode;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SyncStatusAdapter &&
          runtimeType == other.runtimeType &&
          typeId == other.typeId;
}
