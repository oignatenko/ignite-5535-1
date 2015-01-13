/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.apache.ignite.transactions;

import org.apache.ignite.*;

/**
 * Exception thrown whenever grid transaction enters an unknown state.
 * This exception is usually thrown whenever commit partially succeeds.
 * Cache will still resolve this situation automatically to ensure data
 * integrity, by invalidating all values participating in this transaction
 * on remote nodes.
 */
public class IgniteTxHeuristicException extends IgniteCheckedException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * Creates new heuristic exception with given error message.
     *
     * @param msg Error message.
     */
    public IgniteTxHeuristicException(String msg) {
        super(msg);
    }

    /**
     * Creates new heuristic exception with given error message and optional nested exception.
     *
     * @param msg Error message.
     * @param cause Optional nested exception (can be <tt>null</tt>).
     */
    public IgniteTxHeuristicException(String msg, Throwable cause) {
        super(msg, cause);
    }
}