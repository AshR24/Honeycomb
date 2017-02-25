package com.honeycomb;

import com.honeycomb.helper.Database.objects.User;

import java.util.ArrayList;

import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Ash on 24/02/2017.
 */

public class Subjects
{
    public static final BehaviorSubject<User> SUBJECT_CURRENT_USER = BehaviorSubject.create();
    public static final BehaviorSubject<ArrayList<User>> SUBJECT_ALL_USERS = BehaviorSubject.create();
}
