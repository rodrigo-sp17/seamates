import 'package:sea_mates/data/shift.dart';

abstract class ShiftLocalRepository {
  Future<List<Shift>> loadLocal();
  Future<List<Shift>> addLocal(Iterable<Shift> shifts);
  Future<int> removeLocal(Iterable<int> shiftIds);
  Future<List<Shift>> saveLocal(Iterable<Shift> shifts);
  Future<bool> clear();
}
