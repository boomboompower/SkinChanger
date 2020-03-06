/*
 *     Copyright (C) 2020 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.do_you_like.mods.skinchanger.utils.backend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactory {

    private AtomicInteger threadNumber = new AtomicInteger(0); // The current ThreadCount

    private ExecutorService POOL; // Async task scheduler

    public ThreadFactory(String factoryName) {
        POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String.format("%s Thread %s", factoryName, this.threadNumber.incrementAndGet())));
    }

    /**
     * Runs a task async to the main thread
     *
     * @param runnable the runnable to run
     */
    public void runAsync(Runnable runnable) {
        this.POOL.execute(runnable);
    }
}
