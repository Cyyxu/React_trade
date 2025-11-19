package com.xyes.springboot.manager.model;



import com.xyes.springboot.manager.model.response.SparkResponseFunctionCall;
import com.xyes.springboot.manager.model.response.SparkTextUsage;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * SparkTextChatResponse
 */
public class SparkSyncChatResponse implements Serializable {
    private static final long serialVersionUID = -6785055441385392782L;
    /**
     * 回答内容
     */
    private String content;

    private SparkResponseFunctionCall functionCall;
    /**
     * tokens统计
     */
    private SparkTextUsage textUsage;
    private CountDownLatch latch;

    /**
     * 内部自用字段
     */
    private boolean ok = false;

    private Throwable exception;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SparkResponseFunctionCall getFunctionCall() {
        return functionCall;
    }

    public void setFunctionCall(SparkResponseFunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    public SparkTextUsage getTextUsage() {
        return textUsage;
    }

    public void setTextUsage(SparkTextUsage textUsage) {
        this.textUsage = textUsage;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
