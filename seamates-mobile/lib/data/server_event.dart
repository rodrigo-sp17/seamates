import 'dart:convert';

class ServerEvent {
  String type = "";
  Map<String, dynamic> body = {};

  ServerEvent();

  factory ServerEvent.parse(String textEvent) {
    var response = ServerEvent();
    var lines = textEvent.split("\n");
    for (String line in lines) {
      if (line.isNotEmpty) {
        var pair = line.split(":");
        while (pair.length < 2) {
          pair.add(""); // ensures no exception from out of bounds
        }
        var key = pair[0];
        if (key.contains('event')) {
          response.type = pair[1].trimLeft();
        } else if (key.contains('data')) {
          var jsonString = line.replaceFirst("data:", "").trimLeft();
          if (jsonString.isEmpty) {
            response.body = {};
          } else {
            response.body = jsonDecode(jsonString) as Map<String, dynamic>;
          }
        }
      }
    }
    return response;
  }
}
