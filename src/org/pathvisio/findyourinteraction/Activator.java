//  FindYourInteraction Plugin for PathVisio
//	Find metabolic reaction from Rhea
//	Copyright 2016-2018 BiGCaT, Maastricht University
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package org.pathvisio.findyourinteraction;

/**
 * OSGI activator of the plugin
 * @author anwesha, jonathan, mkutmon
 */
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.plugin.Plugin;

public class Activator implements BundleActivator {
	private FindYourInteractionPlugin plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = new FindYourInteractionPlugin();
		context.registerService(Plugin.class.getName(), plugin, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin.done();
	}
}
