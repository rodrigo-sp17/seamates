import 'package:hive/hive.dart';

part 'sync_status.g.dart';

@HiveType(typeId: 1)
enum SyncStatus {
  @HiveField(0)
  SYNC,

  @HiveField(1)
  UNSYNC
}
