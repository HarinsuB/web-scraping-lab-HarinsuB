public class NewsArticle {
    private String headline;
    private String publicationDate;
    private String author;

    public NewsArticle(String headline, String publicationDate, String author) {
        this.headline = headline;
        this.publicationDate = publicationDate;
        this.author = author;
    }

    public String getHeadline() {
        return headline;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString(){
        return "Headline: " +headline + "\n" +
                "Date: " +publicationDate + "\n" +
                "Author: " +author + "\n";
    }
}
