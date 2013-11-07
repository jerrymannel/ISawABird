package com.isawabird.parse.extra;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.isawabird.Consts;

public class SyncUtils {

	/**
	 * Create an entry for this application in the system account list, if it isn't already there.
	 *
	 * @param context Context
	 */
	public static void createSyncAccount(Context context) {

		// Create account, if it's missing. (Either first run, or user has deleted account.)
		Account account = GenericAccountService.GetAccount();
		AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(account, null, null)) {
			// Inform the system that this account is eligible for auto sync when the network is up
			ContentResolver.setSyncAutomatically(account, Consts.AUTHORITY, true);
		}
	}
	
	 /**
     * Helper method to trigger an immediate sync ("refresh").
     *
     * <p>This should only be used when we need to preempt the normal sync schedule. Typically, this
     * means the user has pressed the "refresh" button.
     *
     * Note that SYNC_EXTRAS_MANUAL will cause an immediate sync, without any optimization to
     * preserve battery life. If you know new data is available (perhaps via a GCM notification),
     * but the user is not actively waiting for that data, you should omit this flag; this will give
     * the OS additional freedom in scheduling your sync request.
     */
    public static void triggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(),      // Sync account
                Consts.AUTHORITY, 						 // Content authority
                b);                                      // Extras
    }

}
