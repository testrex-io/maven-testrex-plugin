package io.testrex.plugin;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestrexMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void testResourceTestProjectHasPomWithCorrectPlugin() throws Exception {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        TestrexMojo testrexMojo = (TestrexMojo) rule.lookupConfiguredMojo(pom, "send");
        assertNotNull(testrexMojo);
    }
}

