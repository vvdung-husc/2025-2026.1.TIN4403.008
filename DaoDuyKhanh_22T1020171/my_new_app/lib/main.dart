import 'package:flutter/material.dart';
import 'package:my_new_app/views/auth/login.dart';

void main() {
  runApp(const FacebookCloneApp());
}

class FacebookCloneApp extends StatelessWidget {
  const FacebookCloneApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Facebook Clone',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1877F2), // Facebook blue
          brightness: Brightness.light,
        ),
        primaryColor: const Color(0xFF1877F2),
        scaffoldBackgroundColor: const Color(0xFFF0F2F5),
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF1877F2),
          foregroundColor: Colors.white,
          elevation: 0,
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF1877F2),
            foregroundColor: Colors.white,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(6),
            ),
          ),
        ),
      ),
      home: const LoginScreen(),
    );
  }
}

