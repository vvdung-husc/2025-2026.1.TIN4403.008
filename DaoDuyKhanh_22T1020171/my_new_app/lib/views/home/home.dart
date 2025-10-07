import 'package:flutter/material.dart';
import 'package:my_new_app/views/messenger/messenger.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _selectedIndex = 0;
  final PageController _pageController = PageController();

  @override
  void dispose() {
    _pageController.dispose();
    super.dispose();
  }

  void _onTabTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
    _pageController.animateToPage(
      index,
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF0F2F5),
      body: PageView(
        controller: _pageController,
        onPageChanged: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        children: const [
          _NewsFeedPage(),
          _MessengerPage(),
          _NotificationsPage(),
          _MenuPage(),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.fixed,
        currentIndex: _selectedIndex,
        onTap: _onTabTapped,
        selectedItemColor: const Color(0xFF1877F2),
        unselectedItemColor: Colors.grey[600],
        backgroundColor: Colors.white,
        elevation: 8,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Trang chủ',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.chat_bubble_outline),
            label: 'Messenger',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.notifications_none),
            label: 'Thông báo',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.menu),
            label: 'Menu',
          ),
        ],
      ),
    );
  }
}

class _NewsFeedPage extends StatelessWidget {
  const _NewsFeedPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF0F2F5),
      appBar: AppBar(
        backgroundColor: const Color(0xFF1877F2),
        foregroundColor: Colors.white,
      elevation: 0,
        title: const Text(
          'facebook',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
            letterSpacing: -0.5,
          ),
        ),
        actions: [
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.search),
          ),
          IconButton(
            onPressed: () {},
            icon: const Icon(Icons.chat_bubble_outline),
          ),
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            // Create Post Section
            Container(
              color: Colors.white,
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 20,
                    backgroundColor: Colors.grey[300],
                    child: const Icon(Icons.person, color: Colors.white),
                  ),
                  const SizedBox(width: 12),
            Expanded(
              child: Container(
                height: 40,
                decoration: BoxDecoration(
                        color: const Color(0xFFF0F2F5),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const TextField(
                        decoration: InputDecoration(
                          hintText: 'Bạn đang nghĩ gì?',
                          border: InputBorder.none,
                          contentPadding: EdgeInsets.symmetric(horizontal: 16),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                    IconButton(
                      onPressed: () {},
                    icon: const Icon(Icons.photo_library, color: Colors.green),
                  ),
                ],
              ),
            ),
            
            const SizedBox(height: 8),
            
            // Stories Section
            Container(
              height: 200,
              color: Colors.white,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                padding: const EdgeInsets.symmetric(vertical: 16),
                itemCount: 8,
                itemBuilder: (context, index) {
                  return Container(
                    width: 120,
                    margin: EdgeInsets.only(
                      left: index == 0 ? 16 : 8,
                      right: index == 7 ? 16 : 0,
                    ),
                    child: Column(
                      children: [
                        Container(
                          height: 140,
                          decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(8),
                            gradient: LinearGradient(
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                              colors: [
                                Colors.blue[400]!,
                                Colors.purple[400]!,
          ],
        ),
      ),
                          child: index == 0
                              ? const Center(
                                  child: Icon(
                                    Icons.add,
                                    color: Colors.white,
                                    size: 30,
                                  ),
                                )
                              : const Center(
                                  child: Icon(
                                    Icons.person,
      color: Colors.white,
                                    size: 40,
                                  ),
                                ),
                        ),
                        const SizedBox(height: 8),
                Text(
                          index == 0 ? 'Tạo tin' : 'Tên bạn bè ${index}',
                          style: const TextStyle(fontSize: 12),
                          textAlign: TextAlign.center,
                          maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          );
        },
              ),
            ),
            
            const SizedBox(height: 8),
            
            // Posts
            ...List.generate(5, (index) => _buildPostCard(index)),
          ],
        ),
      ),
    );
  }

  Widget _buildPostCard(int index) {
    return Container(
      color: Colors.white,
      margin: const EdgeInsets.only(bottom: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Post Header
          Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 20,
                  backgroundColor: Colors.grey[300],
                  child: const Icon(Icons.person, color: Colors.white),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
            children: [
                      Text(
                        'Tên người dùng ${index + 1}',
                        style: const TextStyle(
                          fontWeight: FontWeight.w600,
                          fontSize: 15,
                        ),
                      ),
                      Text(
                        '${index + 1} giờ trước',
                        style: TextStyle(
                          color: Colors.grey[600],
                          fontSize: 13,
                        ),
                      ),
                    ],
                  ),
                ),
                IconButton(
                  onPressed: () {},
                  icon: Icon(Icons.more_horiz, color: Colors.grey[600]),
                ),
              ],
            ),
          ),
          
          // Post Content
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Text(
              'Đây là nội dung bài viết thứ ${index + 1}. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
              style: const TextStyle(fontSize: 15),
            ),
          ),
          
          const SizedBox(height: 12),
          
          // Post Image
          Container(
            height: 200,
            width: double.infinity,
            color: Colors.grey[300],
            child: const Center(
              child: Icon(
                Icons.image,
                size: 50,
                color: Colors.grey,
              ),
            ),
          ),
          
          // Post Actions
          Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              children: [
                Row(
                  children: [
                    Icon(Icons.thumb_up, color: Colors.blue[600], size: 16),
                    const SizedBox(width: 4),
                    Text(
                      '${(index + 1) * 12}',
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                    const Spacer(),
                    Text(
                      '${(index + 1) * 3} bình luận',
                      style: TextStyle(color: Colors.grey[600]),
                    ),
                  ],
                ),
                const Divider(height: 20),
                Row(
                  children: [
                    Expanded(
                      child: _buildActionButton(
                        icon: Icons.thumb_up_outlined,
                        label: 'Thích',
                        onTap: () {},
                      ),
                    ),
                    Expanded(
                      child: _buildActionButton(
                        icon: Icons.chat_bubble_outline,
                        label: 'Bình luận',
                        onTap: () {},
                      ),
                    ),
                    Expanded(
                      child: _buildActionButton(
                        icon: Icons.share_outlined,
                        label: 'Chia sẻ',
                        onTap: () {},
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildActionButton({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 20, color: Colors.grey[600]),
            const SizedBox(width: 8),
            Text(
              label,
              style: TextStyle(
                color: Colors.grey[600],
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _MessengerPage extends StatelessWidget {
  const _MessengerPage();

  @override
  Widget build(BuildContext context) {
    return const MessengerScreen();
  }
}

class _NotificationsPage extends StatelessWidget {
  const _NotificationsPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF0F2F5),
      appBar: AppBar(
        backgroundColor: const Color(0xFF1877F2),
        foregroundColor: Colors.white,
        elevation: 0,
        title: const Text('Thông báo'),
      ),
      body: Center(
      child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
        children: [
            Icon(
              Icons.notifications_none,
              size: 80,
              color: Colors.grey[400],
            ),
            const SizedBox(height: 16),
          Text(
              'Chưa có thông báo nào',
              style: TextStyle(
                fontSize: 18,
                color: Colors.grey[600],
                ),
              ),
            ],
          ),
      ),
    );
  }
}

class _MenuPage extends StatelessWidget {
  const _MenuPage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF0F2F5),
      appBar: AppBar(
        backgroundColor: const Color(0xFF1877F2),
        foregroundColor: Colors.white,
        elevation: 0,
        title: const Text('Menu'),
      ),
      body: ListView(
        children: [
          // Profile Section
          Container(
            color: Colors.white,
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 30,
                  backgroundColor: Colors.grey[300],
                  child: const Icon(Icons.person, color: Colors.white, size: 30),
                ),
                const SizedBox(width: 16),
                const Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                        'Tên người dùng',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      Text(
                        'Xem trang cá nhân của bạn',
                        style: TextStyle(
                          fontSize: 14,
                          color: Colors.grey,
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          
          const SizedBox(height: 8),
          
          // Menu Items
          _buildMenuItem(Icons.people, 'Bạn bè', () {}),
          _buildMenuItem(Icons.save, 'Đã lưu', () {}),
          _buildMenuItem(Icons.event, 'Sự kiện', () {}),
          _buildMenuItem(Icons.flag, 'Trang', () {}),
          _buildMenuItem(Icons.group, 'Nhóm', () {}),
          _buildMenuItem(Icons.shopping_bag, 'Marketplace', () {}),
          _buildMenuItem(Icons.video_call, 'Video trực tiếp', () {}),
          _buildMenuItem(Icons.settings, 'Cài đặt', () {}),
          _buildMenuItem(Icons.help, 'Trợ giúp', () {}),
          _buildMenuItem(Icons.logout, 'Đăng xuất', () {
            // Navigate back to login
          }),
        ],
      ),
    );
  }

  Widget _buildMenuItem(IconData icon, String title, VoidCallback onTap) {
    return Container(
      color: Colors.white,
      child: ListTile(
        leading: Icon(icon, color: const Color(0xFF1877F2)),
        title: Text(
          title,
          style: const TextStyle(fontSize: 16),
        ),
        trailing: const Icon(Icons.chevron_right, color: Colors.grey),
        onTap: onTap,
      ),
    );
  }
}
