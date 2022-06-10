import 'package:flutter/material.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/friend.dart';
import 'package:sea_mates/model/friend_list_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/view/friend_profile_view.dart';

class DayView extends StatelessWidget {
  const DayView(this.date);
  final DateTime date;

  Future<void> _openFriendView(BuildContext context, Friend friend) async {
    Navigator.of(context).push(MaterialPageRoute(
        builder: (context) => FriendProfileView(friend),
        fullscreenDialog: true));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text('Day %s'
              .i18n
              .fill([DateFormat.yMd(I18n.localeStr).format(date)])),
        ),
        body: CustomScrollView(slivers: [
          SliverToBoxAdapter(
            child: Column(
              children: [
                SizedBox(
                  height: 10,
                ),
                Text(
                  "Available Friends".i18n,
                  style: Theme.of(context).textTheme.headline6,
                ),
                Divider(
                  height: 10,
                  thickness: 2,
                )
              ],
            ),
          ),
          Consumer<FriendListModel>(
            builder: (context, model, child) {
              return FutureBuilder(
                  future: model.fetchAvailableFriends(date),
                  builder: (context, AsyncSnapshot<List<Friend>?> snapshot) {
                    if (snapshot.hasData) {
                      var friends = snapshot.data!;
                      if (friends.isEmpty) {
                        return SliverToBoxAdapter(
                            child: Center(
                                child: Text('No friends available'.i18n)));
                      } else {
                        return SliverList(
                          delegate:
                              SliverChildBuilderDelegate((context, int index) {
                            var friend = friends[index];
                            return Card(
                              child: ListTile(
                                title: Text(friend.user.name),
                                subtitle: Text(
                                    '${friend.user.username}\n${friend.user.email}'),
                                isThreeLine: true,
                                onTap: () => _openFriendView(context, friend),
                              ),
                            );
                          }, childCount: friends.length),
                        );
                      }
                    } else if (snapshot.hasError) {
                      return SliverToBoxAdapter(
                          child: Center(
                              child: Text(
                                  'Error loading friends! Please, try again!'
                                      .i18n)));
                    } else if (snapshot.data == null) {
                      return SliverToBoxAdapter(
                          child: Center(
                              child: Text('Not available in local mode'.i18n)));
                    } else {
                      return SliverFillRemaining(
                          child: Center(
                        child: CircularProgressIndicator(),
                      ));
                    }
                  });
            },
          )
        ]));
  }
}
