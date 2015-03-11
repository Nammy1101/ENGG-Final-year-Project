package com.example.urbookproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnMatchResultsListItemClickListener implements AdapterView.OnItemClickListener, IAsyncHttpHandler {
    private final int MAX_ADDRESSES = 1;
    private Context context;
    private MatchResultsBaseAdapter adapter;
    private String subject, body;
    private String[] address;

    public OnMatchResultsListItemClickListener(MatchResultsBaseAdapter adapter) {
        this.adapter = adapter;
        this.address = new String[MAX_ADDRESSES];
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
        Intent intent = new Intent(view.getContext(), MatchedUserInformation.class);
        intent.putExtra("matchedUserID", ((BookDataMatch) adapter.getItem(position)).getUserID());
        view.getContext().startActivity(intent);
        */

        String transactionType = ((BookDataMatch) adapter.getItem(position)).getTransactionType();
        context = view.getContext();
        subject = "[urBooks]: ";
        body = "Hello " + ((BookDataMatch) adapter.getItem(position)).getUsername() + ",\n";

        if (transactionType.equals("trade")) {
            subject += "TRADE";

            body += "I would like to trade...\n";
            body += "MY: [";
            body+= ((BookDataMatch) adapter.getItem(position)).getOutgoingBook().getTitle();
            body += "]\n";

            body += "for YOUR: [";
            body += ((BookDataMatch) adapter.getItem(position)).getIncomingBook().getTitle();
            body += "]\n";
        } else if (transactionType.equals("sell")) {
            subject += "SALE";

            body += "I would like to sell...\n";
            body += "MY: [";
            body += ((BookDataMatch) adapter.getItem(position)).getOutgoingBook().getTitle();
            body += "]\n";

            body += "for: $";
            body += ((BookDataMatch) adapter.getItem(position)).getPrice() + "\n";
        } else if (transactionType.equals("buy")) {
            subject += "PURCHASE";

            body += "I would like to purchase...\n";
            body += "YOUR: [";
            body += ((BookDataMatch) adapter.getItem(position)).getIncomingBook().getTitle();
            body += "]\n";

            body += "for: $";
            body += ((BookDataMatch) adapter.getItem(position)).getPrice() + "\n";
        } else {
            Toast.makeText(context.getApplicationContext(), "Something went wrong...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        body += "Regards,\n" +
                ((MyAppUserData) context.getApplicationContext()).getUserData().getUserName();

        subject += " offer from ";
        subject += ((MyAppUserData) context.getApplicationContext()).getUserData().getUserName();

        String url = context.getString(R.string.server_url) + "MatchedUserInformation.php";
        HttpPostAsyncTask task = new HttpPostAsyncTask(context,
                OnMatchResultsListItemClickListener.this);

        task.execute(url,
                "matched_user_id", ((BookDataMatch) adapter.getItem(position)).getUserID());
    }

    @Override
    public void onPostExec(String json) {
        try {
            JSONObject jsonResponse = new JSONObject(json);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("table_data");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                address[0] = jsonChildNode.optString("matched_email").trim();
            }

        } catch (JSONException e) {
            Toast.makeText(context.getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        /* Send email on-click */
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
