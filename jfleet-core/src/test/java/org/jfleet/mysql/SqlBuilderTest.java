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
package org.jfleet.mysql;

import static org.junit.Assert.assertEquals;

import org.jfleet.EntityInfo;
import org.jfleet.FieldInfo;
import org.jfleet.mysql.SqlBuilder;
import org.junit.Test;

public class SqlBuilderTest {

	public EntityInfo buildEntity() {
		EntityInfo entityInfo = new EntityInfo();
		entityInfo.setTableName("simple_table");

		FieldInfo f1 = new FieldInfo();
		f1.setColumnName("column1");
		entityInfo.addField(f1);

		FieldInfo f2 = new FieldInfo();
		f2.setColumnName("column2");
		entityInfo.addField(f2);

		return entityInfo;
	}

	@Test
	public void testLoadDataIntoTable() {
		SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
		sqlBuilder.addLoadDataIntoTable();

		String sql = sqlBuilder.getSql();
		assertEquals("LOAD DATA LOCAL INFILE '' INTO TABLE simple_table ", sql);
	}

	@Test
	public void testFileConfig() {
		SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
		sqlBuilder.addFileConfig();

		String sql = sqlBuilder.getSql();
		// :( testing the same value
		assertEquals("CHARACTER SET UTF8 FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\n' STARTING BY '' ", sql);
	}

	@Test
	public void testColumnNames() {
		SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());
		sqlBuilder.addColumnNames();

		String sql = sqlBuilder.getSql();
		assertEquals("(`column1`, `column2`)", sql);
	}

	@Test
	public void testCompleteQuery() {
		SqlBuilder sqlBuilder = new SqlBuilder(buildEntity());

		String sql = sqlBuilder.build();
		String expectedSql = "LOAD DATA LOCAL INFILE '' INTO TABLE simple_table "
				+ "CHARACTER SET UTF8 FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\n' STARTING BY '' " 
				+ "(`column1`, `column2`)";
		assertEquals(expectedSql, sql);
	}

}
