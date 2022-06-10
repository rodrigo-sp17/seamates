import 'dart:convert';

import 'package:http/http.dart' as http;
import 'package:logging/logging.dart';
import 'package:sea_mates/data/shift.dart';
import 'package:sea_mates/data/sync_status.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/util/api_utils.dart';

import '../shift_remote_repository.dart';

class ShiftWebClient implements ShiftRemoteRepository {
  static const String _base = ApiUtils.API_BASE;
  static const String _path = '/api/shift';
  static const _timeout = Duration(minutes: 2);

  final _log = Logger("ShiftWebClient");

  @override
  Future<List<Shift>> addRemote(Shift shift, String token) async {
    var response = await http
        .post(Uri.https(_base, _path + '/add'),
            body: json.encode(shift.toJson()),
            headers: {
              'content-type': 'application/json',
              'Authorization': token
            })
        .timeout(_timeout)
        .catchError((e) {
          _log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return _parseShiftListJson(decodedJson);
      case 400:
        Map<String, String> errors = jsonDecode(response.body);
        throw BadRequestException(errors.toString());
      case 403:
        throw ForbiddenException('');
      case 500:
        _log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        _log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw UnexpectedResponseException(response.statusCode.toString());
    }
  }

  @override
  Future<List<Shift>> loadRemote(String token) async {
    var response = await http
        .get(Uri.https(_base, _path), headers: {'Authorization': token})
        .timeout(_timeout)
        .catchError((e) {
          _log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 200:
        dynamic decodedJson = jsonDecode(response.body);
        return _parseShiftListJson(decodedJson);
      case 403:
        throw ForbiddenException('');
      case 500:
        _log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        _log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw UnexpectedResponseException(response.statusCode.toString());
    }
  }

  @override
  Future<bool> removeRemote(int shiftId, String token) async {
    var response = await http
        .delete(Uri.https(_base, _path + '/remove', {'id': shiftId.toString()}),
            headers: {'Authorization': token})
        .timeout(_timeout)
        .catchError((e) {
          _log.warning(e);
          throw e;
        });

    switch (response.statusCode) {
      case 204:
        return true;
      case 400:
        throw BadRequestException('');
      case 404:
        throw NotFoundException('');
      case 403:
        throw ForbiddenException('');
      case 500:
        _log.severe('${response.headers}\n ${response.body}');
        throw ServerException('500');
      default:
        _log.warning(
            '${response.statusCode}: ${response.headers}\n ${response.body}');
        throw UnexpectedResponseException(response.statusCode.toString());
    }
  }

  @override
  Future<List<Shift>> saveRemote(Iterable<Shift> shifts, String token) async {
    List<Shift> result = [];
    var client = http.Client();
    try {
      for (Shift s in shifts) {
        var response = await client
            .put(Uri.https(_base, _path + '/edit'),
                body: json.encode(s.toJson()),
                headers: {
                  'content-type': 'application/json',
                  'Authorization': token
                })
            .timeout(_timeout)
            .catchError((e) {
              _log.warning(e);
              throw e;
            });

        switch (response.statusCode) {
          case 200:
            dynamic decodedJson = jsonDecode(response.body);
            var shift = Shift.fromJson(decodedJson);
            shift.syncStatus = SyncStatus.SYNC;
            result.add(shift);
            break;
          case 400:
            Map<String, String> errors = jsonDecode(response.body);
            throw BadRequestException(errors.toString());
          case 401:
            throw ForbiddenException('Unauthorized');
          case 403:
            throw ForbiddenException('Unauthorized');
          case 404:
            throw NotFoundException('');
          case 500:
            _log.severe('${response.headers}\n ${response.body}');
            throw ServerException('500');
          default:
            _log.warning(
                '${response.statusCode}: ${response.headers}\n ${response.body}');
            throw UnexpectedResponseException(response.statusCode.toString());
        }
      }
    } finally {
      client.close();
    }
    return result;
  }

  List<Shift> _parseShiftListJson(dynamic decodedJson) {
    List<Shift> result = [];
    Iterable<dynamic>? resultList = decodedJson['_embedded']?['shiftList'];
    if (resultList == null || resultList.isEmpty) {
      return result;
    } else {
      resultList.forEach((element) {
        var shift = Shift.fromJson(element);
        shift.syncStatus = SyncStatus.SYNC;
        result.add(shift);
      });

      return result;
    }
  }
}
