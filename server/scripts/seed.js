const mysql = require('mysql2/promise');

async function seed() {
  const connection = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'xiaohongshu',
  });

  try {
    console.log('Connected to database.');

    // Clear existing data
    await connection.execute('DELETE FROM comments');
    await connection.execute('DELETE FROM note_resources');
    await connection.execute('DELETE FROM notes');
    await connection.execute('DELETE FROM users');

    console.log('Cleared existing data.');

    // Insert Users
    const [userResult] = await connection.execute(
      `INSERT INTO users (username, phone, password_hash, avatar_url, bio, gender) VALUES 
      ('Alice', '13800138000', 'hash1', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Alice', 'Lover of life', 2),
      ('Bob', '13900139000', 'hash2', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Bob', 'Tech enthusiast', 1)`
    );
    const userId1 = userResult.insertId;
    const userId2 = userId1 + 1;

    console.log('Inserted users.');

    // Insert Notes
    const [noteResult] = await connection.execute(
      `INSERT INTO notes (user_id, title, content, type, cover_url, location, status) VALUES 
      (?, 'Beautiful Sunset', 'Had a great time watching the sunset today!', 1, 'https://images.unsplash.com/photo-1472214103451-9374bd1c798e', 'Shanghai', 1),
      (?, 'Delicious Food', 'This pasta was amazing.', 1, 'https://images.unsplash.com/photo-1473093295043-cdd812d0e601', 'Beijing', 1),
      (?, 'Coding Life', 'Working on a new project.', 1, 'https://images.unsplash.com/photo-1498050108023-c5249f4df085', 'Shenzhen', 1),
      (?, 'Travel Diaries', 'Missing the mountains.', 1, 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1', 'Chengdu', 1)`,
      [userId1, userId2, userId1, userId2]
    );

    console.log('Inserted notes.');

    // Insert Comments
    const noteId1 = noteResult.insertId;
    await connection.execute(
      `INSERT INTO comments (note_id, user_id, content, status) VALUES 
      (?, ?, 'Great photo!', 1),
      (?, ?, 'Looks delicious.', 1)`,
      [noteId1, userId2, noteId1, userId1]
    );
    console.log('Inserted comments.');

    console.log('Seeding complete.');
  } catch (error) {
    console.error('Error seeding data:', error);
  } finally {
    await connection.end();
  }
}

seed();
