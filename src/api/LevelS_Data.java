package api;

import api.LevelData;

import java.util.ArrayList;

public class LevelS_Data extends ArrayList<LevelData> {
    private int _curr = 0;

    public LevelS_Data() {
        this._curr = 0;
    }

    public int getCurr() {
        return this._curr;
    }

    public void setCurr(int c) {
        while(((LevelData)this.get(this._curr)).get_num() < c && this._curr < this.size() - 1) {
            ++this._curr;
        }

    }

    public int testNext(LevelData now) {
        LevelData curr = (LevelData) this.get(this._curr);
        if (curr.next(now.get_num(), now.get_max_move(), now.get_grade()) && this._curr < this.size() - 1) {
            ++this._curr;
        }

        return ((LevelData)this.get(this._curr)).get_num();
    }

    public static LevelS_Data getStages_game_Ex4() {
        LevelS_Data ans = new LevelS_Data();
        int[] st = new int[]{0, 1, 3, 5, 9, 11, 13, 16, 19, 20, 23};
        int[] gg = new int[]{125, 436, 713, 570, 480, 1050, 310, 235, 250, 200, 1000};
        int[] mm = new int[]{290, 580, 580, 500, 580, 580, 580, 290, 580, 290, 1140};

        for(int i = 0; i < st.length; ++i) {
            LevelData c = new LevelData(st[i], mm[i], gg[i]);
            ans.add(c);
        }

        return ans;
    }
}
