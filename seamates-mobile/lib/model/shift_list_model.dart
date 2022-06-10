import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:sea_mates/data/shift.dart';
import 'package:sea_mates/data/sync_status.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/repository/shift_local_repository.dart';
import 'package:sea_mates/repository/shift_remote_repository.dart';
import 'package:sea_mates/strings.i18n.dart';

class ShiftListModel extends ChangeNotifier {
  // Dependencies
  final ShiftRemoteRepository _shiftRemoteRepository;
  final ShiftLocalRepository _shiftLocalRepository;
  late UserModel _userModel;

  ShiftListModel(this._shiftRemoteRepository, this._shiftLocalRepository,
      {List<Shift>? shifts})
      : _shifts = shifts ?? [];

  ShiftListModel update(UserModel userModel) {
    this._userModel = userModel;
    return this;
  }

  // State
  bool _isLoading = false;
  List<Shift> _shifts;
  Set<int> _selectedIds = {};

  bool get isLoading => _isLoading;
  UnmodifiableListView<Shift> get shifts => UnmodifiableListView(_shifts);
  UnmodifiableSetView<int> get selectedIds => UnmodifiableSetView(_selectedIds);

  /// Syncs shifts with remote repository
  Future<String?> syncShifts() async {
    // Sync strategy:
    // - Load both local and remote datasets
    // - Remove SYNCED local data
    // - Add remote data to local data
    // - Add all UN-SYNCED data
    // - Save all UN-SYNCED data to local

    if (!_userModel.hasAuthentication()) {
      return "Sync is unavailable in local mode".i18n;
    }

    _isLoading = true;
    notifyListeners();

    var token = _userModel.getToken();

    String? errorMessage;
    var localShifts = <Shift>[];
    var remoteShifts = <Shift>[];
    try {
      localShifts = await _shiftLocalRepository.loadLocal();
      remoteShifts = await _shiftRemoteRepository.loadRemote(token);
    } on ForbiddenException {
      errorMessage = 'Sync forbidden - please log in again'.i18n;
      _userModel.handleForbidden();
    } on RestException {
      errorMessage = "Failed to fetch data".i18n;
    }

    if (errorMessage != null) {
      _isLoading = false;
      notifyListeners();
      return errorMessage;
    }

    var unSyncShifts = localShifts
        .takeWhile((shift) => shift.syncStatus == SyncStatus.UNSYNC)
        .toList();

    // Replaces local data for remote data
    try {
      await _shiftLocalRepository.removeLocal(localShifts.map((s) => s.id!));
      await _shiftLocalRepository.saveLocal(remoteShifts);
    } on Exception {
      _isLoading = false;
      notifyListeners();
      return 'Failed to sync locally'.i18n;
    }

    // Adds pending shifts to the server
    var added = <Shift>[];
    var remaining = <Shift>[];
    for (Shift s in unSyncShifts) {
      try {
        var addedShifts = await _shiftRemoteRepository.addRemote(s, token);
        added.addAll(addedShifts);
      } on Exception {
        remaining.add(s);
      }
    }
    if (added.isNotEmpty) {
      await _shiftLocalRepository.saveLocal(added);
    }
    if (remaining.isNotEmpty) {
      await _shiftLocalRepository.addLocal(remaining);
    }

    _shifts = await _shiftLocalRepository.loadLocal();

    _isLoading = false;
    notifyListeners();
  }

  /// Adds a new shift
  /// Syncs only upon user request
  /// WARNING: Mutates shift
  Future<void> add(Shift shift) async {
    shift.syncStatus = SyncStatus.UNSYNC;
    var shifts = _calculateRepetitions(shift);
    var addedShifts = await _shiftLocalRepository.addLocal(shifts);
    _shifts.addAll(addedShifts);
    notifyListeners();
  }

  void selectId(int shiftId) {
    _selectedIds.add(shiftId);
    notifyListeners();
  }

  void unselectId(int shiftId) {
    _selectedIds.remove(shiftId);
    notifyListeners();
  }

  void resetSelection() {
    _selectedIds = {};
    notifyListeners();
  }

  /// Removes a shift
  /// Attempts remote deletions as soon as possible
  Future<String?> removeSelected() async {
    List<int> synced = [], unSynced = [];
    _shifts.forEach((shift) {
      var id = shift.id!;
      if (_selectedIds.contains(id)) {
        shift.syncStatus == SyncStatus.SYNC ? synced.add(id) : unSynced.add(id);
      }
    });

    // for un-synced
    if (unSynced.isNotEmpty) {
      await _shiftLocalRepository.removeLocal(unSynced);
      _shifts = await _shiftLocalRepository.loadLocal();
      _selectedIds.removeAll(unSynced);
      notifyListeners();
    }

    // for synced - requires online access
    if (synced.isNotEmpty) {
      if (!_userModel.hasAuthentication()) {
        return 'Synced shifts cannot be deleted when in local mode'.i18n;
      }

      var token = _userModel.getToken();
      _isLoading = true;
      notifyListeners();

      String? errorMsg;
      var removed = <int>[];
      for (int id in synced) {
        try {
          var success = await _shiftRemoteRepository.removeRemote(id, token);
          if (success) removed.add(id);
        } on Exception {
          errorMsg = "Some items could not be deleted".i18n;
        }
      }

      _selectedIds.removeAll(removed);
      await _shiftLocalRepository.removeLocal(removed);
      _shifts = await _shiftLocalRepository.loadLocal();

      _isLoading = false;
      notifyListeners();

      return errorMsg;
    }
  }

  Future<void> clearLocalDatabase() async {
    await _shiftLocalRepository.clear();
  }

  /// Calculates and returns the shifts representing the next cycles based upon
  /// the repeat property of Shift
  ///
  /// The repeat property of the original shift is mutated to 0 to avoid
  /// server duplications
  ///
  /// This method ignores the cycleDays properties - calculations are done based
  /// on available dates
  List<Shift> _calculateRepetitions(Shift shift) {
    int times = shift.repeat!;
    shift.repeat = 0;

    var result = [shift];
    if (times == 0) {
      return result;
    }

    var cycleDays = shift.leavingDate.difference(shift.boardingDate).inDays;
    var beforeDiff =
        shift.boardingDate.difference(shift.unavailabilityStartDate).inDays;
    var afterDiff =
        shift.unavailabilityEndDate.difference(shift.leavingDate).inDays;

    for (int i = 1; i <= times; i++) {
      var nextBoardingDate =
          shift.boardingDate.add(Duration(days: (i * 2 * cycleDays)));
      var nextLeavingDate =
          shift.leavingDate.add(Duration(days: (i * 2 * cycleDays)));
      var nextUnStartDate =
          nextBoardingDate.subtract(Duration(days: beforeDiff));
      var nextUnEndDate = nextLeavingDate.add(Duration(days: afterDiff));

      assert(!nextUnStartDate.isAfter(nextBoardingDate));
      assert(!nextLeavingDate.isBefore(nextBoardingDate));
      assert(!nextUnEndDate.isBefore(nextLeavingDate));

      result.add(new Shift(
          unavailabilityStartDate: nextUnStartDate,
          boardingDate: nextBoardingDate,
          leavingDate: nextLeavingDate,
          unavailabilityEndDate: nextUnEndDate,
          syncStatus: SyncStatus.UNSYNC));
    }

    return result;
  }
}
