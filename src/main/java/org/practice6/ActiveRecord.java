package org.practice6;

import java.sql.*;

public abstract class ActiveRecord {
    protected Integer id;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;

    protected abstract String getTableName();
    protected abstract void insertValues(PreparedStatement stmt) throws SQLException;
    protected abstract void updateValues(PreparedStatement stmt) throws SQLException;
    protected abstract void loadFromResultSet(ResultSet rs) throws SQLException;
    protected abstract String getInsertFields();
    protected abstract String getInsertPlaceholders();
    protected abstract String getUpdateSet();

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public boolean save() {
        if (id == null) {
            return insert();
        } else {
            return update();
        }
    }

    private boolean insert() {
        String sql = "INSERT INTO " + getTableName() + " (" + getInsertFields() +
                ") VALUES (" + getInsertPlaceholders() + ")";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            insertValues(stmt);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                }
                System.out.println("Запис успішно додано. ID: " + this.id);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Помилка при додаванні запису!");
            e.printStackTrace();
        }
        return false;
    }

    private boolean update() {
        // Перевіряємо, чи потрібно оновлювати updated_at (не для всіх таблиць є це поле)
        String updateSet = getUpdateSet();
        String sql = "UPDATE " + getTableName() + " SET " + updateSet;

        // Додаємо updated_at тільки якщо таблиця його має (перевіряємо за назвою таблиці)
        if (!getTableName().equals("comments")) {
            sql += ", updated_at = CURRENT_TIMESTAMP";
        }
        sql += " WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            updateValues(stmt);
            stmt.setInt(getUpdateParameterCount() + 1, this.id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Запис ID=" + this.id + " успішно оновлено.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Помилка при оновленні запису!");
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete() {
        if (id == null) {
            System.err.println("Неможливо видалити запис без ID!");
            return false;
        }

        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Запис ID=" + this.id + " успішно видалено.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Помилка при видаленні запису!");
            e.printStackTrace();
        }
        return false;
    }

    protected static <T extends ActiveRecord> T findById(Class<T> clazz, Integer id) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            String sql = "SELECT * FROM " + instance.getTableName() + " WHERE id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    instance.loadFromResultSet(rs);
                    return instance;
                }
            }
        } catch (Exception e) {
            System.err.println("Помилка при пошуку запису!");
            e.printStackTrace();
        }
        return null;
    }

    protected abstract int getUpdateParameterCount();
}