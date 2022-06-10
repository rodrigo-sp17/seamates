import 'package:email_validator/email_validator.dart';

class Validators {
  static final _usernameRegexp =
      RegExp(r"^[a-zA-Z0-9]+([_@#&-]?[a-zA-Z0-9 ])*$");
  static final _nameRegexp = RegExp(r"^([^0-9{}\\/()\]\[]*)$");
  static final _passwordRegexp = RegExp(
      r"^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[*.!@$%#^&(){}\[\]:;<>,.?~+-/|/=/\\]).*$");

  static String? validateUsername(String? value) {
    if (value == null || value.isEmpty) {
      return "Username is mandatory";
    }
    int size = value.length;
    if (size < 6 || size > 30) {
      return "Username must be between 6 and 30 characters";
    }
    if (!_usernameRegexp.hasMatch(value)) {
      return "Invalid username";
    }
    return null;
  }

  static String? validateName(String? value) {
    if (value == null || value.isEmpty) {
      return "Name is mandatory";
    }
    int size = value.length;
    if (size > 150) {
      return "Names should be at most 150 characters";
    }
    if (!_nameRegexp.hasMatch(value)) {
      return "Invalid name";
    }
    return null;
  }

  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return "Email is mandatory";
    }
    if (!EmailValidator.validate(value)) {
      return "Invalid email";
    }
    return null;
  }

  static String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return "Password is mandatory";
    }
    int size = value.length;
    if (size < 8 || size > 128) {
      return "Passwords must have at least 8 and at most 128 characters";
    }
    if (!_passwordRegexp.hasMatch(value)) {
      return "Passwords must have upper-case and lower-case letters, numbers and special characters";
    }
    return null;
  }
}
