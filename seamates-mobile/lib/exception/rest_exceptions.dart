abstract class RestException implements Exception {
  late String message;
}

class BadRequestException extends RestException {
  String message;
  BadRequestException(this.message);
}

class ConflictException extends RestException {
  String message;

  ConflictException(this.message);
}

class RedirectionException extends RestException {
  String message;

  RedirectionException(this.message);
}

class NotFoundException extends RestException {
  String message;

  NotFoundException(this.message);
}

class ServerException extends RestException {
  String message;

  ServerException(this.message);
}

class ForbiddenException extends RestException {
  String message;

  ForbiddenException(this.message);
}

class UnexpectedResponseException extends RestException {
  String message;

  UnexpectedResponseException(this.message);
}
