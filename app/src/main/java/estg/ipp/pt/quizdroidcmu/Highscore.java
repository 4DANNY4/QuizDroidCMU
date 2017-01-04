package estg.ipp.pt.quizdroidcmu;

public class Highscore {

    private int id;
    private String playerName;
    private Difficulty difficulty;
    private int correctAnswers;
    private int score;
    private boolean unlimited;

    public Highscore(int id, String playerName, Difficulty difficulty, int correctAnswers, int score, boolean unlimited) {
        this.id = id;
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.unlimited = unlimited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) { this.score = score;}

    public boolean isUnlimited() {
        return unlimited;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }
}
