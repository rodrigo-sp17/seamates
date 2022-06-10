import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/social_user.dart';
import 'package:sea_mates/exception/rest_exceptions.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/validators.dart';

class SocialSignupPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final SocialUser user =
        ModalRoute.of(context)!.settings.arguments as SocialUser;

    return Scaffold(
      body: SocialSignupForm(user),
    );
  }
}

class SocialSignupForm extends StatefulWidget {
  SocialSignupForm(this.user);
  final SocialUser user;

  @override
  State<StatefulWidget> createState() => _SignupFormState();
}

class _SignupFormState extends State<SocialSignupForm> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  late SocialUser user;

  @override
  void initState() {
    super.initState();
    user = widget.user;
  }

  void _submit() async {
    var form = _formKey.currentState!;
    if (!form.validate()) {
      return;
    }

    form.save();
    var userModel = Provider.of<UserModel>(context, listen: false);

    var result = userModel.socialSignup(user);
    await result.then(
      (success) async {
        if (success) {
          Navigator.pushReplacementNamed(context, '/home');
        } else {
          _showDialog("Unauthorized", "You are not authorized to signup".i18n);
        }
      },
    ).catchError((e) {
      if (e is RestException) {
        _showDialog("Signup failed".i18n, e.message);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Form(
        key: _formKey,
        child: Scrollbar(
          child: ListView(
            padding: EdgeInsets.all(20),
            children: [
              SizedBox(
                height: 150,
              ),
              TextFormField(
                initialValue: user.name,
                autovalidateMode: AutovalidateMode.onUserInteraction,
                keyboardType: TextInputType.name,
                textInputAction: TextInputAction.next,
                decoration: InputDecoration(
                    icon: const Icon(Icons.person_outline),
                    labelText: 'Name'.i18n),
                autofillHints: [
                  AutofillHints.name,
                ],
                validator: Validators.validateName,
                onSaved: (value) {
                  user.name = value!;
                },
              ),
              TextFormField(
                autovalidateMode: AutovalidateMode.onUserInteraction,
                keyboardType: TextInputType.name,
                textInputAction: TextInputAction.next,
                decoration: InputDecoration(
                    icon: const Icon(Icons.person), labelText: 'Username'.i18n),
                autofillHints: [
                  AutofillHints.username,
                ],
                validator: Validators.validateUsername,
                onSaved: (value) {
                  user.username = value!;
                },
              ),
              TextFormField(
                initialValue: user.email,
                autovalidateMode: AutovalidateMode.onUserInteraction,
                keyboardType: TextInputType.emailAddress,
                textInputAction: TextInputAction.next,
                decoration: InputDecoration(
                    icon: const Icon(Icons.person_outline),
                    labelText: 'Email'.i18n),
                autofillHints: [
                  AutofillHints.email,
                ],
                validator: Validators.validateEmail,
                onSaved: (value) {
                  user.email = value!;
                },
              ),
              SizedBox(
                height: 30,
              ),
              Consumer<UserModel>(
                builder: (context, model, child) {
                  if (model.loaded) {
                    return ElevatedButton(
                        style: ButtonStyle(),
                        onPressed: _submit,
                        child: Text(
                          'SIGNUP'.i18n,
                          textScaleFactor: 1.2,
                        ));
                  } else {
                    return Center(child: CircularProgressIndicator());
                  }
                },
              ),
            ],
          ),
        ));
  }

  Future _showDialog(String title, String failedMessage) {
    return showDialog(
        context: context,
        builder: (_) => AlertDialog(
              title: Text(title),
              content: Text(failedMessage),
              actions: [
                TextButton(
                  child: Text("OK"),
                  onPressed: () => Navigator.pop(context),
                )
              ],
            ));
  }
}
