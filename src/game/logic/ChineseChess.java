package game.logic;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Stack;

/**
 * 中国象棋游戏逻辑
 *
 * @author fanhuan
 * @date 2020/11/14
 */
public class ChineseChess {
    /**
     * 棋盘行数
     */
    public static final int BOARD_ROW;
    /**
     * 棋盘列数
     */
    public static final int BOARD_COLUMN;
    /**
     * 炮位置
     */
    public static final int[] BOARD_CANNON;
    /**
     * 兵位置行
     */
    public static final int[] BOARD_PAWN_ROW;
    /**
     * 兵位置列
     */
    public static final int[] BOARD_PAWN_COLUMN;
    /**
     * 中文数字
     */
    public static final String[] R_NUM = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    public static final String[] B_NUM = new String[]{" 0 ", " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 "};
    /**
     * 棋盘状态数据
     */
    public BoardState[][] board_state;
    /**
     * 当前落子方
     */
    public Color current;
    /**
     * 主场, 表示处于棋盘下方的棋子颜色
     * 若红色为主场, 则红色棋子初始化时在棋盘下方
     * 若黑色为主场, 则黑色棋子初始化时在棋盘下方
     */
    public Color main;
    /**
     * 历史记录
     */
    public Stack<Step> history;
    /**
     * 是否产生赢家
     */
    public boolean hasWinner;

    static {
        BOARD_ROW = 10;
        BOARD_COLUMN = 9;
        BOARD_CANNON = new int[]{2, 7, 1, 7};
        BOARD_PAWN_ROW = new int[]{3, 6};
        BOARD_PAWN_COLUMN = new int[]{2, 4, 6};
    }

    public ChineseChess() {
        board_state = new BoardState[BOARD_ROW][BOARD_COLUMN];
        history = new Stack<>();
//        initBoardState();
    }

    /**
     * 重置
     */
    public void reset(Point selectPoint, Point select2Point) {
        if (history.empty()) {
            return;
        }
        hasWinner = false;
        history.clear();
        initBoardState();
        selectPoint.setLocation(0, 0);
        select2Point.setLocation(BOARD_ROW - 1, BOARD_COLUMN - 1);
        current = Color.RED;
    } //reset

    /**
     * 悔棋
     */
    public Step retract(Point selectPoint) {
        if (history.empty()) {
            return null;
        }
        hasWinner = false;
        Step stp = history.pop();
        board_state[stp.to.x][stp.to.y] = stp.before;
        board_state[stp.from.x][stp.from.y] = stp.after;
        switchCurrent();
        if (history.empty()) {
            stp = null;
        } else {
            stp = history.peek();
            selectPoint.setLocation(stp.to.x, stp.to.y);
        }
        return stp;
    } //retract

    /**
     * 初始化棋盘状态
     */
    public void initBoardState() {
        current = Color.RED;
        int m = main == Color.RED ? 1 : 0;
        int n = 1 - m;
        // 清除棋盘
        for (int i = 0; i < BOARD_ROW; i++) {
            for (int j = 0; j < BOARD_COLUMN; j++) {
                board_state[i][j] = null;
            }
        }
        // 放置兵
        for (int i = 0; i < BOARD_COLUMN; i += 2) {
            board_state[BOARD_PAWN_ROW[n]][i] = BoardState.B_P;
            board_state[BOARD_PAWN_ROW[m]][i] = BoardState.R_P;
        }
        // 放置炮
        board_state[BOARD_CANNON[n]][BOARD_CANNON[2]] = BoardState.B_C;
        board_state[BOARD_CANNON[n]][BOARD_CANNON[3]] = BoardState.B_C;
        board_state[BOARD_CANNON[m]][BOARD_CANNON[2]] = BoardState.R_C;
        board_state[BOARD_CANNON[m]][BOARD_CANNON[3]] = BoardState.R_C;
        // 放置其他棋子
        m *= 9;
        n = 9 - m;
        board_state[n][0] = BoardState.B_R;
        board_state[n][1] = BoardState.B_N;
        board_state[n][2] = BoardState.B_B;
        board_state[n][3] = BoardState.B_Q;
        board_state[n][4] = BoardState.B_K;
        board_state[n][5] = BoardState.B_Q;
        board_state[n][6] = BoardState.B_B;
        board_state[n][7] = BoardState.B_N;
        board_state[n][8] = BoardState.B_R;
        board_state[m][0] = BoardState.R_R;
        board_state[m][1] = BoardState.R_N;
        board_state[m][2] = BoardState.R_B;
        board_state[m][3] = BoardState.R_Q;
        board_state[m][4] = BoardState.R_K;
        board_state[m][5] = BoardState.R_Q;
        board_state[m][6] = BoardState.R_B;
        board_state[m][7] = BoardState.R_N;
        board_state[m][8] = BoardState.R_R;
    } //initBoardState

    /**
     * 判断到达位是否合规
     */
    public boolean isCorrect(Point selectPoint, int x, int y) {
        boolean b;
        switch (board_state[selectPoint.x][selectPoint.y]) {
            case R_P:
            case B_P:
                // 兵卒
                b = x == selectPoint.x - 1 && y == selectPoint.y;
                if (x < 5) {
                    b |= x == selectPoint.x && 1 == Math.abs(y - selectPoint.y);
                }
                return b;
            case R_C:
            case B_C:
                // 砲炮
                int c = -1;
                if (selectPoint.x == x) {
                    c = 0;
                    b = selectPoint.y < y;
                    for (int i = 1 + (b ? selectPoint.y : y); i < (b ? y : selectPoint.y); i++) {
                        if (board_state[x][i] != null) {
                            c++;
                        }
                    }
                } else if (selectPoint.y == y) {
                    c = 0;
                    b = selectPoint.x < x;
                    for (int i = 1 + (b ? selectPoint.x : x); i < (b ? x : selectPoint.x); i++) {
                        if (board_state[i][y] != null) {
                            c++;
                        }
                    }
                }
                if (board_state[x][y] == null) {
                    return c == 0;
                } else {
                    return c == 1;
                }
            case R_K:
            case B_K:
                // 帅将
                if (6 < x && x < 10 && 2 < y && y < 6) {
                    return 1 == (Math.abs(selectPoint.x - x) + Math.abs(selectPoint.y - y));
                } else if (board_state[x][y] == BoardState.B_K || board_state[x][y] == BoardState.R_K && selectPoint.y == y) {
                    for (int i = 1 + x; i < selectPoint.x; i++) {
                        if (board_state[i][y] != null) {
                            return false;
                        }
                    }
                    return true;
                }
                break;
            case R_Q:
            case B_Q:
                // 仕士
                if (6 < x && x < 10 && 2 < y && y < 6) {
                    return 1 == Math.abs(selectPoint.x - x) && 1 == Math.abs(selectPoint.y - y);
                }
                break;
            case R_B:
            case B_B:
                // 相象
                if (x > 4) {
                    if (2 == Math.abs(selectPoint.x - x) && 2 == Math.abs(selectPoint.y - y)) {
                        return board_state[(selectPoint.x + x) / 2][(selectPoint.y + y) / 2] == null;
                    }
                }
                break;
            case R_N:
            case B_N:
                // 马
                if (Math.abs(selectPoint.x - x) == 1 && Math.abs(selectPoint.y - y) == 2) {
                    if (y > selectPoint.y) {
                        y--;
                    } else {
                        y++;
                    }
                    return board_state[selectPoint.x][y] == null;
                } else if (Math.abs(selectPoint.x - x) == 2 && Math.abs(selectPoint.y - y) == 1) {
                    if (x > selectPoint.x) {
                        x--;
                    } else {
                        x++;
                    }
                    return board_state[x][selectPoint.y] == null;
                }
                break;
            case R_R:
            case B_R:
                // 车
                if (selectPoint.x == x) {
                    b = selectPoint.y < y;
                    for (int i = 1 + (b ? selectPoint.y : y); i < (b ? y : selectPoint.y); i++) {
                        if (board_state[x][i] != null) {
                            return false;
                        }
                    }
                    return true;
                } else if (selectPoint.y == y) {
                    b = selectPoint.x < x;
                    for (int i = 1 + (b ? selectPoint.x : x); i < (b ? x : selectPoint.x); i++) {
                        if (board_state[i][y] != null) {
                            return false;
                        }
                    }
                    return true;
                }
                break;
            default:
        }
        return false;
    } //isCorrect

    /**
     * 获取棋谱步骤名
     */
    public String getStepName(Point selectPoint, int x, int y) {
        StringBuilder stepName = new StringBuilder();
        int samePieceRow = isSamePieceInSameColumn(selectPoint);
        BoardState bs = board_state[selectPoint.x][selectPoint.y];
        switch (bs) {
            case R_P:
            case B_P:
                // 兵卒
                if (samePieceRow < 0) {
                    stepName.append(board_state[selectPoint.x][selectPoint.y].nickName());
                    stepName.append(getStrNum(bs, BOARD_COLUMN - selectPoint.y));
                } else {
                    int[] pawns = getSameColumnPawnCount(selectPoint.x, selectPoint.y);
                    switch (pawns[1]) {
                        case 2:
                        case 3:
                            if (pawns[0] == 0) {
                                stepName.append("前");
                            } else if (pawns[0] == 2) {
                                stepName.append("后");
                            } else if (pawns[1] == 2) {
                                stepName.append("后");
                            } else {
                                stepName.append("中");
                            }
                            break;
                        case 4:
                        case 5:
                            stepName.append(getStrNum(bs, pawns[0] + 1));
                            break;
                        default:
                    }
                    stepName.append(board_state[selectPoint.x][selectPoint.y].nickName());
                }
                if (x == selectPoint.x) {
                    stepName.append("平").append(getStrNum(bs, BOARD_COLUMN - y));
                } else {
                    stepName.append("进").append(getStrNum(bs, 1));
                }
                break;
            case R_K:
            case B_K:
                // 帅将
                stepName.append(board_state[selectPoint.x][selectPoint.y].nickName());
                stepName.append(getStrNum(bs, BOARD_COLUMN - selectPoint.y));
                appendLast(selectPoint, stepName, x, y, bs);
                break;
            case R_Q:
            case B_Q:
                // 仕士
            case R_B:
            case B_B:
                // 相象
            case R_N:
            case B_N:
                // 馬马
            case R_C:
            case B_C:
                // 砲炮
            case R_R:
            case B_R:
                // 車车
                appendDoublePieceStepName(samePieceRow, selectPoint, stepName, x, y, bs);
                break;
            default:
        }
        return stepName.toString();
    } //getStepName

    /**
     * 获取指定颜色的数字
     *
     * @param bs    状态颜色
     * @param which 数字
     * @return 对应数字
     */
    private String getStrNum(BoardState bs, int which) {
        if (bs.color() == Color.RED) {
            return R_NUM[which];
        } else {
            return B_NUM[which];
        }
    } // getStrNum

    /**
     * 同列是否有同子
     */
    private int isSamePieceInSameColumn(Point selectPoint) {
        for (int i = 0; i < BOARD_ROW; i++) {
            if (i == selectPoint.x) {
                continue;
            }
            if (board_state[i][selectPoint.y] == board_state[selectPoint.x][selectPoint.y]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 所有列中同列的兵数，同列中只有一个兵的除外
     * 返回长度为2的数组
     * [0] 表示在总个数中的定位
     * [1] 表示总个数
     */
    private int[] getSameColumnPawnCount(int x, int y) {
        int[] pawns = new int[2];
        // 同一列中的兵数
        int cInCol;
        for (int j = BOARD_COLUMN - 1; j >= 0; j--) {
            cInCol = 0;
            for (int i = 0; i < BOARD_ROW; i++) {
                if (board_state[i][j] == board_state[x][y]) {
                    if (i == x && j == y) {
                        pawns[0] = pawns[1];
                    }
                    pawns[1]++;
                    cInCol++;
                }
            }
            if (cInCol == 1) {
                pawns[1]--;
            }
        }
        return pawns;
    }

    private void appendDoublePieceStepName(int samePieceRow, Point selectPoint, StringBuilder stepName, int x, int y, BoardState bs) {
        if (samePieceRow < 0) {
            stepName.append(board_state[selectPoint.x][selectPoint.y].nickName());
            stepName.append(getStrNum(bs, BOARD_COLUMN - selectPoint.y));
        } else {
            if (selectPoint.x > samePieceRow) {
                stepName.append("后");
            } else {
                stepName.append("前");
            }
            stepName.append(board_state[selectPoint.x][selectPoint.y].nickName());
        }
        appendLast(selectPoint, stepName, x, y, bs);
    } // appendDoublePieceStepName

    private void appendLast(Point selectPoint, StringBuilder stepName, int x, int y, BoardState bs) {
        if (x == selectPoint.x) {
            stepName.append("平").append(getStrNum(bs, BOARD_COLUMN - y));
        } else {
            if (x < selectPoint.x) {
                stepName.append("进");
            } else {
                stepName.append("退");
            }
            int ind;
            switch (bs) {
                case R_Q:
                case B_Q:
                    // 仕士
                case R_B:
                case B_B:
                    // 相象
                case R_N:
                case B_N:
                    // 馬马
                    ind = BOARD_COLUMN - y;
                    break;
                case R_C:
                case B_C:
                    // 砲炮
                case R_R:
                case B_R:
                    // 車车
                case R_K:
                case B_K:
                    // 帅将
                    ind = Math.abs(x - selectPoint.x);
                    break;
                default:
                    ind = 0;
            }
            stepName.append(getStrNum(bs, ind));
        }
    } // append

    /**
     * 切换落子方
     */
    public void switchCurrent() {
        if (Color.BLACK == current) {
            current = Color.RED;
        } else if (Color.RED == current) {
            current = Color.BLACK;
        }
    } //switchCurrent

    /**
     * 棋盘状态
     */
    public enum BoardState {
        /**
         * 红: 兵,砲,帅,仕,相,馬,車
         * 黑: 卒,炮,将,士,象,马,车
         */
        R_P, R_C, R_K, R_Q, R_B, R_N, R_R,
        B_P, B_C, B_K, B_Q, B_B, B_N, B_R;

        public static final String[] NICK_NAMES = new String[]{
                "兵", "砲", "帅", "仕", "相", "馬", "車",
                "卒", "炮", "将", "士", "象", "马", "车"};

        public Color color() {
            if (ordinal() < values().length / 2) {
                return Color.RED;
            }
            return Color.BLACK;
        }

        public String nickName() {
            return NICK_NAMES[ordinal()];
        }
    } //BoardState

    /**
     * 步
     */
    public static class Step extends game.logic.Step implements Serializable {
        private static final long serialVersionUID = 3245530199905468782L;
        /**
         * from 起始位置
         * to   目的位置
         */
        public Point from, to;
        /**
         * after  目的位置初始状态
         * before 目的位置最终状态
         */
        public BoardState after, before;
        /**
         * stepName 棋谱步名
         */
        public String stepName;

        public Step() {
        }

        public Step(Step s) {
            super(s);
            from = new Point(s.from);
            to = new Point(s.to);
            after = s.after;
            before = s.before;
            stepName = s.stepName;
        }
    }
} // ChineseChess
