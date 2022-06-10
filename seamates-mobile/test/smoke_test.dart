// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:sea_mates/main.dart';
import 'package:sea_mates/model/friend_list_model.dart';
import 'package:sea_mates/model/notification_model.dart';
import 'package:sea_mates/model/shift_list_model.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/repository/impl/friends_web_client.dart';
import 'package:sea_mates/repository/impl/shift_hive_repository.dart';
import 'package:sea_mates/repository/impl/shift_web_client.dart';
import 'package:sea_mates/repository/impl/user_hive_repo.dart';
import 'package:sea_mates/repository/user_repository.dart';
import 'package:table_calendar/table_calendar.dart';

import 'smoke_test.mocks.dart';

class MockNavigatorObserver extends Mock implements NavigatorObserver {}

Future<void> simulateBackButton(WidgetTester tester) async {
  final dynamic appState = tester.state(find.byType(WidgetsApp));
  expect(await appState.didPopRoute(), true);
  await tester.pump();
}

@GenerateMocks([ShiftWebClient, FriendsWebClient, UserHiveRepository])
void main() {
  testWidgets('Smoke Test - Offline mode', (WidgetTester tester) async {
    // Initializes dependencies and mocks
    final userRepo = MockUserHiveRepository();
    NavigatorObserver mockObserver = MockNavigatorObserver();

    // Ensures no preexisting repo user will change test conditions
    when(userRepo.loadUser()).thenThrow(UserNotFoundException('no user'));

    var userModel = UserModel(userRepo);
    var shiftListModel =
        ShiftListModel(MockShiftWebClient(), ShiftHiveRepository());
    var friendListModel = FriendListModel(MockFriendsWebClient());
    var notificationModel = NotificationModel();
    shiftListModel.update(userModel);
    friendListModel.update(userModel);
    notificationModel.update(userModel);
    userModel.update(shiftListModel, friendListModel, notificationModel);

    final app = SeaMatesApp(
      userModel,
      shiftListModel,
      friendListModel,
      notificationModel,
      navigatorObservers: [mockObserver],
    );
    await tester.pumpWidget(app);

    expect(find.text('SeaMates'), findsOneWidget);
    expect(find.text('Continue offline'), findsOneWidget);
    expect(find.text('Login with email'), findsOneWidget);

    // Login page
    await tester.tap(find.widgetWithText(ElevatedButton, 'Login with email'));
    await tester.pumpAndSettle();
    expect(find.text('SeaMates'), findsNothing);
    expect(find.widgetWithText(ElevatedButton, 'LOGIN'), findsOneWidget);

    // Simulates back button press
    await simulateBackButton(tester);
    expect(find.text('SeaMates'), findsOneWidget);

    // Signup page
    await tester.tap(find.widgetWithText(ElevatedButton, 'Signup'));
    await tester.pumpAndSettle();
    expect(find.text('Confirm Password'), findsOneWidget);
    expect(find.text('SIGNUP'), findsOneWidget);
    await simulateBackButton(tester);

    // Continue offline
    await tester.tap(find.widgetWithText(ElevatedButton, 'Continue offline'));
    await tester.pumpAndSettle();
    expect(find.byType(Dialog), findsOneWidget);
    await tester.tap(find.widgetWithText(TextButton, 'GOT IT!'));
    await tester.pumpAndSettle();
    expect(find.text('Calendar'), findsWidgets);

    // Menu
    await tester.tap(find.byIcon(Icons.more_vert));
    await tester.pump();
    expect(find.text('About'), findsOneWidget);

    // Calendar View
    await tester.tap(find.widgetWithText(Tab, 'Calendar'));
    await tester.pump();
    expect(find.text('Calendar'), findsWidgets);
    expect(find.byIcon(Icons.add), findsOneWidget);

    // Day View
    await tester.tap(find.byType(TableCalendar));
    await tester.pumpAndSettle();
    expect(find.text('Available Friends'), findsOneWidget);
    await tester.tap(find.byType(CloseButton));
    await tester.pumpAndSettle();

    // Shifts View
    await tester.tap(find.widgetWithText(Tab, 'Shifts'));
    await tester.pump();
    expect(find.text('Shifts'), findsOneWidget);
    expect(find.byIcon(Icons.add), findsOneWidget);

    // Profile view
    await tester.tap(find.widgetWithText(Tab, 'Profile'));
    await tester.pumpAndSettle();
    expect(find.text('Profile Info'), findsOneWidget);
    expect(find.text('UPGRADE ACCOUNT'), findsOneWidget);
    expect(find.text('DELETE ACCOUNT'), findsNothing);
  });
}
