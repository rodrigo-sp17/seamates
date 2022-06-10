import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:logging/logging.dart';
import 'package:sea_mates/data/friend.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';

import '../../util/api_utils.dart';

class FriendsWebClient {
  static const String _base = ApiUtils.API_BASE;
  static const _path = '/api/friend';
  static const _timeout = Duration(minutes: 2);

  final log = Logger('FriendsWebClient');

  Future<List<Friend>> getFriends(String token) async {
    var response = await http
        .get(Uri.https(_base, _path), headers: {"authorization": token})
        .timeout(_timeout)
        .catchError((e) {
          log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return _parseFriendsJson(decodedJson);
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 403:
        throw ForbiddenException('');
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  Future<List<FriendRequest>> getRequests(String token) async {
    var response = await http
        .get(Uri.https(_base, _path + '/request'),
            headers: {"authorization": token})
        .timeout(_timeout)
        .catchError((e) {
          log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return _parseRequestsJson(decodedJson);
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 403:
        throw ForbiddenException('');
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  Future<List<Friend>> getAvailableFriends(
      String token, DateTime dateTime) async {
    var response = await http
        .get(
            Uri.https(_base, '/api/calendar/available',
                {"date": dateTime.toIso8601String().substring(0, 10)}),
            headers: {"authorization": token})
        .timeout(_timeout)
        .catchError((e) {
          log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return _parseFriendsJson(decodedJson);
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 403:
        throw ForbiddenException('');
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  Future<FriendRequest> requestFriendship(String token, String username) async {
    var response = await http.post(
        Uri.https(_base, _path + '/request', {"username": username}),
        headers: {"authorization": token}).catchError((e) {
      log.warning(e);
      throw e;
    }).timeout(_timeout);

    switch (response.statusCode) {
      case 201:
        dynamic decodedJson = jsonDecode(response.body);
        return FriendRequest.fromJson(decodedJson);
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 400:
        throw BadRequestException(response.body.toString());
      case 403:
        throw ForbiddenException('');
      case 404:
        throw NotFoundException(response.body.toString());
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning('${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  Future<Friend> acceptRequest(String token, String username) async {
    var response = await http
        .post(Uri.https(_base, _path + '/accept', {"username": username}),
            headers: {"authorization": token})
        .timeout(_timeout)
        .catchError((e) {
          log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return Friend.fromJson(decodedJson);
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 403:
        throw ForbiddenException('');
      case 404:
        throw NotFoundException(response.body.toString());
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning('${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  Future<bool> removeFriend(String token, String username) async {
    var response = await http
        .delete(Uri.https(_base, _path + '/remove', {"username": username}),
            headers: {"authorization": token})
        .timeout(_timeout)
        .catchError((e) {
          log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 204:
        return true;
      case 302:
        throw RedirectionException(response.headers.values.toString());
      case 403:
        throw ForbiddenException('');
      case 404:
        throw NotFoundException(response.body.toString());
      case 500:
        log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        log.warning('${response.headers}\n ${response.body}');
        throw ServerException('Unexpected response');
    }
  }

  List<Friend> _parseFriendsJson(dynamic decodedJson) {
    List<Friend> result = [];
    Iterable<dynamic>? resultList = decodedJson['_embedded']?['appUserList'];
    if (resultList == null || resultList.isEmpty) {
      return result;
    } else {
      resultList.forEach((element) {
        var friend = Friend.fromJson(element);
        result.add(friend);
      });

      return result;
    }
  }

  List<FriendRequest> _parseRequestsJson(dynamic decodedJson) {
    List<FriendRequest> result = [];
    Iterable<dynamic>? resultList =
        decodedJson['_embedded']?['friendRequestDTOList'];
    if (resultList == null || resultList.isEmpty) {
      return result;
    } else {
      resultList.forEach((element) {
        var friend = FriendRequest.fromJson(element);
        result.add(friend);
      });
      return result;
    }
  }
}
