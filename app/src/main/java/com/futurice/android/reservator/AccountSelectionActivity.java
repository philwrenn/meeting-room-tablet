package com.futurice.android.reservator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.accounts.Account;
import android.accounts.AccountManager;

import java.util.List;
import java.util.ArrayList;


public class AccountSelectionActivity extends ReservatorActivity {
    static final int REQUEST_LOBBY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_selection);
        selectAccount();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectAccount();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void moveToModeSelection() {
        Intent i = new Intent(this, ModeSelectionActivity.class);
        startActivityForResult(i, REQUEST_LOBBY);
    }

    public String[] fetchAccounts() {
        List<String> accountsList = new ArrayList<String>();
        for (Account account : AccountManager.get(this).getAccounts()) {
                accountsList.add(account.name);
        }
        return accountsList.toArray(new String[accountsList.size()]);
    }

    public void selectAccount() {
        final SharedPreferences preferences = getSharedPreferences(this.getString(R.string.PREFERENCES_NAME), Context.MODE_PRIVATE);
        final String selectedAccount = preferences.getString(getString(R.string.accountForServation), "");
        boolean addressBookOption = preferences.getBoolean("addressBookOption", false);

        if (selectedAccount == "" && !addressBookOption) {
            final String[] values = fetchAccounts();

            // Only one Google account available so the selection isn't needed.
            if (values.length == 1) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(getString(R.string.accountForServation), values[0])
                        .apply();

                edit.putString(
                        getString(R.string.PREFERENCES_ACCOUNT),
                        values[0]);
                edit.apply();
                moveToModeSelection();
            } else {

                // Build an alert dialog to select the account.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.selectAccount);
                builder.setItems(values, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString(getString(R.string.accountForServation), values[which]);
                        edit.putString(getString(R.string.PREFERENCES_ACCOUNT_TYPE),
                                values[which].substring(values[which].indexOf("@") + 1, values[which].length()));
                        edit.putString(
                                getString(R.string.PREFERENCES_ACCOUNT),
                                values[which]);
                        edit.apply();
                        moveToModeSelection();
                    }
                });

                builder.show();
            }
        } else {
            moveToModeSelection();
        }
    }
}
