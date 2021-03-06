package io.github.mbarre.schemacrawler.tool.linter;

/*
 * #%L
 * Additional SchemaCrawler Lints
 * %%
 * Copyright (C) 2015 - 2016 github
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import io.github.mbarre.schemacrawler.test.utils.LintWrapper;
import io.github.mbarre.schemacrawler.test.utils.PostgreSqlDatabase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.lint.LinterRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Objects;


/**
 * Check tables and columns name
 * @author mbarre
 * @since
 */
public class LinterNoSpaceInNamesTest extends BaseLintTest {

	private static final String CHANGE_LOG_XML_CHECK = "src/test/db/liquibase/LinterNoSpaceInNames/db.changelog.xml";
	private static PostgreSqlDatabase database;

	@BeforeClass
	public static void  init(){
		database = new PostgreSqlDatabase();
		database.setUp(CHANGE_LOG_XML_CHECK);
	}

	@Test
	public void testLint() throws Exception{

		final LinterRegistry registry = new LinterRegistry();
		Assert.assertTrue(registry.hasLinter(LinterNoSpaceInNames.class.getName()));

		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());

		Connection connection = DriverManager.getConnection(PostgreSqlDatabase.CONNECTION_STRING,
				PostgreSqlDatabase.USER_NAME, database.getPostgresPassword());

		List<LintWrapper> lints = executeToJsonAndConvertToLintList(LinterNoSpaceInNames.class.getSimpleName(), options, connection);
		Assert.assertEquals(2,lints.size());
		for (int i=0; i<lints.size();i++) {
            Assert.assertEquals(LinterNoSpaceInNames.class.getName(), lints.get(i).getId());
            Assert.assertEquals("Space should not be used in table or column names", lints.get(i).getDescription());
            Assert.assertEquals("high", lints.get(i).getSeverity());
        }
        Assert.assertTrue(contains(lints, "public.\"table 1\""));
        Assert.assertTrue(contains(lints, "public.\"table 1\".\"more content\""));

    }

	private boolean contains(List<LintWrapper> lints, String columnName){
		return lints.stream().anyMatch(lint -> Objects.equals(lint.getValue(), columnName));
	}

}
