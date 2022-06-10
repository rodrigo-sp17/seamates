import 'package:hive/hive.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:sea_mates/data/sync_status.dart';

part 'shift.g.dart';

@JsonSerializable()
@HiveType(typeId: 0)
class Shift {
  @JsonKey(name: 'shiftId')
  int? id;

  @HiveField(0)
  DateTime unavailabilityStartDate;

  @HiveField(1)
  DateTime boardingDate;

  @HiveField(2)
  DateTime leavingDate;

  @HiveField(3)
  DateTime unavailabilityEndDate;

  @JsonKey(ignore: true)
  @HiveField(4)
  SyncStatus syncStatus;

  int? cycleDays = 0;
  int? repeat = 0;

  Shift({
    DateTime? unavailabilityStartDate,
    DateTime? boardingDate,
    DateTime? leavingDate,
    DateTime? unavailabilityEndDate,
    SyncStatus? syncStatus,
  })  : this.syncStatus = syncStatus ?? SyncStatus.UNSYNC,
        this.unavailabilityStartDate =
            unavailabilityStartDate ?? DateTime.now(),
        this.boardingDate = boardingDate ?? DateTime.now(),
        this.leavingDate = leavingDate ?? DateTime.now(),
        this.unavailabilityEndDate = unavailabilityEndDate ?? DateTime.now();

  Shift.id(
    this.id,
    this.unavailabilityStartDate,
    this.boardingDate,
    this.leavingDate,
    this.unavailabilityEndDate,
    this.syncStatus,
  );

  factory Shift.empty() {
    return Shift.id(null, DateTime.now(), DateTime.now(), DateTime.now(),
        DateTime.now(), SyncStatus.UNSYNC);
  }

  factory Shift.fromJson(Map<String, dynamic> json) => _$ShiftFromJson(json);
  Map<String, dynamic> toJson() => _$ShiftToJson(this);
}
