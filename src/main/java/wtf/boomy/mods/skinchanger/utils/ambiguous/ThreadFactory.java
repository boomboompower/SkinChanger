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

package wtf.boomy.mods.skinchanger.utils.ambiguous;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple class to handle running code async to the main thread.
 *
 * @author boomboompower
 * @version 1.1
 * @since 1.0.0
 */
public class ThreadFactory {
    
    // The current ThreadCount
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    
    // Async task scheduler
    private final ExecutorService POOL;
    
    // Stores if this factory is dead
    private boolean shutdown = false;
    
    /**
     * Creates a new Thread Factory
     *
     * @param factoryName the name of this factory.
     */
    public ThreadFactory(String factoryName) {
        POOL = Executors.newFixedThreadPool(8, r -> new Thread(r, String.format("%s Thread %s", factoryName, this.threadNumber.incrementAndGet())));
    }
    
    /**
     * Runs a task async to the main thread
     *
     * @param runnable the runnable to run
     */
    public void runAsync(Runnable runnable) {
        if (this.shutdown) {
            throw new IllegalStateException("ThreadFactory has been destroyed.");
        }
        
        this.POOL.execute(runnable);
    }
    
    /**
     * Terminates this ThreadFactory and kills the ExecutorService
     */
    public void destroy() {
        if (this.shutdown) {
            return;
        }
        
        this.shutdown = true;
        
        this.POOL.shutdown();
    }
    
    /**
     * Returns true if this factory has been shutdown
     *
     * @return true if this ThreadFactory is dead.
     */
    public boolean isShutdown() {
        return this.shutdown;
    }
}
