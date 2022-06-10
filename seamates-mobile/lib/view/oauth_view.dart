import 'dart:io';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sea_mates/data/social_user.dart';
import 'package:sea_mates/model/user_model.dart';
import 'package:sea_mates/strings.i18n.dart';
import 'package:sea_mates/util/api_utils.dart';
import 'package:webview_flutter/webview_flutter.dart';

class OAuthView extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _OAuthViewState();
}

class _OAuthViewState extends State<OAuthView> {
  @override
  void initState() {
    super.initState();
    if (Platform.isAndroid) WebView.platform = SurfaceAndroidWebView();
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<NavigationDecision> _handleNavigation(
      NavigationRequest request) async {
    var url = Uri.parse(request.url);
    if (url.path.contains("loginSuccess")) {
      await _handleLoginSuccess(url);
      return NavigationDecision.prevent;
    } else if (url.path.contains('socialSignup')) {
      await _handleSocialSignup(url);
      return NavigationDecision.prevent;
    }
    return NavigationDecision.navigate;
  }

  Future<void> _handleLoginSuccess(Uri url) async {
    String token = 'Bearer ' + url.queryParameters['token']!;
    await Provider.of<UserModel>(context, listen: false)
        .socialLogin(token)
        .then((success) async {
      if (success) {
        await Navigator.pushNamedAndRemoveUntil(context, '/home', (_) => false);
      } else {
        await _showFailureDialog(context, 'Login failed'.i18n,
            'Could not fetch user information'.i18n);
      }
    }).catchError((e) async {
      await _showFailureDialog(context, 'Login failed'.i18n,
          'Sorry, something went wrong. Please try again!'.i18n);
    });
    await Navigator.pushNamedAndRemoveUntil(
        context, '/welcome', (route) => false);
  }

  Future<void> _handleSocialSignup(Uri url) async {
    var socialUser = SocialUser.empty();
    socialUser.name = url.queryParameters['name']!;
    socialUser.email = url.queryParameters['email']!;
    socialUser.registrationId = url.queryParameters['registrationId']!;
    socialUser.socialId = url.queryParameters['socialId']!;

    await Navigator.pushNamedAndRemoveUntil(
        context, '/socialSignup', (route) => false,
        arguments: socialUser);
  }

  @override
  Widget build(BuildContext context) {
    return WebView(
      javascriptMode: JavascriptMode.unrestricted,
      initialUrl: Uri.https(ApiUtils.API_BASE, 'oauth2/authorization/facebook')
          .toString(),
      navigationDelegate: _handleNavigation,
    );
  }
}

Future _showFailureDialog(
    BuildContext context, String title, String failedMessage) async {
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
