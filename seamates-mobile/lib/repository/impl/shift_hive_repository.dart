import 'package:hive/hive.dart';
import 'package:sea_mates/data/shift.dart';
import 'package:sea_mates/repository/shift_local_repository.dart';

class ShiftHiveRepository implements ShiftLocalRepository {
  final String _boxName = "shiftsBox";
  // TODO - convert to widget, close box on exit

  @override
  Future<List<Shift>> addLocal(Iterable<Shift> shifts) async {
    var box = await Hive.openBox(_boxName);
    List<Shift> result = [];
    for (Shift s in shifts) {
      int index = await box.add(s);
      s.id = index;
      result.add(s);
    }
    return result;
  }

  @override
  Future<List<Shift>> loadLocal() async {
    var box = await Hive.openBox(_boxName);
    // Ensures key to id conversion
    List<Shift> shifts = [];
    box.toMap().forEach((key, value) {
      value.id = key;
      shifts.add(value);
    });
    return shifts;
  }

  @override
  Future<int> removeLocal(Iterable<int> shiftIds) async {
    var box = await Hive.openBox(_boxName);
    await box.deleteAll(shiftIds);
    return shiftIds.length;
  }

  @override
  Future<List<Shift>> saveLocal(Iterable<Shift> shifts) async {
    var box = await Hive.openBox(_boxName);
    shifts.forEach((element) => box.put(element.id, element));
    var result = shifts.map((e) => box.get(e.id) as Shift).toList();
    return result;
  }

  @override
  Future<bool> clear() async {
    var box = await Hive.openBox(_boxName);
    var answer = true;
    await box.clear().catchError((e) {
      answer = false;
    });
    return answer;
  }
}
