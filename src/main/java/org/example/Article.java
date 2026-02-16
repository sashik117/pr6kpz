package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Article extends ActiveRecord {
    private String title;
    private String content;
    private String author;

    public Article() {
    }

    public Article(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    protected String getTableName() {
        return "articles";
    }
    @Override
    protected String getInsertFields() {
        return "title, content, author";
    }
    @Override
    protected String getInsertPlaceholders() {
        return "?, ?, ?";
    }
    @Override
    protected String getUpdateSet() {
        return "title = ?, content = ?, author = ?";
    }
    @Override
    protected void insertValues(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, this.title);
        stmt.setString(2, this.content);
        stmt.setString(3, this.author);
    }
    @Override
    protected void updateValues(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, this.title);
        stmt.setString(2, this.content);
        stmt.setString(3, this.author);
    }
    @Override
    protected void loadFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.title = rs.getString("title");
        this.content = rs.getString("content");
        this.author = rs.getString("author");
        this.createdAt = rs.getTimestamp("created_at");
        this.updatedAt = rs.getTimestamp("updated_at");
    }
    @Override
    protected int getUpdateParameterCount() {
        return 3; // title, content, author
    }

    public static Article findById(Integer id) {
        return ActiveRecord.findById(Article.class, id);
    }

    public static List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM articles ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Article article = new Article();
                article.loadFromResultSet(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Помилка при отриманні всіх статей!");
            e.printStackTrace();
        }
        return articles;
    }

    public static List<Article> findByAuthor(String author) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT * FROM articles WHERE author = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Article article = new Article();
                article.loadFromResultSet(rs);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Помилка при пошуку статей за автором!");
            e.printStackTrace();
        }
        return articles;
    }

    public List<Comment> getComments() {
        return Comment.findByArticleId(this.id);
    }

    public Comment addComment(String author, String content) {
        Comment comment = new Comment(this.id, author, content);
        if (comment.save()) {
            return comment;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
