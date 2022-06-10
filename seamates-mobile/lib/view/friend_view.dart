import 'package:flutter/material.dart';
import 'package:i18n_extension/i18n_widget.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/friend.dart';
import 'package:sea_mates/model/friend_list_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/view/friend_profile_view.dart';

class FriendView extends StatefulWidget {
  const FriendView(this.defaultActions);
  final List<Widget> defaultActions;

  @override
  State<StatefulWidget> createState() => _FriendViewState();
}

class _FriendViewState extends State<FriendView> {
  final String title = "Friends".i18n;
  final DateFormat dateFormat = DateFormat.yMd(I18n.localeStr);

  Future<void> _requestFriendship(String username) async {
    var message = await Provider.of<FriendListModel>(context, listen: false)
        .requestFriendship(username);

    if (message != null) {
      _showSnackbar(context, message);
    } else {
      _showSnackbar(context, "Friendship requested!".i18n);
    }
  }

  Future<void> _acceptFriendship(String username) async {
    var message = await Provider.of<FriendListModel>(context, listen: false)
        .acceptFriendship(username);

    if (message != null) {
      _showSnackbar(context, message);
    } else {
      _showSnackbar(context, "Friendship accepted!".i18n);
    }
  }

  Future<void> _removeFriendship(String username) async {
    var proceed = await _showUnfriendDialog(context, username);
    if (proceed) {
      var message = await Provider.of<FriendListModel>(context, listen: false)
          .removeFriend(username);

      if (message != null) {
        _showSnackbar(context, message);
      } else {
        _showSnackbar(context, "Unfriended %s!".i18n.fill([username]));
      }
    }
  }

  Future<void> _openFriendView(Friend friend) async {
    Navigator.of(context).push(MaterialPageRoute(
        builder: (context) => FriendProfileView(friend),
        fullscreenDialog: true));
  }

  Future<void> _refresh() async {
    var message =
        await Provider.of<FriendListModel>(context, listen: false).refresh();
    if (message != null) {
      _showSnackbar(context, message);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        RefreshIndicator(
          onRefresh: () async => _refresh(),
          child: CustomScrollView(
            slivers: [
              SliverAppBar(
                automaticallyImplyLeading: false,
                title: Text(title),
                pinned: true,
                actions: widget.defaultActions,
              ),
              SliverToBoxAdapter(
                child: Column(
                  children: [
                    SizedBox(
                      height: 10,
                    ),
                    Text(
                      "Requests".i18n,
                      textScaleFactor: 1.3,
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
                  if (!model.isLoading) {
                    var requests = model.otherRequests;
                    return requests.isEmpty
                        ? SliverToBoxAdapter(
                            child: SizedBox.shrink(),
                          )
                        : SliverList(
                            delegate: SliverChildBuilderDelegate(
                                (context, int index) {
                              var request = requests[index];
                              return OtherRequestCard(
                                  request, _acceptFriendship);
                            }, childCount: requests.length),
                          );
                  } else {
                    return SliverToBoxAdapter(child: SizedBox.shrink());
                  }
                },
              ),
              Consumer<FriendListModel>(
                builder: (context, model, child) {
                  if (!model.isLoading) {
                    var requests = model.myRequests;
                    return requests.isEmpty
                        ? SliverToBoxAdapter(
                            child: SizedBox.shrink(),
                          )
                        : SliverList(
                            delegate: SliverChildBuilderDelegate(
                                (context, int index) {
                              var request = requests[index];
                              return MyRequestCard(request);
                            }, childCount: requests.length),
                          );
                  } else {
                    return SliverToBoxAdapter(child: SizedBox.shrink());
                  }
                },
              ),
              SliverToBoxAdapter(
                child: Column(
                  children: [
                    SizedBox(
                      height: 10,
                    ),
                    Text(
                      "Friends".i18n,
                      textScaleFactor: 1.3,
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
                  if (model.isLoading) {
                    return SliverFillRemaining(
                        child: Center(
                      child: CircularProgressIndicator(),
                    ));
                  } else {
                    var friends = model.friends;
                    return friends.isEmpty
                        ? SliverToBoxAdapter(
                            child: SizedBox.shrink(),
                          )
                        : SliverList(
                            delegate: SliverChildBuilderDelegate(
                                (context, int index) {
                              var friend = friends[index];
                              return FriendCard(
                                  friend, _removeFriendship, _openFriendView);
                            }, childCount: friends.length),
                          );
                  }
                },
              ),
            ],
          ),
        ),
        Positioned(
          bottom: 20,
          right: 20,
          child: FloatingActionButton(
              child: Icon(Icons.add),
              onPressed: () async {
                var username = await _showAddFriendDialog(context);
                _requestFriendship(username!);
              }),
        )
      ],
    );
  }
}

class MyRequestCard extends StatelessWidget {
  const MyRequestCard(this.req);

  final FriendRequest req;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text(req.targetName),
        subtitle: Text('%s\nRequested on %s'.i18n.fill([
          req.targetUsername,
          DateFormat.yMd(I18n.localeStr).format(req.timestamp)
        ]).i18n),
        isThreeLine: true,
      ),
    );
  }
}

class OtherRequestCard extends StatelessWidget {
  const OtherRequestCard(this.req, this.acceptCallback);

  final FriendRequest req;
  final Function(String) acceptCallback;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text(req.sourceName),
        subtitle: Text('%s\n%s'.i18n.fill([
          req.sourceUsername,
          DateFormat.yMd(I18n.localeStr).format(req.timestamp)
        ])),
        isThreeLine: true,
        trailing: TextButton(
          onPressed: () => acceptCallback(req.sourceUsername),
          child: Text('ACCEPT'.i18n),
        ),
      ),
    );
  }
}

class FriendCard extends StatelessWidget {
  const FriendCard(this.friend, this.removeCallback, this.tapCallback);
  final Friend friend;
  final Function(String) removeCallback;
  final Function(Friend) tapCallback;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        leading: Container(
            height: double.infinity,
            child: friend.isAvailable(DateTime.now())
                ? Icon(
                    Icons.home,
                    semanticLabel: 'On land'.i18n,
                  )
                : Icon(
                    Icons.directions_boat,
                    semanticLabel: "On sea".i18n,
                  )),
        title: Text(friend.user.name),
        subtitle: Text('${friend.user.username}\n${friend.user.email}'),
        onTap: () => tapCallback(friend),
        trailing: IconButton(
            icon: Icon(
              Icons.cancel,
              semanticLabel: 'Remove friendship'.i18n,
            ),
            onPressed: () async => removeCallback(friend.user.username)),
        isThreeLine: true,
      ),
    );
  }
}

Future<String?> _showAddFriendDialog(BuildContext context) async {
  String? username = "";

  var value = await showDialog(
      context: context,
      builder: (_) => AlertDialog(
            scrollable: true,
            title: Text("Request friendship".i18n),
            content: Column(
              children: [
                Text('Type the username of the friend you want to add:'.i18n),
                TextFormField(
                  keyboardType: TextInputType.name,
                  textInputAction: TextInputAction.done,
                  autofillHints: [AutofillHints.username],
                  onChanged: (value) {
                    username = value;
                  },
                  onFieldSubmitted: (value) {
                    username = value;
                  },
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: Text('CANCEL'.i18n),
              ),
              TextButton(
                  onPressed: () => Navigator.pop(context, true),
                  child: Text('REQUEST FRIENDSHIP'.i18n))
            ],
          ));

  if (value) {
    return username;
  } else {
    return null;
  }
}

Future<bool> _showUnfriendDialog(BuildContext context, String name) async {
  return await showDialog(
      context: context,
      builder: (_) => AlertDialog(
            title: Text("Remove friendship".i18n),
            content: SingleChildScrollView(
                child: Text(
                    'Are you sure you want to unfriend %s?'.i18n.fill([name]))),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: Text('CANCEL'.i18n),
              ),
              TextButton(
                  onPressed: () => Navigator.pop(context, true),
                  child: Text('UNFRIEND'.i18n))
            ],
          ));
}

/// timeout in seconds
void _showSnackbar(BuildContext context, String message, [int timeout = 2]) {
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(
    content: Text(message),
    duration: Duration(seconds: timeout),
  ));
}
