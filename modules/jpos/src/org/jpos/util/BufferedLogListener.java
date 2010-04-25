/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2010 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.util;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class BufferedLogListener implements LogListener, Configurable, LogProducer {
    int maxSize;
    String name;
    public static final int DEFAULT_SIZE = 100;
    List<LogListener> listeners = Collections.synchronizedList (new ArrayList<LogListener>());
    List<LogEvent> events = Collections.synchronizedList (new ArrayList<LogEvent>());

    public LogEvent log(LogEvent ev) {
        events.add (new FrozenLogEvent (ev));
        while (events.size() > maxSize)
            events.remove(0);
        notifyListeners (ev);
        return ev;
    }
    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        maxSize = cfg.getInt ("size", DEFAULT_SIZE);
        if (name != null)
            NameRegistrar.unregister (name);
        name = cfg.get ("name", null);
        if (name != null)
            NameRegistrar.register (name, this);
    }
    public void addListener (final LogListener listener) {
        for (LogEvent ev : events) {
            listener.log(ev);
        }
        listeners.add (listener);
    }
    public void removeListener (LogListener listener) {
        listeners.remove (listener);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public int getMaxSize() {
        return maxSize;
    }
    private void notifyListeners (LogEvent evt) {
        for (LogListener listener : listeners) {
            listener.log(evt);
        }
    }
}