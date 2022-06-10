import 'package:hive/hive.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/local_user.dart';
import 'package:sea_mates/data/user.dart';
import 'package:sea_mates/repository/user_repository.dart';

class UserHiveRepository implements UserRepository {
  final String _boxName = "userBox";
  final int _key = 17; // just a random key number
  final String _localId = "LOCAL_USER";

  @override
  Future<User> loadUser() async {
    var box = await Hive.openBox(_boxName);
    var result = await box.get(_key);
    if (result == null) {
      throw UserNotFoundException("No user");
    }
    if (result == _localId) {
      return LocalUser();
    } else {
      return result as AuthenticatedUser;
    }
  }

  @override
  Future<void> saveUser(User user) async {
    var box = await Hive.openBox(_boxName);
    if (user.isLocalUser()) {
      await box.put(_key, _localId);
    } else {
      await box.put(_key, user);
    }
  }

  @override
  Future<void> dropUser() async {
    var box = await Hive.openBox(_boxName);
    await box.clear();
  }
}
