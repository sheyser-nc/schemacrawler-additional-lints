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


/**
 * Check columns with JSON content but not JSON or JSONB type
 * @author mbarre
 * @since 1.0.1
 */
public class LinterXmlContentTest extends BaseLintTest {

	private static final String CHANGE_LOG_XML_CHECK = "src/test/db/liquibase/LinterXmlContent/db.changelog.xml";
	private static PostgreSqlDatabase database;

	@BeforeClass
	public static void  init(){
		database = new PostgreSqlDatabase();
		database.setUp(CHANGE_LOG_XML_CHECK);
	}

	@Test
	public void testLint() throws Exception{

		final LinterRegistry registry = new LinterRegistry();
		Assert.assertTrue(registry.hasLinter(LinterXmlContent.class.getName()));

		final SchemaCrawlerOptions options = new SchemaCrawlerOptions();
		options.setSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
		options.setTableNamePattern("test_xml");

		Connection connection = DriverManager.getConnection(PostgreSqlDatabase.CONNECTION_STRING,
				PostgreSqlDatabase.USER_NAME, database.getPostgresPassword());

		List<LintWrapper> lints = executeToJsonAndConvertToLintList(LinterXmlContent.class.getSimpleName(), options, connection);
		Assert.assertEquals(1,lints.size());
		int index = 0;
		Assert.assertEquals(LinterXmlContent.class.getName(), lints.get(index).getId());
		Assert.assertEquals("public.test_xml.content", lints.get(index).getValue());
		Assert.assertEquals("should be XML type", lints.get(index).getDescription());
		Assert.assertEquals("high", lints.get(index).getSeverity());

	}

}
