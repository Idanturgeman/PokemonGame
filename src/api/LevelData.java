package api;

public class LevelData {
    private int _num;
    private int _max_move;
    private int _grade;

    public LevelData(int n, int m, int g) {
        this.set_grade(g);
        this.set_max_move(m);
        this.set_num(n);
    }

    public boolean next(int n, int mm, int g) {
        boolean ans = false;
        if (n == this.get_num() && mm <= this.get_max_move() && g >= this.get_grade()) {
            ans = true;
        }

        return ans;
    }

    public int get_num() {
        return this._num;
    }

    public void set_num(int _num) {
        this._num = _num;
    }

    public int get_max_move() {
        return this._max_move;
    }

    public void set_max_move(int _max_move) {
        this._max_move = _max_move;
    }

    public int get_grade() {
        return this._grade;
    }

    public void set_grade(int _grade) {
        this._grade = _grade;
    }
}
