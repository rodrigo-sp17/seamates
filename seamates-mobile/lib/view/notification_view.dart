import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/model/notification_model.dart';
import 'package:sea_mates/strings.i18n.dart';

class NotificationView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Notifications'.i18n),
      ),
      body: Scrollbar(
          child: Consumer<NotificationModel>(builder: (context, model, child) {
        if (model.notifications.isEmpty) {
          return Center(
            child: Text("No notifications to show".i18n),
          );
        } else {
          var notifs = model.notifications;
          return ListView.builder(
            itemBuilder: (context, index) {
              return ListTile(
                title: Text(notifs.elementAt(index)),
              );
            },
            itemCount: notifs.length,
          );
        }
      })),
    );
  }
}
