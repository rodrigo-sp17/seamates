import 'dart:core';

import 'package:sea_mates/data/shift.dart';

abstract class ShiftRemoteRepository {
  Future<List<Shift>> loadRemote(String token);

  /// Adds a shift to the remote repository
  Future<List<Shift>> addRemote(Shift shift, String token);

  /// Removes a shift from the remote repository
  Future<bool> removeRemote(int shiftIs, String token);

  /// Saves shifts to the remote repository
  ///
  /// WARNING: is not atomic - will return an error even in partial success
  Future<List<Shift>> saveRemote(Iterable<Shift> shifts, String token);
}
