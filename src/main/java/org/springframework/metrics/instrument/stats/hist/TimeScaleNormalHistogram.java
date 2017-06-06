/**
 * Copyright 2017 Pivotal Software, Inc.
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
package org.springframework.metrics.instrument.stats.hist;

import org.springframework.metrics.instrument.internal.TimeUtils;

import java.util.concurrent.TimeUnit;

import static org.springframework.metrics.instrument.internal.TimeUtils.convert;

public class TimeScaleNormalHistogram extends NormalHistogram<Double> {
    private final TimeUnit timeScale;

    public TimeScaleNormalHistogram(BucketFunction<? extends Double> f, TimeUnit timeScale) {
        super(f);
        this.timeScale = timeScale;
    }

    /**
     * @param shift The time scale of the new cumulative histogram
     * @return
     */
    public TimeScaleNormalHistogram shiftScale(TimeUnit shift) {
        if(shift.equals(timeScale))
            return this;
        return new TimeScaleNormalHistogram(new ScaledBucketFunction(shift), shift);
    }

    class ScaledBucketFunction implements BucketFunction<Double> {
        private TimeUnit shift;

        ScaledBucketFunction(TimeUnit shift) {
            this.shift = shift;
        }

        @Override
        public Double bucket(double d) {
            return convert(f.bucket(convert(d, shift, timeScale)), timeScale, shift);
        }
    }
}