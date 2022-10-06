package windows.panel;

import game.logic.ChineseChess;
import game.logic.ChineseChess.BoardState;
import game.logic.ChineseChess.Step;
import windows.ChineseChessFrame;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author paso
 * @since 2020/11/14
 */
public class ChineseChessPanel extends DoubleBufferingPanel implements MouseListener, ComponentListener {
    /**
     * 中国象棋游戏逻辑
     */
    public ChineseChess chineseChess;
    /**
     * 棋盘左端x值
     */
    int board_left_x;
    /**
     * 棋盘右端x值
     */
    int board_right_x;
    /**
     * 棋盘顶端y值
     */
    int board_top_y;
    /**
     * 棋盘底端y值
     */
    int board_bottom_y;
    /**
     * 棋盘中底端y值
     */
    int board_center_bottom_y;
    /**
     * 棋盘中顶端y值
     */
    int board_center_top_y;
    /**
     * 棋盘线间隔
     */
    int board_spacing;
    /**
     * 棋盘斜线行
     */
    int[] board_oblique_row;
    /**
     * 棋盘斜线列
     */
    int[] board_oblique_column;
    /**
     * 棋盘斜线坐标
     */
    int[] board_oblique_pts;
    /**
     * 炮兵特殊位置间隔
     */
    int board_special_spacing;
    /**
     * 棋子半径
     */
    int piece_radius;
    /**
     * 棋子字体
     */
    Font piece_text_font;
    /**
     * 棋子线条描边
     */
    Stroke piece_stroke;

    /**
     * 是否计算过尺寸
     */
    boolean calculated;

    /**
     * 点击监听
     */
    OnClickListener onClickListener;
    /**
     * 落子监听
     */
    OnPieceDownListener onPieceDownListener;
    /**
     * 选中点
     */
    Point select_point;
    /**
     * 另一名玩家的选中点
     */
    Point select2_point;
    /**
     * 选中点描边
     */
    Stroke select_stroke;
    /**
     * 赢家监听
     */
    OnWinnerListener onWinnerListener;

    /**
     * 固定落子方
     */
    Color player;

    ChineseChessFrame frame;

    public ChineseChessPanel(ChineseChessFrame frame) {
        this.frame = frame;
        chineseChess = new ChineseChess();
        defInit();
        addMouseListener(this);
        addComponentListener(this);
    }

    /**
     * @return 能否悔棋
     */
    public boolean canRetract() {
        return !chineseChess.history.empty();
    } // canRetract

    /**
     * 重置
     */
    public void reset() {
        chineseChess.reset(select_point, select2_point);
        repaint();
    } // reset

    /**
     * 悔棋
     */
    public Step retract() {
        Point point = chineseChess.current == player ? select_point : select2_point;
        Step step = chineseChess.retract(point);
        repaint();
        return step;
    } // retract

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnPieceDownListener(OnPieceDownListener listener) {
        onPieceDownListener = listener;
    }

    public void setOnWinnerListener(OnWinnerListener listener) {
        onWinnerListener = listener;
    }

    public void setPlayer(Color color) {
        player = color;
        chineseChess.main = color;
        chineseChess.initBoardState();
    } // setPlayer

    public Color getPlayer() {
        return player;
    }

    private void defInit() {
        board_oblique_row = new int[]{0, 2, 7, 9};
        board_oblique_column = new int[]{3, 5};

        piece_stroke = new BasicStroke(2);

        select_point = new Point();
        select2_point = new Point(ChineseChess.BOARD_ROW - 1, ChineseChess.BOARD_COLUMN - 1);

        select_stroke = new BasicStroke(1);
    } // defInit

    /**
     * 计算尺寸数据
     */
    private void calculate() {
        if (calculated) {
            return;
        }
        int w = getWidth();
        int h = getHeight();
        if (w < h) {
            // 四周各留一个间隔
            board_spacing = w / (ChineseChess.BOARD_COLUMN + 1);
            board_left_x = board_spacing;
            board_top_y = (h - (ChineseChess.BOARD_ROW - 1) * board_spacing) / 2;
        } else {
            // 四周各留一个间隔
            board_spacing = h / (ChineseChess.BOARD_ROW + 1);
            board_top_y = board_spacing;
            board_left_x = (w - (ChineseChess.BOARD_COLUMN - 1) * board_spacing) / 2;
        }
        board_right_x = board_left_x + (ChineseChess.BOARD_COLUMN - 1) * board_spacing;
        board_bottom_y = board_top_y + (ChineseChess.BOARD_ROW - 1) * board_spacing;
        board_center_bottom_y = board_top_y + (ChineseChess.BOARD_ROW / 2 - 1) * board_spacing;
        board_center_top_y = board_center_bottom_y + board_spacing;

        // 半径为间隔一半，再乘以0.85
        piece_radius = board_spacing * 85 / 200;
        board_special_spacing = board_spacing / 8;

        piece_text_font = new Font(null, Font.BOLD, piece_radius);

        board_oblique_pts = new int[16];
        for (int i = 0; i < board_oblique_pts.length; i++) {
            if (i % 2 == 0) {
                board_oblique_pts[i] = board_left_x;
            } else {
                board_oblique_pts[i] = board_top_y;
            }
        }
        board_oblique_pts[0] += board_oblique_column[0] * board_spacing;
        board_oblique_pts[1] += board_oblique_row[0] * board_spacing;
        board_oblique_pts[2] += board_oblique_column[1] * board_spacing;
        board_oblique_pts[3] += board_oblique_row[1] * board_spacing;

        board_oblique_pts[4] += board_oblique_column[1] * board_spacing;
        board_oblique_pts[5] += board_oblique_row[0] * board_spacing;
        board_oblique_pts[6] += board_oblique_column[0] * board_spacing;
        board_oblique_pts[7] += board_oblique_row[1] * board_spacing;

        board_oblique_pts[8] += board_oblique_column[0] * board_spacing;
        board_oblique_pts[9] += board_oblique_row[2] * board_spacing;
        board_oblique_pts[10] += board_oblique_column[1] * board_spacing;
        board_oblique_pts[11] += board_oblique_row[3] * board_spacing;

        board_oblique_pts[12] += board_oblique_column[1] * board_spacing;
        board_oblique_pts[13] += board_oblique_row[2] * board_spacing;
        board_oblique_pts[14] += board_oblique_column[0] * board_spacing;
        board_oblique_pts[15] += board_oblique_row[3] * board_spacing;

        calculated = true;
    } // calculate

    /**
     * 绘制棋盘
     */
    private void drawBoard(Graphics2D g2d) {
        // 绘制背景色

        // 绘制横线
        for (int i = 0; i < ChineseChess.BOARD_ROW; i++) {
            g2d.drawLine(board_left_x, board_top_y + i * board_spacing,
                    board_right_x, board_top_y + i * board_spacing);
        }

        // 绘制竖线
        for (int i = 0; i < ChineseChess.BOARD_COLUMN; i++) {
            if (i == 0 || i == ChineseChess.BOARD_COLUMN - 1) {
                g2d.drawLine(board_left_x + i * board_spacing, board_top_y,
                        board_left_x + i * board_spacing, board_bottom_y);
            } else {
                g2d.drawLine(board_left_x + i * board_spacing, board_top_y,
                        board_left_x + i * board_spacing, board_center_bottom_y);
                g2d.drawLine(board_left_x + i * board_spacing, board_center_top_y,
                        board_left_x + i * board_spacing, board_bottom_y);
            }
        }

        // 绘制斜线
        drawLines(g2d, board_oblique_pts);

        // 绘制炮位置
        drawSpecial(g2d, board_left_x + ChineseChess.BOARD_CANNON[2] * board_spacing, board_top_y + ChineseChess.BOARD_CANNON[0] * board_spacing);
        drawSpecial(g2d, board_left_x + ChineseChess.BOARD_CANNON[3] * board_spacing, board_top_y + ChineseChess.BOARD_CANNON[0] * board_spacing);
        drawSpecial(g2d, board_left_x + ChineseChess.BOARD_CANNON[2] * board_spacing, board_top_y + ChineseChess.BOARD_CANNON[1] * board_spacing);
        drawSpecial(g2d, board_left_x + ChineseChess.BOARD_CANNON[3] * board_spacing, board_top_y + ChineseChess.BOARD_CANNON[1] * board_spacing);

        // 绘制左兵位置
        drawSpecialRight(g2d, board_left_x, board_top_y + ChineseChess.BOARD_PAWN_ROW[0] * board_spacing);
        drawSpecialRight(g2d, board_left_x, board_top_y + ChineseChess.BOARD_PAWN_ROW[1] * board_spacing);
        // 绘制右兵位置
        drawSpecialLeft(g2d, board_right_x, board_top_y + ChineseChess.BOARD_PAWN_ROW[0] * board_spacing);
        drawSpecialLeft(g2d, board_right_x, board_top_y + ChineseChess.BOARD_PAWN_ROW[1] * board_spacing);
        // 绘制其余兵位置
        for (int i = 0; i < ChineseChess.BOARD_PAWN_ROW.length; i++) {
            for (int j = 0; j < ChineseChess.BOARD_PAWN_COLUMN.length; j++) {
                drawSpecial(g2d, board_left_x + ChineseChess.BOARD_PAWN_COLUMN[j] * board_spacing, board_top_y + ChineseChess.BOARD_PAWN_ROW[i] * board_spacing);
            }
        }
    } // drawBoard

    /**
     * 绘制多条线段
     *
     * @param pts 每个点的横纵坐标, 长度必须为4的倍数
     */
    public void drawLines(Graphics2D g2d, int[] pts) {
        for (int i = 0; i < pts.length; i += 4) {
            g2d.drawLine(pts[i], pts[i + 1], pts[i + 2], pts[i + 3]);
        }
    } // drawLines

    /**
     * 画炮兵特殊位置
     */
    private void drawSpecial(Graphics2D g2d, int x, int y) {
        drawSpecialLeft(g2d, x, y);
        drawSpecialRight(g2d, x, y);
    } // drawSpecial

    /**
     * 画炮兵特殊位置左半部分
     */
    private void drawSpecialLeft(Graphics2D g2d, int x, int y) {
        int z;
        x -= board_special_spacing;
        y -= board_special_spacing;
        z = y - board_special_spacing;
        g2d.drawLine(x, z, x, y);
        z = x - board_special_spacing;
        g2d.drawLine(z, y, x, y);

        y += 2 * board_special_spacing;
        g2d.drawLine(z, y, x, y);
        z = y + board_special_spacing;
        g2d.drawLine(x, z, x, y);
    } // drawSpecialLeft

    /**
     * 画炮兵特殊位置右半部分
     */
    private void drawSpecialRight(Graphics2D g2d, int x, int y) {
        int z;
        x += board_special_spacing;
        y -= board_special_spacing;
        z = y - board_special_spacing;
        g2d.drawLine(x, z, x, y);
        z = x + board_special_spacing;
        g2d.drawLine(z, y, x, y);

        y += 2 * board_special_spacing;
        g2d.drawLine(z, y, x, y);
        z = y + board_special_spacing;
        g2d.drawLine(x, z, x, y);
    } // drawSpecialRight

    /**
     * 绘制棋子
     */
    public void drawPiece(Graphics2D g2d) {
        int x, y;
        BoardState bs;
        for (int i = 0; i < ChineseChess.BOARD_ROW; i++) {
            for (int j = 0; j < ChineseChess.BOARD_COLUMN; j++) {
                bs = chineseChess.board_state[i][j];
                if (bs != null) {
                    x = board_left_x + j * board_spacing;
                    y = board_top_y + i * board_spacing;
                    // 绘制棋子背景色
                    g2d.setColor(Color.LIGHT_GRAY);
                    fillCircle(g2d, x, y, piece_radius);
                    // 绘制棋子
                    g2d.setColor(bs.color());
                    g2d.setStroke(piece_stroke);
                    drawCircle(g2d, x, y, piece_radius * 4 / 5);
                    x -= piece_text_font.getSize() / 2;
                    y += piece_text_font.getSize() * 2 / 5;
                    g2d.setFont(piece_text_font);
                    g2d.drawString(bs.nickName(), x, y);
                }
            }
        }
    } // drawPiece

    private void drawCircle(Graphics2D g2d, int x, int y, int r) {
        int l = 2 * r;
        g2d.drawOval(x - r, y - r, l, l);
    } // drawCircle

    private void fillCircle(Graphics2D g2d, int x, int y, int r) {
        int l = 2 * r;
        g2d.fillOval(x - r, y - r, l, l);
    } // fillCircle

    /**
     * 绘制选中位置
     */
    private void drawSelect(Graphics2D g2d, Point point) {
        if (isOutside(point.x, point.y)) {
            return;
        }
        int x, y, z;
        x = board_left_x + point.y * board_spacing;
        y = board_top_y + point.x * board_spacing;
        x -= piece_radius;
        y -= piece_radius;
        z = y + board_special_spacing;
        g2d.drawLine(x, z, x, y);
        z = x + board_special_spacing;
        g2d.drawLine(z, y, x, y);

        y += 2 * piece_radius;
        g2d.drawLine(z, y, x, y);
        z = y - board_special_spacing;
        g2d.drawLine(x, z, x, y);

        x += 2 * piece_radius;
        g2d.drawLine(x, z, x, y);
        z = x - board_special_spacing;
        g2d.drawLine(z, y, x, y);

        y -= 2 * piece_radius;
        g2d.drawLine(z, y, x, y);
        z = y + board_special_spacing;
        g2d.drawLine(x, z, x, y);
    } // drawSelect

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 创建 Graphics 的副本, 需要改变 Graphics 的参数,
        // 这里必须使用副本, 避免影响到 Graphics 原有的设置
        Graphics2D g2d = (Graphics2D) g.create();
        calculate();
        drawBoard(g2d);
        drawPiece(g2d);
        g2d.setStroke(select_stroke);
        g2d.setColor(player);
        drawSelect(g2d, select_point);
        if (select_point.equals(select2_point)) {
            g2d.setColor(Color.BLUE);
        } else {
            g2d.setColor(player == Color.RED ? Color.BLACK : Color.RED);
        }
        drawSelect(g2d, select2_point);
        // 自己创建的副本用完要销毁掉
        g2d.dispose();
    }

    /**
     * 触点坐标差转为行列
     *
     * @param offset 坐标差
     */
    private int point2Row(int offset) {
        int r;
        int s = offset / board_spacing;
        int y = offset % board_spacing;
        if (y <= board_spacing / 2) {
            r = s;
        } else {
            r = s + 1;
        }
        return r;
    } // point2Row

    /**
     * 是否出界
     *
     * @param x x坐标
     * @param y y坐标
     */
    private boolean isOutside(int x, int y) {
        if (x < 0 || x >= ChineseChess.BOARD_ROW || y < 0 || y >= ChineseChess.BOARD_COLUMN) {
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // 发起请求时无法落子
        if (frame.isRequesting) {
            return;
        }
        // 已诞生赢家的情况下不能再落子
        if (chineseChess.hasWinner) {
            return;
        }
        int x = event.getX(), y = event.getY();
        int tx, ty;
        // 横坐标转为列数
        ty = point2Row(x - board_left_x);
        // 纵坐标转为行数
        tx = point2Row(y - board_top_y);
        // 出界判断
        if (isOutside(tx, ty)) {
            return;
        }
        // 多次点击同一个位置
        if (select_point.x == tx && select_point.y == ty) {
            return;
        }
        BoardState selBS = chineseChess.board_state[select_point.x][select_point.y];
        Step msg = null;
        // 之前选中的不为空
        if (selBS != null) {
            // 回合轮流限制
            if (selBS.color() == chineseChess.current && player == chineseChess.current) {
                // 到达位要么为空，要么为对方棋子
                if (chineseChess.board_state[tx][ty] == null || chineseChess.board_state[tx][ty].color() != chineseChess.current) {
                    // 到达位符合规则
                    if (chineseChess.isCorrect(select_point, tx, ty)) {
                        // 建立棋谱步骤
                        Step stp = new Step();
                        stp.from = new Point(select_point);
                        stp.to = new Point(tx, ty);
                        stp.before = chineseChess.board_state[tx][ty];
                        stp.after = chineseChess.board_state[select_point.x][select_point.y];
                        stp.stepName = chineseChess.getStepName(select_point, tx, ty);
                        msg = new Step(stp);
                        // 更新棋盘状态
                        updateFrom(stp, false);
                    }
                }
            }
        }
        // 设置选中位置
        select_point.setLocation(tx, ty);
        if (onClickListener != null) {
            if (msg == null) {
                msg = new Step();
                msg.to = new Point(select_point);
            }
            onClickListener.onClick(msg);
        }
        // 重绘画布
        repaint();
    }

    /**
     * 更新步骤
     *
     * @param stp       步骤
     * @param fromOther 来自另一名玩家
     */
    public void updateFrom(Step stp, boolean fromOther) {
        if (fromOther) {
            swapDirection(stp);
        }
        if (stp.stepName != null && !stp.stepName.isEmpty()) {
            // 更新棋盘状态
            chineseChess.board_state[stp.to.x][stp.to.y] = stp.after;
            chineseChess.board_state[stp.from.x][stp.from.y] = null;
            // 入栈
            chineseChess.history.push(stp);
            // 胜利判定
            if (stp.before == BoardState.R_K || stp.before == BoardState.B_K) {
                chineseChess.hasWinner = true;
                if (onWinnerListener != null) {
                    onWinnerListener.onWinner(chineseChess.current);
                }
            }
            if (onPieceDownListener != null) {
                onPieceDownListener.onPieceDown(stp, chineseChess.current);
            }
            // 切换落子方
            chineseChess.switchCurrent();
            if (onPieceDownListener != null) {
                onPieceDownListener.afterPieceDown(chineseChess.current);
            }
        }
        // 只更新光标
        if (fromOther) {
            select2_point.setLocation(stp.to);
            repaint();
        }
    } // updateFrom

    /**
     * 交换方向
     *
     * @param p 点
     */
    private void swapDirection(Point p) {
        if (p == null) {
            return;
        }
        p.setLocation(ChineseChess.BOARD_ROW - p.x - 1, ChineseChess.BOARD_COLUMN - p.y - 1);
    } // swapDirection

    /**
     * 交换方向
     *
     * @param s 步
     */
    private void swapDirection(Step s) {
        if (s == null) {
            return;
        }
        swapDirection(s.from);
        swapDirection(s.to);
    } // swapDirection

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void componentResized(ComponentEvent e) {
        calculated = false;
        clearCache();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    /**
     * 点击监听
     */
    public interface OnClickListener {
        void onClick(Step msg);
    } // OnClickListener

    /**
     * 落子监听器
     */
    public interface OnPieceDownListener {
        /**
         * @param s   落子位置
         * @param cur 落子颜色
         */
        void onPieceDown(Step s, Color cur);

        /**
         * @param cur 下次要落子的颜色
         */
        void afterPieceDown(Color cur);
    }

    /**
     * 赢家监听器
     */
    public interface OnWinnerListener {
        /**
         * 产生赢家
         *
         * @param cur 赢家
         */
        void onWinner(Color cur);
    }
} // ChineseChessPanel
