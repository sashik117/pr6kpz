package org.practice6;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Comment extends ActiveRecord {
    private Integer articleId;
    private String author;
    private String content;

    public Comment() {
    }

    public Comment(Integer articleId, String author, String content) {
        this.articleId = articleId;
        this.author = author;
        this.content = content;
    }

    public Integer getArticleId() {
        return articleId;
    }
    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    protected String getTableName() {
        return "comments";
    }
    @Override
    protected String getInsertFields() {
        return "article_id, author, content";
    }
    @Override
    protected String getInsertPlaceholders() {
        return "?, ?, ?";
    }
    @Override
    protected String getUpdateSet() {
        return "article_id = ?, author = ?, content = ?";
    }
    @Override
    protected void insertValues(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, this.articleId);
        stmt.setString(2, this.author);
        stmt.setString(3, this.content);
    }
    @Override
    protected void updateValues(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, this.articleId);
        stmt.setString(2, this.author);
        stmt.setString(3, this.content);
    }
    @Override
    protected void loadFromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.articleId = rs.getInt("article_id");
        this.author = rs.getString("author");
        this.content = rs.getString("content");
        this.createdAt = rs.getTimestamp("created_at");
    }
    @Override
    protected int getUpdateParameterCount() {
        return 3; // article_id, author, content
    }

    public static Comment findById(Integer id) {
        return ActiveRecord.findById(Comment.class, id);
    }

    public static List<Comment> findByArticleId(Integer articleId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE article_id = ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, articleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.loadFromResultSet(rs);
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Помилка при отриманні коментарів!");
            e.printStackTrace();
        }
        return comments;
    }

    public static List<Comment> findByAuthor(String author) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE author = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, author);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comment comment = new Comment();
                comment.loadFromResultSet(rs);
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Помилка при пошуку коментарів за автором!");
            e.printStackTrace();
        }
        return comments;
    }

    public Article getArticle() {
        return Article.findById(this.articleId);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}