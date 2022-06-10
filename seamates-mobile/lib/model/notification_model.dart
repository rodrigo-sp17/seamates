import 'dart:async';
import 'dart:collection';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:logging/logging.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/server_event.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/api_utils.dart';

class NotificationModel extends ChangeNotifier {
  final log = Logger('NotificationModel');

  // Dependencies
  late UserModel _userModel;
  http.Client? _client;

  void update(UserModel userModel) {
    _userModel = userModel;
  }

  // State
  bool _isSubscribed = false;
  List<String> _notifications = [];
  int _newNotifications = 0;
  List<int> _valueBuffer = <int>[];

  bool get isSubscribed => _isSubscribed;
  UnmodifiableListView<String> get notifications =>
      UnmodifiableListView(_notifications);
  int get newNotifications => _newNotifications;

  // Actions
  Future<void> subscribe() async {
    if (!_userModel.hasAuthentication()) {
      return;
    }

    var user = _userModel.user as AuthenticatedUser;
    var username = user.username;
    var token = _userModel.getToken();

    var uri = Uri.https(ApiUtils.API_BASE, '/api/push/subscribe/$username');
    try {
      var client = http.Client();
      _client = client;

      var request = new http.Request("GET", uri);
      request.headers["Accept"] = "text/event-stream";
      request.headers["Cache-Control"] = "no-cache";
      request.headers["Authorization"] = token;

      var response = await client.send(request);
      if (response.statusCode == 200) {
        _isSubscribed = true;
        response.stream.forEach((value) {
          _valueBuffer.addAll(value);
          int last = value.length;
          if (value[last - 1] == 10 && value[last - 2] == 10) {
            var event = ServerEvent.parse(utf8.decode(_valueBuffer));
            _handleEvents(event);
            _valueBuffer = [];
          }
        }).onError((error, stackTrace) {
          log.info("Connection closed");
          unsubscribe();
        }).whenComplete(() {
          unsubscribe();
          subscribe();
        });
      } else {
        _isSubscribed = false;
      }
      notifyListeners();
    } catch (e) {
      unsubscribe();
      log.warning("Could not subscribe to notifications");
      log.warning(e);
    }
  }

  void unsubscribe() {
    _client!.close();
    _isSubscribed = false;
    notifyListeners();
  }

  void clearNewNotifications() {
    _newNotifications = 0;
    notifyListeners();
  }

  void clearNotifications() {
    _notifications = <String>[];
    notifyListeners();
  }

  void _addNotifications(String notification) {
    _notifications.insert(0, notification);
    ++_newNotifications;
    notifyListeners();
  }

  void _handleEvents(ServerEvent event) {
    var username = (_userModel.user as AuthenticatedUser).username;
    switch (event.type) {
      case "FRIEND_REQUEST":
        var source = event.body["source"];
        if (source != username) {
          var msg =
              '%s requested your friendship!'.i18n.fill([event.body["source"]]);
          _addNotifications(msg);
        }
        break;
      case "FRIEND_ACCEPT":
        var source = event.body["source"];
        if (source != username) {
          var msg = '%s accepted your friendship request!'
              .i18n
              .fill([event.body["source"]]);
          _addNotifications(msg);
        }
        break;
      default:
    }
  }
}
