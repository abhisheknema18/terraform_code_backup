package com.amtsybex.fieldreach.services.messages.response;

import java.io.Serializable;

import com.amtsybex.fieldreach.services.messages.types.ErrorMessage;
import com.amtsybex.fieldreach.services.messages.types.FullDataBaseMessage;

public abstract class DatabaseResponse implements Serializable {
    private static final long serialVersionUID = -3895863319338774559L;

    private ErrorMessage error;
    private FullDataBaseMessage full;
    private boolean success;

    /**
     * Get the 'error' element value.
     *
     * @return value
     */
    public ErrorMessage getError() {
        return error;
    }

    /**
     * Set the 'error' element value.
     *
     * @param error
     */
    public void setError(ErrorMessage error) {
        this.error = error;
    }

    /**
     * Get the 'full' element value.
     *
     * @return value
     */
    public FullDataBaseMessage getFull() {
        return full;
    }

    /**
     * Set the 'full' element value.
     *
     * @param full
     */
    public void setFull(FullDataBaseMessage full) {
        this.full = full;
    }

    /**
     * Get the 'success' element value.
     *
     * @return value
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set the 'success' element value.
     *
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
