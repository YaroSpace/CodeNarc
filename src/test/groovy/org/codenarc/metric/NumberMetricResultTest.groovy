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
package org.codenarc.metric

import org.codenarc.metric.abc.AbstractAbcTest

/**
 * Tests for NumberMetricResult
 *
 * @author Chris Mair
 * @version $Revision$ - $Date$
 */
class NumberMetricResultTest extends AbstractAbcTest {
    private static final METRIC = [:] as Metric

    void testPassingNullMetricIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('metric') { new NumberMetricResult(null, 1) }
    }

    void testPassingNullValueIntoConstructorThrowsException() {
        shouldFailWithMessageContaining('number') { new NumberMetricResult(METRIC, null) }
    }

    void testGetMetricIsSameIntegerValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, 23)
        assert result.getMetric() == METRIC
    }

    void testGetTotalValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, 23)
        assert result.getTotalValue() == 23
    }

    void testGetTotalValueIsSameBigDecimalValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, 0.23456)
        assert result.getTotalValue() == 0.23456
    }

    void testGetAverageValueIsSameIntegerValuePassedIntoConstructor() {
        def result = new NumberMetricResult(METRIC, 23)
        assert result.getAverageValue() == 23
    }

    void testGetCountIsOneForSingleValue() {
        def result = new NumberMetricResult(METRIC, 0.23456)
        assert result.getCount() == 1
    }

}