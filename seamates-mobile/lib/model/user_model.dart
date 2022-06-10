import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:logging/logging.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/local_user.dart';
import 'package:sea_mates/data/social_user.dart';
import 'package:sea_mates/data/user.dart';
import 'package:sea_mates/data/user_request.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/model/friend_list_model.dart';
import 'package:sea_mates/model/notification_model.dart';
import 'package:sea_mates/model/shift_list_model.dart';
import 'package:sea_mates/repository/user_repository.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/api_utils.dart';

class UserModel extends ChangeNotifier {
  final log = Logger('UserModel');

  // Dependencies
  final UserRepository userRepository;
  late ShiftListModel shiftListModel;
  late FriendListModel friendListModel;
  late NotificationModel _notificationModel;
  final GlobalKey<NavigatorState> navigatorKey =
      new GlobalKey<NavigatorState>();

  UserModel(this.userRepository) {
    load();
  }

  void update(ShiftListModel shiftListModel, FriendListModel friendListModel,
      NotificationModel notificationModel) {
    this.shiftListModel = shiftListModel;
    this.friendListModel = friendListModel;
    _notificationModel = notificationModel;
  }

  // State
  bool _loaded = false;
  User? _user;
  UserStatus _userStatus = UserStatus.ANONYMOUS;

  User? get user => _user;
  UserStatus get userStatus => _userStatus;
  bool get loaded => _loaded;

  Future<void> load() async {
    _loaded = false;
    notifyListeners();
    try {
      _user = await userRepository.loadUser();
      if (_user!.isLocalUser()) {
        _userStatus = UserStatus.LOCAL;
      } else {
        _userStatus = UserStatus.AUTH;
        refreshOnlineData();
      }
    } on UserNotFoundException {
      _user = null;
      _userStatus = UserStatus.ANONYMOUS;
    }
    _loaded = true;
    notifyListeners();
  }

  /// Triggers the loading/refresh of dependencies that depend on user status
  void refreshOnlineData() {
    shiftListModel.syncShifts();
    friendListModel.refresh();
    _notificationModel.subscribe();
  }

  bool hasAuthentication() {
    if (_user == null) {
      return false;
    } else if (_user!.isLocalUser()) {
      return false;
    } else {
      return true;
    }
  }

  String getToken() {
    if (_user == null) {
      throw Exception("Not an auth user!".i18n);
    } else if (_user!.isLocalUser()) {
      throw Exception("User is local".i18n);
    } else {
      return (_user as AuthenticatedUser).token;
    }
  }

  Future socialSignup(SocialUser user) async {
    _loaded = false;
    notifyListeners();

    log.info(user.toString());
    var uri = Uri.https(ApiUtils.API_BASE, '/api/user/socialSignup');
    var headers = {"content-type": "application/json"};
    var response = await http
        .post(uri, headers: headers, body: jsonEncode(user))
        .timeout(Duration(minutes: 2), onTimeout: () {
      throw TimeoutException(
          'Could not connect to server. Please check your internet connection'
              .i18n);
    }).catchError((e) {
      log.warning(e);
      throw e;
    }).whenComplete(() {
      _loaded = true;
      notifyListeners();
    });

    bool answer = false;
    Exception? error;
    switch (response.statusCode) {
      case 201:
        String token = response.headers['authorization']!;
        answer = await socialLogin(token);
        break;
      case 400:
        error = BadRequestException('Invalid data'.i18n);
        break;
      case 409:
        var body = response.body;
        bool username = body.contains('username');
        bool email = body.contains('email');
        String msg = "";
        if (username && email) {
          msg = 'The username and email already exist'.i18n;
        } else if (username) {
          msg = 'The username already exists'.i18n;
        } else {
          msg = 'The email already exists'.i18n;
        }
        error = ConflictException(msg);
        break;
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        error =
            ServerException('Ops, something is wrong with the server!'.i18n);
        break;
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        error = ServerException('Ops, the server responded unexpectedly!'.i18n);
    }

    _loaded = true;
    notifyListeners();

    if (error != null) {
      throw error;
    } else {
      return answer;
    }
  }

  Future<bool> signup(UserRequest request) async {
    _loaded = false;
    notifyListeners();

    var uri = Uri.https(ApiUtils.API_BASE, '/api/user/signup');
    var headers = {"content-type": "application/json"};
    var response = await http
        .post(uri, headers: headers, body: jsonEncode(request))
        .timeout(Duration(minutes: 2), onTimeout: () {
      _loaded = true;
      notifyListeners();
      throw TimeoutException(
          'Could not connect to server. Please check your internet connection'
              .i18n);
    });

    bool answer = false;
    Exception? error;
    switch (response.statusCode) {
      case 201:
        answer = true;
        break;
      case 400:
        error = BadRequestException('Invalid data'.i18n);
        break;
      case 403:
        answer = false;
        handleForbidden();
        break;
      case 409:
        var body = response.body;
        bool username = body.contains('username');
        bool email = body.contains('email');
        String msg = "";

        if (username && email) {
          msg = 'The username and email already exist'.i18n;
        } else if (username) {
          msg = 'The username already exists'.i18n;
        } else {
          msg = 'The email already exists'.i18n;
        }
        error = ConflictException(msg);
        break;
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        error =
            ServerException('Ops, something is wrong with the server!'.i18n);
        break;
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        error = ServerException('Ops, the server responded unexpectedly!'.i18n);
    }

    _loaded = true;
    notifyListeners();

    if (error != null) {
      throw error;
    } else {
      return answer;
    }
  }

  Future<bool> socialLogin(String token) async {
    _loaded = false;
    notifyListeners();

    var hasUserInfo = await _fetchUserInfo(token);
    if (hasUserInfo) {
      refreshOnlineData();
      return true;
    } else {
      return false;
    }
  }

  Future<bool> login(String username, String password) async {
    _loaded = false;
    notifyListeners();

    var uri = Uri.https(ApiUtils.API_BASE, '/login');
    Map<String, String> body = {"username": username, "password": password};
    var response = await http.post(uri, body: jsonEncode(body), headers: {
      "content-type": "application/json"
    }).timeout(Duration(minutes: 2), onTimeout: () {
      _loaded = true;
      notifyListeners();
      throw TimeoutException('Request timed out'.i18n);
    }).catchError((e) {
      _loaded = true;
      notifyListeners();
      throw e;
    });

    String? error;
    bool answer = false;

    if (response.statusCode == 200) {
      var token = response.headers['authorization'];
      var hasUserInfo = await _fetchUserInfo(token!);
      if (hasUserInfo) {
        answer = true;
        refreshOnlineData();
      } else {
        error = "Failed to fetch user info".i18n;
      }
    } else if (response.statusCode == 403 || response.statusCode == 401) {
      handleForbidden();
      answer = false;
    } else {
      error = "Something seem wrong with the server...".i18n;
    }

    _loaded = true;
    notifyListeners();

    if (error != null) {
      return Future.error(error);
    } else {
      return answer;
    }
  }

  Future<bool> loginAsLocal() async {
    User user = new LocalUser();
    await userRepository.saveUser(user);
    _user = user;
    _userStatus = UserStatus.LOCAL;
    notifyListeners();
    return true;
  }

  Future<void> logout() async {
    await shiftListModel.clearLocalDatabase();
    await userRepository.dropUser();
    await friendListModel.clearState();
    _notificationModel.unsubscribe();
    _userStatus = UserStatus.ANONYMOUS;
    _user = null;
    notifyListeners();
  }

  Future<bool> editUser(String name, String email) async {
    assert(email.isNotEmpty);
    _loaded = false;
    notifyListeners();

    var user = _user as AuthenticatedUser;
    var token = user.token;
    Map<String, String> body = {
      "userId": user.id.toString(),
      "name": name,
    };

    // this check is needed since the server throws conflict
    // if we send the unchanged email
    if (user.email != email) {
      body['email'] = email;
    }

    var result = await http
        .put(Uri.https(ApiUtils.API_BASE, 'api/user'),
            headers: {
              'authorization': token,
              'content-type': 'application/json'
            },
            body: jsonEncode(body))
        .timeout(Duration(minutes: 2))
        .catchError((e) {
      log.warning(e);
      _loaded = true;
      notifyListeners();
      throw e;
    });

    bool answer = false;
    Exception? error;
    switch (result.statusCode) {
      case 200:
        var fetched = await _fetchUserInfo(token);
        answer = fetched ? true : false;
        break;
      case 403:
        handleForbidden();
        answer = false;
        break;
      case 409:
        error =
            ConflictException('Email already exists. Choose another one'.i18n);
        break;
      case 500:
        log.severe('${result.headers}\n ${result.body}');
        error =
            ServerException('Oops...something is wrong with the server!'.i18n);
        break;
      default:
        log.warning('${result.statusCode}: ${result.headers}\n ${result.body}');
        error = ServerException('Ops, the server responded unexpectedly!'.i18n);
    }

    _loaded = true;
    notifyListeners();

    if (error != null) {
      throw error;
    } else {
      return answer;
    }
  }

  Future<bool> deleteAccount(String password) async {
    _loaded = false;
    notifyListeners();
    var uri = Uri.https(ApiUtils.API_BASE, 'api/user/delete');
    var headers = {
      "Authorization": getToken(),
      "content-type": "application/json",
      "password": password
    };
    var response = await http
        .delete(uri, headers: headers)
        .timeout(Duration(minutes: 2), onTimeout: () {
      _loaded = true;
      notifyListeners();
      throw TimeoutException(
          'Could not connect to server. Please check your internet connection'
              .i18n);
    }).catchError((e) {
      log.warning(e);
      _loaded = true;
      notifyListeners();
      throw e;
    });

    bool answer = false;
    Exception? error;
    switch (response.statusCode) {
      case 204:
        answer = true;
        break;
      case 403:
        answer = false;
        handleForbidden();
        break;
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        error = ServerException('Ops, the server responded unexpectedly!'.i18n);
    }

    _loaded = true;
    notifyListeners();

    if (error != null) {
      throw error;
    } else {
      return answer;
    }
  }

  /// Performs redirections in case of forbidden errors
  void handleForbidden() {
    navigatorKey.currentState!
        .pushNamedAndRemoveUntil('/welcome', (_) => false);
  }

  Future<bool> _fetchUserInfo(String token) async {
    var result = await http.get(Uri.https(ApiUtils.API_BASE, 'api/user/me'),
        headers: {'authorization': token});

    if (result.statusCode == 200) {
      var json = jsonDecode(result.body);
      AuthenticatedUser user;
      try {
        user = AuthenticatedUser.fromAppUserJson(json);
      } catch (e) {
        _loaded = true;
        notifyListeners();
        return false;
      }
      user.token = token;
      await userRepository.saveUser(user);
      await load();
      return true;
    } else {
      _loaded = true;
      notifyListeners();
      return false;
    }
  }
}

enum UserStatus { ANONYMOUS, LOCAL, AUTH }
