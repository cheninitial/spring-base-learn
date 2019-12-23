/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.gradle.testkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.kotlin.cli.common.PropertiesKt;
import org.jetbrains.kotlin.compilerRunner.KotlinCompilerRunner;
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin;
import org.jetbrains.kotlin.gradle.plugin.KotlinPlugin;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.springframework.asm.ClassVisitor;
import org.springframework.boot.loader.tools.LaunchScript;
import org.springframework.util.FileCopyUtils;

/**
 * A {@link TestRule} for running a Gradle build using {@link GradleRunner}.
 *
 * @author Andy Wilkinson
 */
public class GradleBuild implements TestRule {

	private static final Pattern GRADLE_VERSION_PATTERN = Pattern.compile("\\[Gradle .+\\]");

	private final TemporaryFolder temp = new TemporaryFolder();

	private final Dsl dsl;

	private File projectDir;

	private String script;

	private String gradleVersion;

	public GradleBuild() {
		this(Dsl.GROOVY);
	}

	public GradleBuild(Dsl dsl) {
		this.dsl = dsl;
	}

	public Dsl getDsl() {
		return this.dsl;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		URL scriptUrl = findDefaultScript(description);
		if (scriptUrl != null) {
			script(scriptUrl.getFile());
		}
		return this.temp.apply(new Statement() {

			@Override
			public void evaluate() throws Throwable {
				before();
				try {
					base.evaluate();
				}
				finally {
					after();
				}
			}

		}, description);
	}

	private URL findDefaultScript(Description description) {
		URL scriptUrl = getScriptForTestMethod(description);
		if (scriptUrl != null) {
			return scriptUrl;
		}
		return getScriptForTestClass(description.getTestClass());
	}

	private URL getScriptForTestMethod(Description description) {
		String name = description.getTestClass().getSimpleName() + "-"
				+ removeGradleVersion(description.getMethodName()) + this.dsl.getExtension();
		return description.getTestClass().getResource(name);
	}

	private String removeGradleVersion(String methodName) {
		return GRADLE_VERSION_PATTERN.matcher(methodName).replaceAll("").trim();
	}

	private URL getScriptForTestClass(Class<?> testClass) {
		return testClass.getResource(testClass.getSimpleName() + this.dsl.getExtension());
	}

	private void before() throws IOException {
		this.projectDir = this.temp.newFolder();
	}

	private void after() {
		GradleBuild.this.script = null;
	}

	private List<File> pluginClasspath() {
		return Arrays.asList(new File("bin"), new File("build/classes/java/main"), new File("build/resources/main"),
				new File(pathOfJarContaining(LaunchScript.class)), new File(pathOfJarContaining(ClassVisitor.class)),
				new File(pathOfJarContaining(DependencyManagementPlugin.class)),
				new File(pathOfJarContaining(PropertiesKt.class)),
				new File(pathOfJarContaining(KotlinCompilerRunner.class)),
				new File(pathOfJarContaining(KotlinPlugin.class)),
				new File(pathOfJarContaining(KotlinGradleSubplugin.class)),
				new File(pathOfJarContaining(ArchiveEntry.class)));
	}

	private String pathOfJarContaining(Class<?> type) {
		return type.getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	public GradleBuild script(String script) {
		this.script = script.endsWith(this.dsl.getExtension()) ? script : script + this.dsl.getExtension();
		return this;
	}

	public BuildResult build(String... arguments) {
		try {
			return prepareRunner(arguments).build();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public BuildResult buildAndFail(String... arguments) {
		try {
			return prepareRunner(arguments).buildAndFail();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public GradleRunner prepareRunner(String... arguments) throws IOException {
		String scriptContent = FileCopyUtils.copyToString(new FileReader(this.script))
				.replace("{version}", getBootVersion())
				.replace("{dependency-management-plugin-version}", getDependencyManagementPluginVersion());
		FileCopyUtils.copy(scriptContent, new FileWriter(new File(this.projectDir, "build" + this.dsl.getExtension())));
		GradleRunner gradleRunner = GradleRunner.create().withProjectDir(this.projectDir)
				.withPluginClasspath(pluginClasspath());
		if (this.dsl != Dsl.KOTLIN) {
			// see https://github.com/gradle/gradle/issues/6862
			gradleRunner.withDebug(true);
		}
		if (this.gradleVersion != null) {
			gradleRunner.withGradleVersion(this.gradleVersion);
		}
		else if (this.dsl == Dsl.KOTLIN) {
			gradleRunner.withGradleVersion("4.10.3");
		}
		List<String> allArguments = new ArrayList<>();
		allArguments.add("-PbootVersion=" + getBootVersion());
		allArguments.add("--stacktrace");
		allArguments.addAll(Arrays.asList(arguments));
		return gradleRunner.withArguments(allArguments);
	}

	public File getProjectDir() {
		return this.projectDir;
	}

	public void setProjectDir(File projectDir) {
		this.projectDir = projectDir;
	}

	public GradleBuild gradleVersion(String version) {
		this.gradleVersion = version;
		return this;
	}

	public String getGradleVersion() {
		return this.gradleVersion;
	}

	private static String getBootVersion() {
		return evaluateExpression(
				"/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']" + "/text()");
	}

	private static String getDependencyManagementPluginVersion() {
		try (FileReader pomReader = new FileReader(".flattened-pom.xml")) {
			Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(pomReader));
			NodeList dependencyElements = pom.getElementsByTagName("dependency");
			for (int i = 0; i < dependencyElements.getLength(); i++) {
				Element dependency = (Element) dependencyElements.item(i);
				if (dependency.getElementsByTagName("artifactId").item(0).getTextContent()
						.equals("dependency-management-plugin")) {
					return dependency.getElementsByTagName("version").item(0).getTextContent();
				}
			}
			throw new IllegalStateException("dependency management plugin version not found");
		}
		catch (Exception ex) {
			throw new IllegalStateException("Failed to find dependency management plugin version", ex);
		}
	}

	private static String evaluateExpression(String expression) {
		try (FileReader pomReader = new FileReader(".flattened-pom.xml")) {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expr = xpath.compile(expression);
			String version = expr.evaluate(new InputSource(pomReader));
			return version;
		}
		catch (Exception ex) {
			throw new IllegalStateException("Failed to evaluate expression", ex);
		}
	}

}
