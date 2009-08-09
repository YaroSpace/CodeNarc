/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.ant

import org.apache.tools.ant.BuildException
import org.apache.tools.ant.Task
import org.apache.tools.ant.types.FileSet
import org.codenarc.AnalysisContext
import org.codenarc.analyzer.SourceAnalyzer
import org.codenarc.report.HtmlReportWriter
import org.codenarc.ruleset.CompositeRuleSet
import org.codenarc.ruleset.RuleSet
import org.codenarc.ruleset.XmlFileRuleSet
import org.apache.log4j.Logger
import org.codenarc.ruleset.PropertiesFileRuleSetConfigurer
import org.codenarc.CodeNarcRunner
import org.codenarc.results.Results

/**
 * Ant Task for running CodeNarc.
 * <p/>
 * The <code>ruleSetFiles</code> property specifies the path to the Groovy or XML RuleSet
 * definition files, relative to the classpath. This can be a single file path, or multiple
 * paths separated by commas. It is required.
 * <p/>
 * The <code>maxPriority1Violations</code> property specifies the maximum number of priority 1
 * violations allowed before failing the build (throwing a BuildException). Likewise,
 * <code>maxPriority2Violations</code> and <code>maxPriority3Violations</code> specifiy the
 * thresholds for violations of priority 2 and 3.
 * <p/>
 * The <code>fileset</code> nested element is required, and is used to specify the source files to be
 * analyzed. This is the standard Ant <i>FileSet</i>, and is quite powerful and flexible.
 * See the <i>Apache Ant Manual</i> for more information on <i>FileSets</i>. 
 * <p/>
 * The <ode>report</code> nested element defines the format and output file for the analysis report.
 * Currently, HTML ("html") is the only supported format. It includes <code>type</code>,
 * <code>toFile</code>, and <code>title</code> attributes.
 *
 * @see "http://ant.apache.org/manual/index.html"
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class CodeNarcTask extends Task {
    static final LOG = Logger.getLogger(CodeNarcTask)

    /**
     * The path to the Groovy or XML RuleSet definition files, relative to the classpath. This can be a
     * single file path, or multiple paths separated by commas.
     */
    String ruleSetFiles

    int maxPriority1Violations = Integer.MAX_VALUE
    int maxPriority2Violations = Integer.MAX_VALUE
    int maxPriority3Violations = Integer.MAX_VALUE

    protected List reportWriters = []
    protected FileSet fileSet
    protected ruleSet

    // Abstract creation of the CodeNarcRunner instance to allow substitution of test spy for unit tests
    protected createCodeNarcRunner = { return new CodeNarcRunner() }

    /**
     * Execute this Ant Task
     */
    void execute() throws BuildException {
        assert ruleSetFiles
        assert fileSet

        def sourceAnalyzer = createSourceAnalyzer()
        def codeNarcRunner = createCodeNarcRunner()
        codeNarcRunner.ruleSetFiles = ruleSetFiles
        codeNarcRunner.reportWriters = reportWriters
        codeNarcRunner.sourceAnalyzer = sourceAnalyzer

        def results = codeNarcRunner.execute()

        checkMaxViolations(results) 
    }

    void addFileset(FileSet fileSet) {
        assert fileSet
        if (this.fileSet) {
            throw new BuildException('The FileSet for this Task has already been set. Only a single FileSet is allowed.')
        }
        this.fileSet = fileSet
    }

    /**
     * Ant-defined method (by convention), called with each instance of a nested <report>
     * element within this task.
     */
    void addConfiguredReport(Report report) {
        if (report.type != 'html') {
            throw new BuildException("Invalid type: [$report.type]")
        }
        def reportWriter = new HtmlReportWriter(title:report.title)
        if (report.toFile) {
            reportWriter.outputFile = report.toFile 
        }
        LOG.debug("Adding report: $reportWriter")
        reportWriters << reportWriter
    }

    /**
     * Create and return the SourceAnalyzer
     * @return a configured SourceAnalyzer instance
     */
    protected SourceAnalyzer createSourceAnalyzer() {
        return new AntFileSetSourceAnalyzer(getProject(), fileSet)
    }

    private void checkMaxViolations(Results results) {
        def p1 = results.getNumberOfViolationsWithPriority(1, true)
        def p2 = results.getNumberOfViolationsWithPriority(2, true)
        def p3 = results.getNumberOfViolationsWithPriority(3, true)
        def countsText = "(p1=$p1; p2=$p2; p3=$p3)"

        checkMaxViolationForPriority(1, p1, countsText)
        checkMaxViolationForPriority(2, p2, countsText)
        checkMaxViolationForPriority(3, p3, countsText)
    }

    private void checkMaxViolationForPriority(int priority, int count, String countsText) {
        if (count > this."maxPriority${priority}Violations") {
            throw new BuildException("Exceeded maximum number of priority ${priority} violations: " + countsText)
        }
    }

}