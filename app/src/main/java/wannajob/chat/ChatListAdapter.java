package wannajob.chat;

/**
 * Created by Txuso on 24/02/16.
 */
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.txuso.wannajob.R;
import com.firebase.client.Query;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>IndividualChat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<IndividualChat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String userID;
    private String toID;
    private TextView CHAT_TXT;



    public ChatListAdapter(Query ref, Activity activity, int layout, String authorID, String toID) {
        super(ref, IndividualChat.class, layout, activity);

        this.userID = authorID;
        this.toID = toID;
    }


    /**
     * Bind an instance of the <code>IndividualChat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>IndividualChat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, IndividualChat chat) {
        CHAT_TXT = (TextView)view.findViewById(R.id.message);

        // Map a IndividualChat object to an entry in our listview
        LinearLayout singleMessageContainer = (LinearLayout) view.findViewById(R.id.singleMessageContainer);
        CHAT_TXT.setText(chat.getAuthor() + "\n" + chat.getMessage() + "  -  " + chat.getDate());

        if (chat.getAuthorID().equals(userID) && chat.getReceptorID().equals(toID)){
            CHAT_TXT.setBackgroundResource(R.drawable.right);
            singleMessageContainer.setGravity(Gravity.RIGHT);
        }
        else
        if (chat.getAuthorID().equals(toID) && chat.getReceptorID().equals(userID)){
            CHAT_TXT.setBackgroundResource(R.drawable.left);
            singleMessageContainer.setGravity(Gravity.LEFT);

        }



    }
}