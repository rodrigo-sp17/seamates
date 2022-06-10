import 'package:sea_mates/data/user.dart';

abstract class UserRepository {
  Future<User> loadUser();
  Future<void> saveUser(User user);
  Future<void> dropUser();
}

class UserNotFoundException implements Exception {
  String message;
  UserNotFoundException(this.message);
}
