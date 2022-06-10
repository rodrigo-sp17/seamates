import 'dart:async';
import 'dart:collection';

import 'package:flutter/widgets.dart';
import 'package:logging/logging.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/friend.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/repository/impl/friends_web_client.dart';
import 'package:sea_mates/strings.i18n.dart';

class FriendListModel extends ChangeNotifier {
  final log = Logger('FriendListModel');

  // Dependencies
  final FriendsWebClient _friendsWebClient;
  late UserModel _userModel;

  FriendListModel(this._friendsWebClient);

  void update(UserModel userModel) {
    _userModel = userModel;
  }

  // State
  bool _isLoading = false;
  List<Friend> _friends = [];
  List<FriendRequest> _myRequests = [];
  List<FriendRequest> _otherRequests = [];

  bool get isLoading => _isLoading;
  UnmodifiableListView<Friend> get friends => UnmodifiableListView(_friends);
  UnmodifiableListView<FriendRequest> get myRequests =>
      UnmodifiableListView(_myRequests);
  UnmodifiableListView<FriendRequest> get otherRequests =>
      UnmodifiableListView(_otherRequests);

  // Actions
  Future<String?> refresh() async {
    _isLoading = true;
    notifyListeners();

    var token = _userModel.getToken();
    try {
      _friends = await _friendsWebClient.getFriends(token);
      var requests = await _friendsWebClient.getRequests(token);
      _myRequests = [];
      _otherRequests = [];

      var authUsername = (_userModel.user as AuthenticatedUser).username;
      requests.forEach((req) {
        if (req.sourceUsername == authUsername) {
          _myRequests.add(req);
        } else {
          _otherRequests.add(req);
        }
      });
      return null;
    } on TimeoutException {
      return 'Could not reach server. Are you online?'.i18n;
    } on ForbiddenException {
      _userModel.handleForbidden();
      return 'You are not unauthorized'.i18n;
    } on ServerException {
      return "Sync failed!";
    } on Exception {
      return "Something went wrong...".i18n;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// Fetches available friends on the provided date
  /// Returns null if not available or an Future error if some error occurred
  Future<List<Friend>?> fetchAvailableFriends(DateTime date) async {
    if (!_userModel.hasAuthentication()) {
      return null;
    }

    try {
      var friends = await _friendsWebClient.getAvailableFriends(
          _userModel.getToken(), date);
      return friends;
    } on TimeoutException {
      return Future.error('Could not reach server. Are you online?'.i18n);
    } on ForbiddenException {
      _userModel.handleForbidden();
      return Future.error('You are not unauthorized'.i18n);
    } on ServerException {
      return Future.error("Sync failed!".i18n);
    } on Exception {
      return Future.error("Something went wrong...".i18n);
    }
  }

  /// Requests friendship for the given username
  /// Returns null if ok, or an error message if failed
  Future<String?> requestFriendship(String username) async {
    _isLoading = true;
    notifyListeners();

    // assume the token exists, otherwise the view should not be shown
    try {
      var request = await _friendsWebClient.requestFriendship(
          _userModel.getToken(), username);
      _myRequests.add(request);
      return null;
    } on TimeoutException {
      return 'Could not reach server. Are you online?'.i18n;
    } on ForbiddenException {
      _userModel.handleForbidden();
      return 'Request unauthorized'.i18n;
    } on NotFoundException {
      return 'The user does not exist!'.i18n;
    } on BadRequestException {
      return 'Trying to be friends with yourself? Nice!'.i18n;
    } on ServerException {
      return "Request failed!".i18n;
    } on Exception {
      return "Something went wrong...".i18n;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// Accepts friendship for the given username
  /// Returns null if ok, or an error message if failed
  Future<String?> acceptFriendship(String username) async {
    _isLoading = true;
    notifyListeners();

    // assume the token exists, otherwise the view should not be shown
    try {
      var friend = await _friendsWebClient.acceptRequest(
          _userModel.getToken(), username);
      _friends.add(friend);
      _otherRequests.removeWhere((req) => req.sourceUsername == username);
      return null;
    } on TimeoutException {
      return 'Could not reach server. Are you online?'.i18n;
    } on ForbiddenException {
      _userModel.handleForbidden();
      return 'Request unauthorized'.i18n;
    } on NotFoundException {
      return 'The request does not exist!'.i18n;
    } on ServerException {
      return "Could not accept!".i18n;
    } on Exception {
      return "Something went wrong...".i18n;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// Removes friendship for the given username
  /// Returns null if ok, or an error message if failed
  Future<String?> removeFriend(String username) async {
    _isLoading = true;
    notifyListeners();

    // assume the token exists, otherwise the view should not be shown
    try {
      var success =
          await _friendsWebClient.removeFriend(_userModel.getToken(), username);
      if (success) {
        _friends.removeWhere((friend) => friend.user.username == username);
      } else {
        return "Unfriending failed!".i18n;
      }
      return null;
    } on TimeoutException {
      return 'Could not reach server. Are you online?'.i18n;
    } on ForbiddenException {
      _userModel.handleForbidden();
      return 'Request unauthorized'.i18n;
    } on NotFoundException {
      return 'The friend does not exist!'.i18n;
    } on ServerException {
      return "Could not remove friend!".i18n;
    } on Exception {
      return "Something went wrong...".i18n;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> clearState() async {
    _friends = [];
    _myRequests = [];
    _otherRequests = [];
  }
}
