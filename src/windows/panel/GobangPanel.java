package windows.panel;

import game.logic.Gobang;
import game.logic.Gobang.Step;
import windows.GobangFrame;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Stack;

/**
 * @author paso
 * @since 2020/11/17
 */
public class GobangPanel extends DoubleBufferingPanel implements MouseListener, ComponentListener {
    /**
     * 五子棋游戏逻辑
     */
    public Gobang gobang;
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
     * 棋盘线间隔
     */
    int board_spacing;
    /**
     * 棋子半径
     */
    int piece_radius;
    /**
     * 棋盘线厚度
     */
    Stroke board_stroke;
    /**
     * 选中落子位置间隔
     */
    int board_special_spacing;
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
     * 落子点
     */
    Point down_point;
    /**
     * 赢家棋子始末
     */
    Point winner_start_point, winner_end_point;
    /**
     * 赢家连线厚度
     */
    Stroke winner_line_stroke;
    /**
     * 赢家监听
     */
    OnWinnerListener onWinnerListener;
    /**
     * 固定玩家
     */
    public Gobang.BoardState player;

    GobangFrame frame;

    public GobangPanel(GobangFrame frame) {
        this.frame = frame;
        gobang = new Gobang();
        defInit();
        addMouseListener(this);
        addComponentListener(this);
    }

    /**
     * 重置
     */
    public void reset() {
        gobang.reset();
        repaint();
    } // reset

    /**
     * 悔棋
     */
    public Step retract() {
        Step step = gobang.retract(down_point);
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

    private void defInit() {
        gobang.board_row = 15;
        gobang.board_column = 15;
        gobang.board_total = gobang.board_row * gobang.board_column;
        gobang.board_state = new Gobang.BoardState[gobang.board_row][gobang.board_column];

        board_stroke = new BasicStroke();

        gobang.current = Gobang.BoardState.BLACK;
        down_point = new Point(-1, -1);
        gobang.history = new Stack<>();
        winner_start_point = new Point();
        winner_end_point = new Point();

        winner_line_stroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
    } //defInit

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
            board_spacing = w / (gobang.board_column + 1);
            board_left_x = board_spacing;
            board_top_y = (h - (gobang.board_row - 1) * board_spacing) / 2;
        } else {
            // 四周各留一个间隔
            board_spacing = h / (gobang.board_row + 1);
            board_top_y = board_spacing;
            board_left_x = (w - (gobang.board_column - 1) * board_spacing) / 2;
        }
        board_right_x = board_left_x + (gobang.board_column - 1) * board_spacing;
        board_bottom_y = board_top_y + (gobang.board_row - 1) * board_spacing;

        // 半径为间隔一半，再乘以0.85
        piece_radius = board_spacing * 85 / 200;
        board_special_spacing = board_spacing / 6;

        calculated = true;
    } //calculate

    /**
     * 绘制棋盘
     */
    private void drawBoard(Graphics2D g2d) {
        g2d.setStroke(board_stroke);
        //绘制背景色
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.BLACK);
        //绘制横线
        for (int i = 0; i < gobang.board_row; i++) {
            g2d.drawLine(board_left_x, board_top_y + i * board_spacing,
                    board_right_x, board_top_y + i * board_spacing);
        }

        //绘制竖线
        for (int i = 0; i < gobang.board_column; i++) {
            g2d.drawLine(board_left_x + i * board_spacing, board_top_y,
                    board_left_x + i * board_spacing, board_bottom_y);
        }

    } //drawBoard

    /**
     * 绘制棋子
     */
    private void drawPiece(Graphics2D g2d) {
        for (int i = 0; i < gobang.board_row; i++) {
            for (int j = 0; j < gobang.board_column; j++) {
                if (gobang.board_state[i][j] != null) {
                    g2d.setColor(gobang.board_state[i][j].getColor());
                    fillCircle(g2d, board_left_x + j * board_spacing,
                            board_top_y + i * board_spacing, piece_radius);
                }
            }
        }
    } //drawPiece

    private void fillCircle(Graphics2D g2d, int x, int y, int r) {
        int l = 2 * r;
        g2d.fillOval(x - r, y - r, l, l);
    } // fillCircle

    /**
     * 绘制选中位置
     */
    private void drawSelect(Graphics2D g2d) {
        if (gobang.isOutside(down_point.x, down_point.y)) {
            return;
        }
        g2d.setColor(Color.RED);
        int x, y, z;
        x = board_left_x + down_point.y * board_spacing;
        y = board_top_y + down_point.x * board_spacing;
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

    /**
     * 绘制赢家棋子连线
     */
    private void drawWinnerLine(Graphics2D g2d) {
        if (gobang.hasWinner) {
            g2d.setStroke(winner_line_stroke);
            g2d.drawLine(board_left_x + winner_start_point.y * board_spacing,
                    board_top_y + winner_start_point.x * board_spacing,
                    board_left_x + winner_end_point.y * board_spacing,
                    board_top_y + winner_end_point.x * board_spacing);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 创建 Graphics 的副本, 需要改变 Graphics 的参数,
        // 这里必须使用副本, 避免影响到 Graphics 原有的设置
        Graphics2D g2d = (Graphics2D) g.create();
        calculate();
        drawBoard(g2d);
        drawPiece(g2d);
        drawSelect(g2d);
        drawWinnerLine(g2d);
        // 自己创建的副本用完要销毁掉
        g2d.dispose();
    }

    /**
     * 触点坐标差转为行列
     */
    private int point2Row(int offset) {
        int s = offset / board_spacing;
        int y = offset % board_spacing;
        if (y > board_spacing / 2) {
            s++;
        }
        return s;
    } //point2Row

    @Override
    public void mouseClicked(MouseEvent event) {
        // 发起请求时无法落子
        if (frame.isRequesting) {
            return;
        }
        // 已诞生赢家的情况下不能再落子
        if (gobang.hasWinner) {
            return;
        }
        int x = event.getX(), y = event.getY();
        int rx, ry;

        // 横坐标转为列数
        ry = point2Row(x - board_left_x);
        // 纵坐标转为行数
        rx = point2Row(y - board_top_y);
        // 出界判断
        if (gobang.isOutside(rx, ry)) {
            return;
        }
        if (player == gobang.current && gobang.board_state[rx][ry] == null) {
            down_point.setLocation(rx, ry);
            Step msg = new Step();
            msg.point = new Point(down_point);
            updateFrom(msg, false);
            if (onClickListener != null) {
                onClickListener.onClick(msg);
            }
        }
    }

    /**
     * 更新步骤
     *
     * @param stp       步骤
     * @param fromOther 来自另一名玩家
     */
    public void updateFrom(Step stp, boolean fromOther) {
        Point p = stp.point;
        if (fromOther) {
            down_point.setLocation(p);
        }
        // 在该位置落子
        gobang.board_state[p.x][p.y] = gobang.current;
        Step step = new Step();
        step.boardState = gobang.current;
        step.point = new Point(down_point);
        // 存入历史记录
        gobang.history.push(step);

        // 赢家判定
        gobang.hasWinner = gobang.isWinner(p.x, p.y, winner_start_point, winner_end_point);
        if (gobang.hasWinner) {
            if (onWinnerListener != null) {
                onWinnerListener.onWinner(gobang.current);
            }
        } else if (gobang.history.size() == gobang.board_total) {
            // 和局判定
            if (onWinnerListener != null) {
                onWinnerListener.onWinner(null);
            }
        }
        repaint();

        if (onPieceDownListener != null) {
            onPieceDownListener.onPieceDown(down_point, gobang.current);
        }

        // 切换落子方
        gobang.switchCurrent();
        if (onPieceDownListener != null) {
            onPieceDownListener.afterPieceDown(gobang.current);
        }
    }

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
         * 参数x和y表示落子位置，bs为该子颜色
         */
        void onPieceDown(Point p, Gobang.BoardState bs);

        /**
         * bs为下次要落子的颜色
         */
        void afterPieceDown(Gobang.BoardState bs);
    }

    /**
     * 赢家监听器
     */
    public interface OnWinnerListener {
        /**
         * 产生赢家
         */
        void onWinner(Gobang.BoardState bs);
    }
} // GobangPanel
