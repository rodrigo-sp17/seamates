import 'package:flutter/material.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:intl/intl.dart';
import 'package:sea_mates/data/friend.dart';
import 'package:sea_mates/strings.i18n.dart';

class FriendProfileView extends StatelessWidget {
  FriendProfileView(this.friend);
  final Friend friend;
  final DateFormat dateFormat = DateFormat.yMd(I18n.localeStr);

  @override
  Widget build(BuildContext context) {
    const sizedBox = SizedBox(
      height: 15,
    );
    return Scaffold(
      appBar: AppBar(
        title: Text("%s Profile".i18n.fill([friend.user.name])),
      ),
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Column(
              children: [
                SizedBox(
                  height: 10,
                ),
                Text(
                  "Personal Data".i18n,
                  style: Theme.of(context).textTheme.headline6,
                ),
                Divider(
                  height: 10,
                  thickness: 2,
                )
              ],
            ),
          ),
          SliverPadding(
            padding: EdgeInsets.all(20),
            sliver: SliverList(
              delegate: SliverChildListDelegate([
                TextFormField(
                  initialValue: friend.user.username,
                  enabled: true,
                  readOnly: true,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    icon: Icon(Icons.person_outline),
                    labelText: "Username".i18n,
                  ),
                ),
                sizedBox,
                TextFormField(
                  initialValue: friend.user.name,
                  enabled: true,
                  readOnly: true,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    icon: Icon(Icons.person),
                    labelText: "Name".i18n,
                  ),
                ),
                sizedBox,
                TextFormField(
                  initialValue: friend.user.email,
                  enabled: true,
                  readOnly: true,
                  decoration: InputDecoration(
                    border: OutlineInputBorder(),
                    icon: Icon(Icons.email),
                    labelText: "Email".i18n,
                  ),
                ),
              ]),
            ),
          ),
          SliverToBoxAdapter(
            child: Column(
              children: [
                SizedBox(
                  height: 10,
                ),
                Text(
                  "Shifts".i18n,
                  style: Theme.of(context).textTheme.headline6,
                ),
                Divider(
                  height: 10,
                  thickness: 2,
                )
              ],
            ),
          ),
          SliverPadding(
              padding: EdgeInsets.all(0),
              sliver: SliverList(
                delegate: SliverChildBuilderDelegate((context, int index) {
                  var shifts = friend.shifts;
                  var shift = shifts[index];
                  return ListTile(
                    title: Text(
                      dateFormat.format(shift.unavailabilityStartDate) +
                          " ~ " +
                          dateFormat.format(shift.unavailabilityEndDate),
                      textScaleFactor: 1.1,
                    ),
                  );
                }, childCount: friend.shifts.length),
              ))
        ],
      ),
    );
  }
}
