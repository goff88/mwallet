/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package pro.devapp.mwallet.core.main;

import java.util.concurrent.atomic.AtomicInteger;

import pro.devapp.mwallet.core.Convert;

public interface Time {

    int getTime();

    final class EpochTime implements Time {

        public int getTime() {
            return Convert.toEpochTime(System.currentTimeMillis());
        }

    }

    final class ConstantTime implements Time {

        private final int time;

        public ConstantTime(int time) {
            this.time = time;
        }

        public int getTime() {
            return time;
        }

    }

    final class FasterTime implements Time {

        private final int multiplier;
        private final long systemStartTime;
        private final int time;

        public FasterTime(int time, int multiplier) {
            if (multiplier > 1000 || multiplier <= 0) {
                throw new IllegalArgumentException("Time multiplier must be between 1 and 1000");
            }
            this.multiplier = multiplier;
            this.time = time;
            this.systemStartTime = System.currentTimeMillis();
        }

        public int getTime() {
            return time + (int)((System.currentTimeMillis() - systemStartTime) / (1000 / multiplier));
        }

    }

    final class CounterTime implements Time {

        private final AtomicInteger counter;

        public CounterTime(int time) {
            this.counter = new AtomicInteger(time);
        }

        public int getTime() {
            return counter.incrementAndGet();
        }

    }

}
