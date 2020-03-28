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

package me.do_you_like.mods.skinchanger.utils.installing;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Detects if the client has access to the Internet or not
 *
 * @since 3.0.0
 * @version 1.0
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class InternetConnection {

    private InternetConnection() {
    }

    /**
     * Detects an internet connection by trying to connect to Google.
     * The client has 10 seconds to make a connection before the assumption is made
     *
     * @return true if the client can connect to Google.
     */
    public static boolean hasInternetConnection() {
        try {
            URL url = new URL("https://google.com");

            URLConnection connection = url.openConnection();

            connection.setConnectTimeout(10000);

            connection.connect();

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
