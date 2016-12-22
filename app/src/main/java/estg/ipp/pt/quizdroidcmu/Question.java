package estg.ipp.pt.quizdroidcmu;

public class Question {

    private int id;
    private String text;
    private Difficulty difficulty;
    private String[] answers = new String[4];
    private int correctAnswer;
    private int reward;

    public Question(int id, String text, Difficulty difficulty, String[] answers, int correctAnswer, int reward) {
        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.reward = reward;
    }

    public Question(Question newQuestion){
        this.id = newQuestion.getId();
        this.text = newQuestion.getText();
        this.difficulty = newQuestion.getDifficulty();
        this.answers = newQuestion.getAnswers();
        this.correctAnswer = newQuestion.getCorrectAnswer();
        this.reward = newQuestion.getReward();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
