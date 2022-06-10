import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:hive/hive.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:logging/logging.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/data/shift.dart';
import 'package:sea_mates/data/sync_status.dart';
import 'package:sea_mates/model/friend_list_model.dart';
import 'package:sea_mates/model/notification_model.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/repository/impl/friends_web_client.dart';
import 'package:sea_mates/repository/impl/shift_hive_repository.dart';
import 'package:sea_mates/repository/impl/shift_web_client.dart';
import 'package:sea_mates/repository/impl/user_hive_repo.dart';
import 'package:sea_mates/view/home_page.dart';
import 'package:sea_mates/view/login_page.dart';
import 'package:sea_mates/view/oauth_view.dart';
import 'package:sea_mates/view/signup_page.dart';
import 'package:sea_mates/view/social_signup_page.dart';
import 'package:sea_mates/view/splash_page.dart';
import 'package:sea_mates/view/welcome_page.dart';

import 'model/shift_list_model.dart';

void main() async {
  await Hive.initFlutter("sea_mates");
  Hive.registerAdapter(ShiftAdapter());
  Hive.registerAdapter(SyncStatusAdapter());
  Hive.registerAdapter(AuthenticatedUserAdapter());

  Logger.root.level = Level.INFO;
  Logger.root.onRecord.listen((record) {
    print('${record.level.name}: ${record.time}: ${record.message}');
  });

  var userModel = UserModel(UserHiveRepository());
  var shiftListModel = ShiftListModel(ShiftWebClient(), ShiftHiveRepository());
  shiftListModel.update(userModel);
  var friendListModel = FriendListModel(FriendsWebClient());
  var notificationModel = NotificationModel();
  notificationModel.update(userModel);
  friendListModel.update(userModel);
  userModel.update(shiftListModel, friendListModel, notificationModel);

  runApp(SeaMatesApp(
      userModel, shiftListModel, friendListModel, notificationModel));
}

class SeaMatesApp extends StatelessWidget {
  SeaMatesApp(this.userModel, this.shiftListModel, this.friendListModel,
      this.notificationModel,
      {this.navigatorObservers = const []});
  final UserModel userModel;
  final ShiftListModel shiftListModel;
  final FriendListModel friendListModel;
  final NotificationModel notificationModel;
  final List<NavigatorObserver> navigatorObservers;

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
        providers: [
          ChangeNotifierProvider.value(value: userModel),
          ChangeNotifierProvider.value(value: shiftListModel),
          ChangeNotifierProvider.value(value: friendListModel),
          ChangeNotifierProvider.value(value: notificationModel),
        ],
        child: MaterialApp(
            navigatorObservers: navigatorObservers,
            localizationsDelegates: [
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            supportedLocales: [
              const Locale('en', 'US'),
              const Locale('pt', 'BR'),
              const Locale('es', ''),
            ],
            navigatorKey: userModel.navigatorKey,
            title: 'SeaMates',
            theme: ThemeData(
                primaryColor: Color(0xff064273),
                primaryColorLight: Color(0xff456da2),
                primaryColorDark: Color(0xff001c47),
                accentColor: Color(0xff76b6c4),
                secondaryHeaderColor: Color(0xff458694),
                primaryTextTheme:
                    TextTheme(bodyText2: TextStyle(color: Color(0xffffffff))),
                accentTextTheme:
                    TextTheme(bodyText2: TextStyle(color: Color(0xff000000))),
                elevatedButtonTheme: ElevatedButtonThemeData(
                    style: ButtonStyle(
                        backgroundColor:
                            MaterialStateProperty.all(Color(0xff76b6c4)),
                        minimumSize:
                            MaterialStateProperty.all(Size.fromHeight(45)),
                        textStyle: MaterialStateProperty.all(TextStyle(
                            fontSize: 16, fontWeight: FontWeight.bold))))),
            home: I18n(child: SplashPage()),
            routes: {
              '/welcome': (context) => WelcomePage(),
              '/home': (context) => HomePage(),
              '/login': (context) => LoginPage(),
              '/signup': (context) => SignupPage(),
              '/socialSignup': (context) => SocialSignupPage(),
              '/oauth2': (context) => OAuthView(),
            }));
  }
}
