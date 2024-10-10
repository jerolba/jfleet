/**
 * Copyright 2022 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.common;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.jfleet.JFleetException;

public class ParallelContentWriter implements ContentWriter {

    private final Executor executor;
    private final ContentWriter contentWriter;
    private Future<Exception> last = null;

    public ParallelContentWriter(Executor executor, ContentWriter contentWriter) {
        if (executor == null) {
            this.executor = ForkJoinPool.commonPool();
        } else {
            this.executor = executor;
        }
        this.contentWriter = contentWriter;
    }

    @Override
    public void writeContent(StringContent stringContent) throws SQLException, JFleetException {
        waitForWrite();
        last = CompletableFuture.supplyAsync(() -> {
            try {
                contentWriter.writeContent(stringContent);
                return null;
            } catch (SQLException | JFleetException e) {
                return e;
            }
        }, executor);
    }

    @Override
    public void waitForWrite() throws SQLException, JFleetException {
        if (last != null) {
            try {
                rethrow(last.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void rethrow(Exception e) throws SQLException, JFleetException {
        if (e != null) {
            if (e instanceof SQLException) {
                throw (SQLException) e;
            }
            if (e instanceof JFleetException) {
                throw (JFleetException) e;
            }
        }
    }

}
