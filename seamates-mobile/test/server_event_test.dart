import 'package:flutter_test/flutter_test.dart';
import 'package:sea_mates/data/server_event.dart';

void main() {
  test("Parsing events is correct", () {
    var event1 = "data: {\"key1\":\"value1\",\"key2\":\"value2\"}\n";
    var event2 = "event: PING\ndata:{}";
    var event3 = "event:\n";
    var event4 = ":\ndata:{\"key1\":\"\",\"key2\":\"some\"}";

    var parsed1 = ServerEvent.parse(event1);
    expect(parsed1.type, equals(""));
    expect(parsed1.body["key1"], equals("value1"));
    expect(parsed1.body["key2"], equals("value2"));
    var parsed2 = ServerEvent.parse(event2);
    expect(parsed2.type, equals("PING"));
    expect(parsed2.body, equals({}));
    var parsed3 = ServerEvent.parse(event3);
    expect(parsed3.type, equals(""));
    expect(parsed3.body, equals({}));
    var parsed4 = ServerEvent.parse(event4);
    expect(parsed4.type, equals(""));
    expect(parsed4.body["key1"], equals(""));
    expect(parsed4.body["key2"], equals("some"));
  });
}
