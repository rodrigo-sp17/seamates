import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/shift.dart';

/// Class that represents a [AuthenticatedUser] friend with his/her corresponding
/// shifts
/// Intended for parsing web data only
class Friend {
  late AuthenticatedUser user;
  List<Shift> shifts;

  Friend._() : shifts = [];

  factory Friend.fromJson(Map<String, dynamic> json) {
    Friend friend = new Friend._();
    friend.user = AuthenticatedUser.fromAppUserJson(json);

    List<Shift> result = [];
    Iterable<dynamic> shiftsJson = json['shifts'];
    for (dynamic s in shiftsJson) {
      result.add(Shift.fromJson(s));
    }
    friend.shifts = result;
    return friend;
  }

  bool isAvailable(DateTime date) {
    for (Shift s in shifts) {
      if (!s.unavailabilityStartDate.isAfter(date) &&
          !s.unavailabilityEndDate.isBefore(date)) {
        return false;
      }
    }
    return true;
  }
}

class FriendRequest {
  int id;
  String sourceUsername;
  String sourceName;
  String targetUsername;
  String targetName;
  DateTime timestamp;

  FriendRequest(this.id, this.sourceUsername, this.sourceName,
      this.targetUsername, this.targetName, this.timestamp);

  factory FriendRequest.fromJson(Map<String, dynamic> json) {
    var request = FriendRequest(
        json['id'],
        json['sourceUsername'],
        json['sourceName'],
        json['targetUsername'],
        json['targetName'],
        DateTime.parse(json['timestamp'] as String));
    return request;
  }
}
