package edu.udacity.faraonc.fullstackquizapp;

import android.content.res.Resources;
import android.util.Log;
import android.content.Context;
import java.lang.reflect.Field;

/**
 * Created by faraonc on 12/13/17.
 */

class OSQuizMaker extends AbstractQuizMaker {

    OSQuizMaker(Context context) {
        init();
        Resources resource = context.getResources();
        Field[] fields = R.array.class.getFields();

        for (final Field field : fields) {
            try {
                String question[] = resource.getStringArray(field.getInt(R.string.class));
                processStringArrayQuestion(question);

            } catch (Exception ex) {
                Log.e(context.getString(R.string.os_constructor_error), ex.getMessage());
            }

        }
    }

}
