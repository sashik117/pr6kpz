package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Система управління статтями блогу ===\n");

        DatabaseConnection.initDatabase();
        System.out.println();

        System.out.println("--- CREATE: Створення нових статей ---");

        Article article1 = new Article(
                "Вступ до патернів проектування",
                "Патерни проектування - це типові рішення поширених проблем, які виникають при проектуванні програмного забезпечення. Active Record є одним з таких патернів.",
                "Іван Петренко"
        );
        article1.save();

        Article article2 = new Article(
                "Основи роботи з базами даних",
                "База даних - це організована колекція структурованої інформації або даних, які зазвичай зберігаються в електронному вигляді в комп'ютерній системі.",
                "Марія Іванова"
        );
        article2.save();

        Article article3 = new Article(
                "Java та об'єктно-орієнтоване програмування",
                "Java - це об'єктно-орієнтована мова програмування, яка широко використовується для розробки різних типів застосунків.",
                "Іван Петренко"
        );
        article3.save();
        System.out.println();

        System.out.println("--- READ: Читання всіх статей ---");
        List<Article> allArticles = Article.findAll();
        for (Article article : allArticles) {
            System.out.println(article);
        }
        System.out.println();

        System.out.println("--- READ: Пошук статті за ID ---");
        Article foundArticle = Article.findById(1);
        if (foundArticle != null) {
            System.out.println("Знайдено: " + foundArticle);
            System.out.println("Повний зміст: " + foundArticle.getContent());
        }
        System.out.println();

        System.out.println("--- READ: Пошук статей за автором ---");
        List<Article> articlesByAuthor = Article.findByAuthor("Іван Петренко");
        System.out.println("Статті автора 'Іван Петренко':");
        for (Article article : articlesByAuthor) {
            System.out.println("  - " + article.getTitle());
        }
        System.out.println();

        System.out.println("--- CREATE: Додавання коментарів до статей ---");

        Comment comment1 = new Comment(1, "Олександр", "Дуже корисна стаття! Дякую за пояснення.");
        comment1.save();

        Comment comment2 = new Comment(1, "Наталія", "Чи можете дати більше прикладів використання Active Record?");
        comment2.save();

        Comment comment3 = new Comment(2, "Петро", "Цікаво написано про бази даних!");
        comment3.save();

        if (foundArticle != null) {
            foundArticle.addComment("Андрій", "Чудова робота! Хочу дізнатися більше.");
        }
        System.out.println();

        System.out.println("--- READ: Отримання коментарів до статті ---");
        if (foundArticle != null) {
            List<Comment> comments = foundArticle.getComments();
            System.out.println("Коментарі до статті '" + foundArticle.getTitle() + "':");
            for (Comment comment : comments) {
                System.out.println("  [" + comment.getAuthor() + "]: " + comment.getContent());
            }
        }
        System.out.println();

        System.out.println("--- UPDATE: Оновлення статті ---");
        if (foundArticle != null) {
            System.out.println("Стара назва: " + foundArticle.getTitle());
            foundArticle.setTitle("Вступ до патернів проектування (оновлено)");
            foundArticle.setContent(foundArticle.getContent() + " Оновлено з додатковою інформацією про переваги використання патернів.");
            foundArticle.save();
            System.out.println("Нова назва: " + foundArticle.getTitle());
        }
        System.out.println();

        System.out.println("--- UPDATE: Оновлення коментаря ---");
        Comment foundComment = Comment.findById(1);
        if (foundComment != null) {
            System.out.println("Старий текст: " + foundComment.getContent());
            foundComment.setContent("Дуже корисна стаття! Дякую за детальне пояснення патернів проектування.");
            foundComment.save();
            System.out.println("Новий текст: " + foundComment.getContent());
        }
        System.out.println();

        System.out.println("--- READ: Демонстрація зв'язків між таблицями ---");
        Comment commentWithArticle = Comment.findById(2);
        if (commentWithArticle != null) {
            Article relatedArticle = commentWithArticle.getArticle();
            System.out.println("Коментар: " + commentWithArticle.getContent());
            System.out.println("Належить до статті: " + relatedArticle.getTitle());
        }
        System.out.println();

        System.out.println("--- DELETE: Видалення коментаря ---");
        Comment commentToDelete = Comment.findById(3);
        if (commentToDelete != null) {
            System.out.println("Видаляємо коментар ID=" + commentToDelete.getId());
            commentToDelete.delete();
        }
        System.out.println();

        System.out.println("--- DELETE: Видалення статті ---");
        Article articleToDelete = Article.findById(3);
        if (articleToDelete != null) {
            System.out.println("Видаляємо статтю: " + articleToDelete.getTitle());
            articleToDelete.delete();
        }
        System.out.println();

        System.out.println("--- READ: Список статей після видалення ---");
        allArticles = Article.findAll();
        System.out.println("Залишилось статей: " + allArticles.size());
        for (Article article : allArticles) {
            System.out.println("  - " + article.getTitle() + " (Автор: " + article.getAuthor() + ")");
        }
        System.out.println();

        DatabaseConnection.closeConnection();
        System.out.println("=== Демонстрація завершена ===");
    }
}
