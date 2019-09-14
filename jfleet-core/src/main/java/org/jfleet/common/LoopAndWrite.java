/**
 * Copyright 2017 Jerónimo López Bezanilla
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
import java.util.Iterator;
import java.util.stream.Stream;

import org.jfleet.JFleetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoopAndWrite {

    private static Logger logger = LoggerFactory.getLogger(LoopAndWrite.class);

    private final JFleetBatchConfig config;
    private final ContentWriter contentWriter;
    private final EntityRowBuilder rowBuilder;

    public LoopAndWrite(JFleetBatchConfig config, ContentWriter contentWriter, EntityRowBuilder rowBuilder) {
        this.config = config;
        this.contentWriter = contentWriter;
        this.rowBuilder = rowBuilder;
    }

    public <T> void go(Stream<T> stream) throws SQLException, JFleetException {
        ContentWriter writer = config.isConcurrent() ? new ParallelContentWriter(config.getExecutor(), contentWriter)
                : contentWriter;
        ContentBuilder contentBuilder = new ContentBuilder(rowBuilder, config.getBatchSize(), config.isConcurrent());
        Iterator<T> iterator = stream.iterator();
        while (iterator.hasNext()) {
            contentBuilder.add(iterator.next());
            if (contentBuilder.isFilled()) {
                logger.debug("Writing content");
                writer.writeContent(contentBuilder.getContent());
                contentBuilder.reset();
            }
        }
        logger.debug("Flushing content");
        writer.writeContent(contentBuilder.getContent());
        writer.waitForWrite();
        contentBuilder.reset();
    }

}
