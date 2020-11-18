package game.logic;

import java.awt.*;
import java.io.Serializable;
import java.util.Stack;

/**
 * 五子棋游戏逻辑
 *
 * @author fanhuan
 * @date 2020/11/17
 */
public class Gobang {
    /**
     * 棋盘行数
     */
    public int board_row;
    /**
     * 棋盘列数
     */
    public int board_column;
    /**
     * 棋盘总格数
     */
    public int board_total;
    /**
     * 棋盘状态数据
     */
    public BoardState[][] board_state;
    /**
     * 当前落子方
     */
    public BoardState current;
    /**
     * 历史记录
     */
    public Stack<Step> history;
    /**
     * 是否产生赢家
     */
    public boolean hasWinner;

    /**
     * @return 能否悔棋
     */
    public boolean canRetract() {
        return !history.empty();
    } // canRetract

    /**
     * 重置
     */
    public void reset() {
        if (history.empty()) {
            return;
        }
        hasWinner = false;
        history.clear();
        for (int i = 0; i < board_row; i++) {
            for (int j = 0; j < board_column; j++) {
                board_state[i][j] = null;
            }
        }
        current = BoardState.BLACK;
    } //reset

    /**
     * 悔棋
     */
    public Step retract(Point down_point) {
        if (history.empty()) {
            return null;
        }
        hasWinner = false;
        Step step = history.pop();
        Point p = step.point;
        board_state[p.x][p.y] = null;
        if (!history.empty()) {
            down_point.setLocation(history.peek().point);
        }
        switchCurrent();
        return step;
    } //retract

    /**
     * 获取当前落子方
     */
    public BoardState getCurrent() {
        return current;
    }

    /**
     * 赢家判定
     */
    public boolean isWinner(int x, int y, Point winner_start_point, Point winner_end_point) {
        //除了落点以外还有4个棋子
        int count = 4;
        //始方向有几个同色棋子
        int sc = 0;
        //末方向有几个同色棋子
        int ec = 0;
        // 先往左，再往右
        for (sc = 0; !isOutside(x, y - sc - 1) && board_state[x][y - sc - 1] == current; sc++) {
        }
        for (ec = 0; ec < count - sc; ec++) {
            if (isOutside(x, y + ec + 1) || board_state[x][y + ec + 1] != current) {
                break;
            }
        }
        if (sc + ec == count) {
            winner_start_point.setLocation(x, y - sc);
            winner_end_point.setLocation(x, y + ec);
            return true;
        }

        // 先往上，再往下
        for (sc = 0; !isOutside(x - sc - 1, y) && board_state[x - sc - 1][y] == current; sc++) {
        }
        for (ec = 0; ec < count - sc; ec++) {
            if (isOutside(x + ec + 1, y) || board_state[x + ec + 1][y] != current) {
                break;
            }
        }
        if (sc + ec == count) {
            winner_start_point.setLocation(x - sc, y);
            winner_end_point.setLocation(x + ec, y);
            return true;
        }

        // 先往左上，再往右下
        for (sc = 0; !isOutside(x - sc - 1, y - sc - 1) && board_state[x - sc - 1][y - sc - 1] == current; sc++) {
        }
        for (ec = 0; ec < count - sc; ec++) {
            if (isOutside(x + ec + 1, y + ec + 1) || board_state[x + ec + 1][y + ec + 1] != current) {
                break;
            }
        }
        if (sc + ec == count) {
            winner_start_point.setLocation(x - sc, y - sc);
            winner_end_point.setLocation(x + ec, y + ec);
            return true;
        }

        // 先往左下，再往右上
        for (sc = 0; !isOutside(x + sc + 1, y - sc - 1) && board_state[x + sc + 1][y - sc - 1] == current; sc++) {
        }
        for (ec = 0; ec < count - sc; ec++) {
            if (isOutside(x - ec - 1, y + ec + 1) || board_state[x - ec - 1][y + ec + 1] != current) {
                break;
            }
        }
        if (sc + ec == count) {
            winner_start_point.setLocation(x + sc, y - sc);
            winner_end_point.setLocation(x - ec, y + ec);
            return true;
        }
        return false;
    } //isWinner

    /**
     * 是否出界
     */
    public boolean isOutside(int x, int y) {
        if (x < 0 || x >= board_row || y < 0 || y >= board_column) {
            return true;
        }
        return false;
    }

    /**
     * 切换落子方
     */
    public void switchCurrent() {
        switch (current) {
            case BLACK:
                current = BoardState.WHITE;
                break;

            case WHITE:
                current = BoardState.BLACK;
                break;
            default:
        }
    } //switchCurrent

    /**
     * 棋盘上的状态
     */
    public enum BoardState {
        /**
         * 黑棋, 白棋
         */
        BLACK, WHITE;

        public Color getColor() {
            Color c = null;
            switch (this) {
                case BLACK:
                    c = Color.BLACK;
                    break;

                case WHITE:
                    c = Color.WHITE;
                    break;
                default:
            }
            return c;
        } //getColor
    }

    /**
     * 步
     */
    public static class Step extends game.logic.Step implements Serializable {
        private static final long serialVersionUID = -314856283164590036L;
        /**
         * point 落点位置
         */
        public Point point;
        public BoardState boardState;

        public Step() {
        }

        public Step(Step s) {
            super(s);
            point = new Point(s.point);
            boardState = s.boardState;
        }
    }
} // Gobang
