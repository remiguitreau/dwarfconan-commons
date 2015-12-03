/**
 * 
 * Copyright (C) 2007 Rémi "DwarfConan" Guitreau
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */
package fr.dwarfconan.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Updater of DwarfConan
 *
 * @author Remi "DwarfConan" Guitreau
 * @since 9 sept. 07
 *
 */
public class DwarfConanUpdater implements Updater {

	private Map<String, String> updateInfos;
	private final PackageDownloader downloader;
	
	//---------------------------------------------------------
	// Constructors
	//---------------------------------------------------------
	public DwarfConanUpdater(final PackageDownloader downloader) {
		super();
		
		this.downloader = downloader;
	}

	//---------------------------------------------------------
	//	Implementation of Updater
	//---------------------------------------------------------
	public boolean checkForUpdate(final String applicationName, final String currentVersion) {
		try {
			updateInfos = retrieveUpdateInfos(new URL(UpdateConstants.UPDATE_INFOS_FILE_URL+applicationName+"/"+UpdateConstants.UPDATE_INFOS_NAME));
			if(updateInfos != null) {
				updateInfos.put(UpdateConstants.APPLICATION_INFO, applicationName);
			}
			return updateInfos != null
				&& updateInfos.get(UpdateConstants.VERSION_INFO) != null
				&& !currentVersion.equals(updateInfos.get(UpdateConstants.VERSION_INFO));
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void processUpdate() throws UpdateException {
		try {
			downloader.download(new URL(UpdateConstants.UPDATE_INFOS_FILE_URL+updateInfos.get(UpdateConstants.APPLICATION_INFO)+"/"+updateInfos.get(UpdateConstants.PACKAGE_INFO)),
					updateInfos.get(UpdateConstants.PACKAGE_INFO));
		}
		catch(Exception e) {
			throw new UpdateException(e.getMessage());
		}
	}
	
	//---------------------------------------------------------
	// Private
	//---------------------------------------------------------
	private Map<String, String> retrieveUpdateInfos(final URL url) throws IOException {
		final HashMap<String, String> updateInfos = new HashMap<String, String>();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String line = reader.readLine();
		while(line != null) {
			final String[] infos = line.split("=");
			updateInfos.put(infos[0], infos[1]);
			line = reader.readLine();
		}
		
		reader.close();
		
		return updateInfos;
	}
}
