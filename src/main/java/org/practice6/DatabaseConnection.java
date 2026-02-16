package org.practice6;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:blog.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Підключення до бази даних встановлено.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC драйвер не знайдено!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Помилка підключення до бази даних!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("З'єднання з базою даних закрито.");
            }
        } catch (SQLException e) {
            System.err.println("Помилка при закритті з'єднання!");
            e.printStackTrace();
        }
    }

    public static void initDatabase() {
        Connection conn = getConnection();
        try {
            Statement stmt = conn.createStatement();

            // Створення таблиці articles
            String createArticlesTable = """
                CREATE TABLE IF NOT EXISTS articles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    author TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.execute(createArticlesTable);

            // Створення таблиці comments
            String createCommentsTable = """
                CREATE TABLE IF NOT EXISTS comments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    article_id INTEGER NOT NULL,
                    author TEXT NOT NULL,
                    content TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
                )
            """;
            stmt.execute(createCommentsTable);

            System.out.println("Таблиці успішно створено або вже існують.");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Помилка при створенні таблиць!");
            e.printStackTrace();
        }
    }
}
