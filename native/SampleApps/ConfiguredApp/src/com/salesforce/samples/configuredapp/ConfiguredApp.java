/*
 * Copyright (c) 2014, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.samples.configuredapp;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.RestrictionEntry;
import android.content.RestrictionsManager;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.auth.LoginServerManager;
import com.salesforce.samples.configuredapp.ui.MainActivity;

/**
 * Application class for our application.
 */
public class ConfiguredApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		SalesforceSDKManager.initNative(getApplicationContext(), new KeyImpl(),
				MainActivity.class);
		SalesforceSDKManager.getInstance().setLoginServerManager(new LoginServerManagerUsingWorkProfile(getApplicationContext()));
	}
	
	public static class LoginServerManagerUsingWorkProfile extends LoginServerManager {

		public LoginServerManagerUsingWorkProfile(Context ctx) {
			super(ctx);
			
			String loginHostFromRestrictions = getLoginHostFromRestrictions(ctx);;
			if (loginHostFromRestrictions != null) {
				addCustomLoginServer("Profile server", loginHostFromRestrictions);
				setSelectedLoginServer(getLoginServerFromURL(loginHostFromRestrictions));
				
		        Toast.makeText(ctx, loginHostFromRestrictions + " read from profile", Toast.LENGTH_SHORT).show();
			}
			else {
		        Toast.makeText(ctx, "No login host found in profile", Toast.LENGTH_SHORT).show();
			}
		}

		private String getLoginHostFromRestrictions(Context ctx) {
			RestrictionsManager restrictionsManager =
	                (RestrictionsManager) ctx.getSystemService(Context.RESTRICTIONS_SERVICE);
	        List<RestrictionEntry> restrictions =
	                restrictionsManager.getManifestRestrictions("com.salesforce.samples.configuredapp");
	        if (restrictions != null) {
		        for (RestrictionEntry restriction : restrictions) {
		        	// XXX hard-coded constant
		            if ("login_host".equals(restriction.getKey())) {
		                return restriction.getSelectedString();
		            }
		        }
	        }			
	        
	        return null;
		}
		
		
	}
}


