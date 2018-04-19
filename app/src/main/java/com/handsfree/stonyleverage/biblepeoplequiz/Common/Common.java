package com.handsfree.stonyleverage.biblepeoplequiz.Common;

import com.handsfree.stonyleverage.biblepeoplequiz.Model.Questions;
import com.handsfree.stonyleverage.biblepeoplequiz.Model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 4/3/2018.
 */

public class Common {
    public static String CategoryId,CategoryName;
    public static User currentUser;
    public static List<Questions> questionList = new ArrayList<>();

    public static final String STR_PUSH = "pushNotification";
}
