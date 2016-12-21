package estg.ipp.pt.quizdroidcmu;

/**
 * Created by Tiago Fernandes on 20/12/2016.
 */

public class Difficulty {

    private int id;
    private int name;
    private int description;

    public Difficulty(int id, int name, int description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }
}