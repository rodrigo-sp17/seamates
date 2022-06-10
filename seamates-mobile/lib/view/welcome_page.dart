import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/api_utils.dart';
import 'package:url_launcher/url_launcher.dart';

class WelcomePage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return WillPopScope(
        onWillPop: () async => false,
        child: Scaffold(
          body: Container(
              decoration: BoxDecoration(
                  gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [
                    Theme.of(context).primaryColor,
                    Theme.of(context).accentColor
                  ])),
              child: Scrollbar(
                  child: ListView(
                padding: EdgeInsets.symmetric(horizontal: 10, vertical: 20),
                children: [
                  Padding(
                    padding: EdgeInsets.only(top: 80),
                    child: Center(
                        child: Image(
                      height: 100,
                      image: AssetImage('assets/icon.png'),
                    )),
                  ),
                  Padding(
                    padding: EdgeInsets.only(top: 10, bottom: 50),
                    child: Center(
                      child: Text(
                        'SeaMates',
                        style: TextStyle(
                            color: Colors.white,
                            fontSize: Theme.of(context)
                                .accentTextTheme
                                .headline3!
                                .fontSize),
                      ),
                    ),
                  ),
                  ElevatedButton.icon(
                      style: ButtonStyle(
                          backgroundColor:
                              MaterialStateProperty.all(Color(0xFF1877F2))),
                      icon: Icon(FontAwesomeIcons.facebookF),
                      label: Text('Continue with Facebook'.i18n),
                      onPressed: () => Navigator.pushNamed(context, '/oauth2')),
                  SizedBox(
                    height: 15,
                  ),
                  ElevatedButton(
                      onPressed: () {
                        Navigator.pushNamed(context, '/login');
                      },
                      child: Text("Login with email".i18n)),
                  Divider(
                    height: 20,
                    thickness: 0,
                  ),
                  ElevatedButton(
                      style: ButtonStyle(
                          backgroundColor: MaterialStateProperty.all(
                              Theme.of(context).primaryColorLight)),
                      onPressed: () {
                        Navigator.pushNamed(context, '/signup');
                      },
                      child: Text("Signup".i18n)),
                  Divider(
                    height: 20,
                    thickness: 0,
                  ),
                  Consumer<UserModel>(builder: (context, model, child) {
                    switch (model.userStatus) {
                      case UserStatus.ANONYMOUS:
                        return ElevatedButton(
                            style: ButtonStyle(
                                backgroundColor:
                                    MaterialStateProperty.all(Colors.grey)),
                            onPressed: () {
                              _showLocalUserDialog(context);
                            },
                            child: Text("Continue offline".i18n));
                      case UserStatus.LOCAL:
                        return ElevatedButton(
                            style: ButtonStyle(
                                backgroundColor:
                                    MaterialStateProperty.all(Colors.grey)),
                            onPressed: () =>
                                Navigator.pushNamed(context, '/home'),
                            child: Text("Remain in local mode".i18n));
                      case UserStatus.AUTH:
                        return ElevatedButton(
                          style: ButtonStyle(
                              backgroundColor:
                                  MaterialStateProperty.all(Colors.grey)),
                          onPressed: () => _showLogoutDialog(context),
                          child: Text("Logout"),
                        );
                      default:
                        throw AssertionError("Unrecognized user status");
                    }
                  }),
                  SizedBox(
                    height: 15,
                  ),
                  TextButton(
                    onPressed: () => launch(
                        Uri.https(ApiUtils.API_BASE, '/recovery').toString()),
                    child: Text(
                      'Forgot your password?'.i18n,
                      textScaleFactor: 1.1,
                      style: TextStyle(
                          color: Colors.white,
                          decoration: TextDecoration.underline),
                    ),
                  )
                ],
              ))),
        ));
  }
}

void _showLocalUserDialog(BuildContext context) {
  showDialog(
      context: context,
      builder: (_) => AlertDialog(
            title: Text("Entering in Local mode".i18n),
            content: SingleChildScrollView(
              child: Text('You are now going in local mode.\n\n'
                      'In this mode, you will not be able to:\n'
                      '- Add friends\n'
                      '- View friends shifts\n'
                      '- Invite friends to events\n'
                      '- Sync with the cloud\n\n'
                      'However, don\'t worry!\n'
                      'You can add an account later and sync your shifts! :)'
                  .i18n),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: Text('CANCEL'.i18n),
              ),
              TextButton(
                  onPressed: () async {
                    var result =
                        await Provider.of<UserModel>(context, listen: false)
                            .loginAsLocal();
                    if (result)
                      Navigator.pushNamedAndRemoveUntil(
                          context, '/home', (_) => false);
                  },
                  child: Text('GOT IT!'.i18n))
            ],
          ));
}

void _showLogoutDialog(BuildContext context) {
  showDialog(
      context: context,
      builder: (_) => AlertDialog(
            title: Text("Logout"),
            content: SingleChildScrollView(
              child: Text('Are you sure you want to logout?\n'
                      'All your un-synced modifications will be discarded.'
                  .i18n),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: Text('CANCEL'),
              ),
              TextButton(
                  onPressed: () async {
                    await Provider.of<UserModel>(context, listen: false)
                        .logout();
                  },
                  child: Text('YES, LOG ME OUT!'.i18n))
            ],
          ));
}
