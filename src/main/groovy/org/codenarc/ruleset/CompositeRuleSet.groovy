/*
 * Copyright 2009 the original author or authors.
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
package org.codenarc.ruleset

/**
 * A <code>RuleSet</code> implementation that aggregates a set of RuleSets.
 *
 * @author Chris Mair
 * @version $Revision: 193 $ - $Date: 2009-01-13 21:04:52 -0500 (Tue, 13 Jan 2009) $
 */
class CompositeRuleSet implements RuleSet {
    private rules = []

    void add(RuleSet ruleSet) {
        assert ruleSet != null
        rules.addAll(ruleSet.getRules())
    }

    /**
     * @return a List of Rule objects. The returned List is immutable.
     */
    List getRules() {
        return rules.asImmutable()
    }

}