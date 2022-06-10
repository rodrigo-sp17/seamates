import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/auth_user.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';

import '../util/validators.dart';

class ProfileView extends StatefulWidget {
  const ProfileView(this.defaultActions);
  final List<Widget> defaultActions;

  @override
  State<StatefulWidget> createState() => _ProfileViewState();
}

class _ProfileViewState extends State<ProfileView> {
  bool editing = false;
  List<Widget> actions = [];

  final _usernameController = TextEditingController();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _usernameController.text = "username";
    _nameController.text = "Name Surname";
    _emailController.text = "email@domain.com";
    actions = widget.defaultActions;
  }

  @override
  void dispose() {
    _usernameController.dispose();
    _nameController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  void _editValue(
      TextEditingController ctrl, String? Function(String?) validator) async {
    GlobalKey<FormState> key = new GlobalKey<FormState>();
    String? result;
    await showDialog(
        context: context,
        builder: (_) => AlertDialog(
              title: Text("Enter a new value".i18n),
              content: Form(
                key: key,
                child: TextFormField(
                  autovalidateMode: AutovalidateMode.onUserInteraction,
                  initialValue: ctrl.text,
                  validator: validator,
                  onSaved: (val) {
                    Navigator.pop(context, val);
                  },
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () {
                    Navigator.pop(context);
                  },
                  child: Text("CANCEL".i18n),
                ),
                TextButton(
                    onPressed: () {
                      var form = key.currentState!;
                      if (form.validate()) {
                        form.save();
                      }
                    },
                    child: Text('SAVE'.i18n)),
              ],
            )).then((value) => result = value);

    if (result != null) {
      setState(() {
        actions = [
          TextButton(
              style: ButtonStyle(
                  foregroundColor: MaterialStateProperty.all(Colors.white)),
              onPressed: _submitChanges,
              child: Text("SAVE CHANGES".i18n)),
        ];
        editing = true;
        ctrl.text = result!;
      });
    }
  }

  void _submitChanges() async {
    setState(() {
      actions = widget.defaultActions;
    });

    await Provider.of<UserModel>(context, listen: false)
        .editUser(_usernameController.text, _emailController.text)
        .then((success) {
      if (!success) {
        _showErrorDialog(context, 'Ops...', 'Edition failed!'.i18n);
      }
    }).catchError((e) {
      if (e is ConflictException) {
        _showErrorDialog(context, 'Edition failed'.i18n,
            'The email already exists. Please choose another one'.i18n);
      } else {
        _showErrorDialog(
            context, 'Edition failed'.i18n, 'Unexpected server response'.i18n);
      }
    });
    Navigator.pop(context);

    setState(() {
      editing = false;
    });
  }

  void _deleteAccount() async {
    var password = await _showDeletionDialog(context);
    if (password != null) {
      await Provider.of<UserModel>(context, listen: false)
          .deleteAccount(password)
          .then((success) {
        if (success) {
          Navigator.pushNamedAndRemoveUntil(
              context, '/welcome', (route) => false);
        } else {
          _showErrorDialog(context, 'Deletion failed'.i18n,
              'Sorry, the deletion was not authorized'.i18n);
        }
      }).catchError((e) {
        if (e is RestException) {
          _showErrorDialog(
              context,
              "Deletion failed".i18n,
              'Sorry, deletion could not be performed at this moment. Could you try logging in again?'
                  .i18n);
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    const sizedBox = SizedBox(
      height: 15,
    );
    return CustomScrollView(
      slivers: [
        SliverAppBar(
          automaticallyImplyLeading: false,
          title: Text("Profile Info".i18n),
          actions: actions,
        ),
        SliverPadding(
          padding: EdgeInsets.all(20),
          sliver: SliverList(
              delegate: SliverChildListDelegate([
            Center(
              heightFactor: 4,
              child: Icon(Icons.person_sharp),
            ),
            Consumer<UserModel>(builder: (context, model, child) {
              if (model.userStatus == UserStatus.AUTH && editing == false) {
                AuthenticatedUser user = model.user as AuthenticatedUser;
                _usernameController.text = user.username;
                _nameController.text = user.name;
                _emailController.text = user.email;
              } else if (model.userStatus == UserStatus.LOCAL) {
                return Center(
                  child: Text(
                    'Logged as Local User'.i18n,
                    textScaleFactor: 1.3,
                  ),
                );
              }
              return Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  TextField(
                    controller: _usernameController,
                    enabled: true,
                    readOnly: true,
                    decoration: InputDecoration(
                      border: OutlineInputBorder(),
                      icon: Icon(Icons.person_outline),
                      labelText: "Username".i18n,
                    ),
                  ),
                  sizedBox,
                  TextField(
                    controller: _nameController,
                    enabled: true,
                    readOnly: true,
                    decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        icon: Icon(Icons.person),
                        labelText: "Name".i18n,
                        suffixIcon: IconButton(
                          icon: Icon(Icons.edit),
                          onPressed: () {
                            _editValue(
                                _nameController, Validators.validateName);
                          },
                        )),
                  ),
                  sizedBox,
                  TextField(
                    controller: _emailController,
                    enabled: true,
                    readOnly: true,
                    decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        icon: Icon(Icons.email),
                        labelText: "Email".i18n,
                        suffixIcon: IconButton(
                          icon: Icon(Icons.edit),
                          onPressed: () {
                            _editValue(
                                _emailController, Validators.validateEmail);
                          },
                        )),
                  ),
                  sizedBox,
                  Visibility(
                      visible: !model.loaded,
                      child: SizedBox(
                        height: 90,
                        child: Center(child: CircularProgressIndicator()),
                      )),
                ],
              );
            }),
            Divider(
              height: 30,
              thickness: 3,
            ),
            Consumer<UserModel>(builder: (context, model, child) {
              if (!model.loaded) {
                return sizedBox;
              }
              switch (model.userStatus) {
                case UserStatus.AUTH:
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      ElevatedButton(
                        child: Text(
                          'LOGOUT',
                        ),
                        onPressed: () => _showLogoutDialog(context),
                      ),
                      Divider(
                        height: 30,
                        thickness: 3,
                      ),
                      ElevatedButton(
                        style: ButtonStyle(
                            foregroundColor:
                                MaterialStateProperty.all(Colors.black),
                            backgroundColor:
                                MaterialStateProperty.all(Colors.red)),
                        child: Text(
                          "DELETE ACCOUNT".i18n,
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        onPressed: () => _deleteAccount(),
                      ),
                    ],
                  );
                case UserStatus.LOCAL:
                  return ElevatedButton(
                      onPressed: () => Navigator.pushNamed(context, '/welcome'),
                      child: Text('UPGRADE ACCOUNT'.i18n));
                default:
                  return Center();
              }
            }),
          ])),
        )
      ],
    );
  }
}

void _showErrorDialog(BuildContext context, String title, String content) {
  showDialog(
      context: context,
      builder: (_) => AlertDialog(
            title: Text(title),
            content: SingleChildScrollView(
              child: Text(content),
            ),
            actions: [
              TextButton(
                  onPressed: () => Navigator.pop(context), child: Text('OK'))
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
                child: Text('CANCEL'.i18n),
              ),
              TextButton(
                  onPressed: () async {
                    await Provider.of<UserModel>(context, listen: false)
                        .logout();
                    Navigator.pushNamedAndRemoveUntil(
                        context, '/welcome', (_) => false);
                  },
                  child: Text('YES, LOG ME OUT'.i18n))
            ],
          ));
}

Future<String?> _showDeletionDialog(BuildContext context) async {
  String? password = "";

  var value = await showDialog(
      context: context,
      builder: (_) => AlertDialog(
            scrollable: true,
            title: Text("Account deletion".i18n),
            content: Column(
              children: [
                Text(
                    'This actions is permanent.\nPlease confirm your password on the field below to allow deletion:'
                        .i18n),
                TextFormField(
                  keyboardType: TextInputType.visiblePassword,
                  textInputAction: TextInputAction.done,
                  obscureText: true,
                  autofillHints: [AutofillHints.password],
                  onChanged: (value) {
                    password = value;
                  },
                  onFieldSubmitted: (value) {
                    password = value;
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
                  child: Text('YES, DELETE MY ACCOUNT'.i18n))
            ],
          ));
  if (value) {
    return password;
  } else {
    return null;
  }
}
