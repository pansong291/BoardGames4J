package game.logic;

import java.io.Serializable;

/**
 * 步
 *
 * @author fanhuan
 * @date 2020/11/17
 */
public class Step implements Serializable {
    private static final long serialVersionUID = 5969195736000099036L;
    /**
     * 请求类型
     */
    public RequestType requestType;
    /**
     * 回应类型
     */
    public ResponseType responseType;

    public Step() {
    }

    public Step(Step s) {
        requestType = s.requestType;
        responseType = s.responseType;
    }
} // Step
