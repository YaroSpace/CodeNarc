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
package org.codenarc.rule

import org.codenarc.source.SourceCode

/**
 * Abstract superclass for Rules that use a Groovy AST Visitor.
 * <p/>
 * Each subclass must set the <code>astVisitorClass</code> property or else define a new
 * property with the same name, specifying the Class of the <code>AstVisitor</code>
 * to applied to the specified source code.
 *
 * @author Chris Mair
 * @version $Revision: 202 $ - $Date: 2009-01-16 21:27:36 -0500 (Fri, 16 Jan 2009) $
 */
abstract class AbstractAstVisitorRule extends AbstractRule {

    /** Each concrete subclass must either set this property or define its own property with the same name */
    Class astVisitorClass

    AstVisitor getAstVisitor() {
        def visitorClass = getAstVisitorClass()
        assert visitorClass, "The astVisitorClass property must not be null"
        assert AstVisitor.isAssignableFrom(visitorClass), "The astVisitorClass property must specify a class that implements AstVisitor"
        return visitorClass.newInstance()
    }

    void applyTo(SourceCode sourceCode, List violations) {
        // If AST is null, skip this source code
        def ast = sourceCode.ast
        if (ast) {
            ast.classes.each { classNode ->
                def visitor = getAstVisitor()
                visitor.rule = this
                visitor.sourceCode = sourceCode
                visitor.visitClass(classNode)
                violations.addAll(visitor.violations)
            }
        }
    }

}