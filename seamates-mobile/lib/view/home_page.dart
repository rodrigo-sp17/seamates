import 'package:flutter/material.dart';
import 'package:package_info/package_info.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/model/notification_model.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/api_utils.dart';
import 'package:sea_mates/view/calendar_view.dart';
import 'package:sea_mates/view/notification_view.dart';
import 'package:sea_mates/view/profile_view.dart';
import 'package:sea_mates/view/shift_view.dart';
import 'package:url_launcher/url_launcher.dart';

import 'friend_view.dart';

enum Menu { ABOUT }

class HomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final String websiteLink = Uri.https(ApiUtils.API_BASE, "").toString();

  late List<Widget> defaultActions = [];

  PackageInfo _packageInfo = PackageInfo(
      appName: 'Unknown',
      packageName: 'Unknown',
      version: 'Unknown',
      buildNumber: 'Unknown');

  late List<Widget> offlineChildren;
  late List<Widget> offlineTabs;

  @override
  void initState() {
    super.initState();
    _initPackageInfo();

    defaultActions = [
      Consumer<NotificationModel>(builder: (context, model, child) {
        if (model.isSubscribed) {
          return IconButton(
              icon: Icon(model.newNotifications > 0
                  ? Icons.notifications_active
                  : Icons.notifications_none),
              onPressed: () {
                model.clearNewNotifications();
                Navigator.of(context).push(MaterialPageRoute(
                    fullscreenDialog: true,
                    builder: (context) => NotificationView()));
              });
        } else {
          return SizedBox.shrink();
        }
      }),
      PopupMenuButton(
        onSelected: (value) {
          switch (value) {
            case Menu.ABOUT:
              _showAboutDialog();
          }
        },
        itemBuilder: (context) =>
            [PopupMenuItem<Menu>(value: Menu.ABOUT, child: Text("About".i18n))],
      )
    ];

    offlineChildren = [
      CalendarView(defaultActions),
      ShiftView(defaultActions),
      ProfileView(defaultActions)
    ];
    offlineTabs = [
      Tab(
        icon: Icon(Icons.today),
        text: 'Calendar'.i18n,
      ),
      Tab(
        icon: Icon(Icons.work),
        text: 'Shifts'.i18n,
      ),
      Tab(
        icon: Icon(Icons.person),
        text: 'Profile'.i18n,
      ),
    ];
  }

  Future<void> _initPackageInfo() async {
    final PackageInfo info = await PackageInfo.fromPlatform();
    setState(() {
      _packageInfo = info;
    });
  }

  void _showAboutDialog() {
    final TextStyle textStyle = Theme.of(context).textTheme.bodyText2!;

    showAboutDialog(
      context: context,
      applicationIcon: Image(
        height: 70,
        image: AssetImage('assets/icon.png'),
      ),
      applicationName: 'SeaMates',
      applicationVersion: _packageInfo.version,
      applicationLegalese: '\u{a9} 2021 Rodrigo Silva dev.rodrigosp@gmail.com',
      children: [
        const SizedBox(
          height: 20,
        ),
        RichText(
          text: TextSpan(children: [
            TextSpan(
                style: textStyle,
                text: 'SeaMates is an app for monitoring your work shifts,'
                        ' sharing with friends and easily checking who is available.\n\n'
                    .i18n),
            TextSpan(style: textStyle, text: 'Online website at '.i18n),
          ]),
        ),
        InkWell(
          child: Text(
            websiteLink,
            style: textStyle.copyWith(color: Colors.blue),
          ),
          onTap: () => launch(websiteLink),
        )
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<UserModel>(
      builder: (context, model, child) {
        List<Widget> children = [];
        List<Widget> tabs = [];

        if (model.hasAuthentication()) {
          children = [
            CalendarView(defaultActions),
            ShiftView(defaultActions),
            FriendView(defaultActions),
            ProfileView(defaultActions)
          ];
          tabs = [
            Tab(
              icon: Icon(Icons.today),
              text: 'Calendar'.i18n,
            ),
            Tab(
              icon: Icon(Icons.work),
              text: 'Shifts'.i18n,
            ),
            Tab(
              icon: Icon(Icons.people),
              text: 'Friends'.i18n,
            ),
            Tab(
              icon: Icon(Icons.person),
              text: 'Profile'.i18n,
            )
          ];
        } else {
          children = offlineChildren;
          tabs = offlineTabs;
        }

        return DefaultTabController(
          length: children.length,
          child: Scaffold(
            bottomNavigationBar: TabBar(
                labelColor: Theme.of(context).primaryColorLight,
                indicatorColor: Theme.of(context).primaryColorLight,
                tabs: tabs),
            body: TabBarView(
              children: children,
            ),
          ),
        );
      },
    );
  }
}
