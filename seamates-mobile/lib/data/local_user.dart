import 'package:sea_mates/data/user.dart';

class LocalUser implements User {
  @override
  bool isLocalUser() => true;
}
